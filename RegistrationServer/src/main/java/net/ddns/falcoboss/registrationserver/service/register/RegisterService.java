package net.ddns.falcoboss.registrationserver.service.register;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.security.auth.login.LoginException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.ddns.falcoboss.common.cryptography.KeyHelper;
import net.ddns.falcoboss.common.cryptography.PublicKeyCryptography;
import net.ddns.falcoboss.common.transport.objects.HTTPHeaderNames;
import net.ddns.falcoboss.common.transport.objects.KeyPairTO;
import net.ddns.falcoboss.common.transport.objects.MessageTO;
import net.ddns.falcoboss.common.transport.objects.PartiallySignatureTO;
import net.ddns.falcoboss.common.transport.objects.UsernameAndPasswordTO;
import net.ddns.falcoboss.registrationserver.rest.client.MediatorRestClient;
import net.ddns.falcoboss.registrationserver.security.AuthenticatorBean;
import net.ddns.falcoboss.registrationserver.usermanagement.User;
import net.ddns.falcoboss.registrationserver.usermanagement.UserBean;

@Stateless(name = "RegisterService", mappedName = "ejb/RegisterService")
public class RegisterService implements RegisterServiceProxy {

	private static final long serialVersionUID = -6663599014192066936L;

	//private static final int _ThreadPool = 10;
	//private ExecutorService writer = Executors.newFixedThreadPool(_ThreadPool);

	private List<MessageTO> recivedMessages = new LinkedList<MessageTO>();

	private HashMap<String, AsyncResponse> listeners = new HashMap<String, AsyncResponse>();

	@EJB
	private AuthenticatorBean authenticatorBean;

	@EJB
	private UserBean userBean;

	@Override
	public Response login(@Context HttpHeaders httpHeaders, final UsernameAndPasswordTO usernameAndPasswordBean) {
		String serviceKey = httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY);
		String username = usernameAndPasswordBean.getUsername();
		String password = usernameAndPasswordBean.getPassword();
		try {
			String authToken = authenticatorBean.login(serviceKey, username, password);
			JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
			jsonObjBuilder.add("auth_token", authToken);
			JsonObject jsonObj = jsonObjBuilder.build();
			User requestUser = userBean.findByServiceKey(serviceKey);
			String requestUsername = requestUser.getUsername();
			synchronized (listeners) {
				listeners.remove(requestUsername);
			}
			return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
		} catch (final LoginException ex) {
			JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
			jsonObjBuilder.add("message", "Problem matching service key, username and password");
			JsonObject jsonObj = jsonObjBuilder.build();
			return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).entity(jsonObj.toString()).build();
		}
	}

	@Override
	public Response sendMessage(@Context HttpHeaders httpHeaders, final MessageTO message) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean messageSend = false;
				message.setSendDate(new Date());
				synchronized (recivedMessages) {
					synchronized (listeners) {
						for (Entry<String, AsyncResponse> entry : listeners.entrySet()) {
							String listenerRecipient = entry.getKey();
							String messageRecipient = message.getRecipient();
							if (listenerRecipient.equals(messageRecipient)) {
								try {
									send(entry.getValue(), message);
									messageSend = true;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						if (!messageSend) {
							recivedMessages.add(message);
							recivedMessages.sort(new MessageTO());
						}
					}
				}
			}
		}).start();
		JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
		jsonObjBuilder.add("message", "Executed sendMessage");
		JsonObject jsonObj = jsonObjBuilder.build();
		return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
	}

	@Override
	public void reciveMessage(@Context HttpHeaders httpHeaders, @Suspended AsyncResponse asyncResponse) {

		String serviceKey = httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY);
		User requestUser = userBean.findByServiceKey(serviceKey);
		String requestUsername = requestUser.getUsername();
		boolean messageFound = false;
		synchronized (recivedMessages) {
			for (MessageTO message : recivedMessages) {
				if (message.getRecipient().equals(requestUsername)) {
					send(asyncResponse, message);
					recivedMessages.remove(message);
					messageFound = true;
					break;
				}
			}
			if (!messageFound) {
				synchronized (listeners) {
					listeners.put(requestUsername, asyncResponse);
				}
			}
		}
	}

	@Override
	public Response logout(@Context HttpHeaders httpHeaders) {
		try {
			String serviceKey = httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY);
			String authToken = httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN);
			authenticatorBean.logout(serviceKey, authToken);
			User requestUser = userBean.findByServiceKey(serviceKey);
			String requestUsername = requestUser.getUsername();
			synchronized (listeners) {
				listeners.remove(requestUsername);
			}
			listeners.remove(requestUsername);
			return getNoCacheResponseBuilder(Response.Status.NO_CONTENT).build();
		} catch (final GeneralSecurityException ex) {
			return getNoCacheResponseBuilder(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	private Response.ResponseBuilder getNoCacheResponseBuilder(Response.Status status) {
		CacheControl cc = new CacheControl();
		cc.setNoCache(true);
		cc.setMaxAge(-1);
		cc.setMustRevalidate(true);
		return Response.status(status).cacheControl(cc);
	}

	@Override
	public void requestNewKey(@Context HttpHeaders httpHeaders, @Suspended AsyncResponse asyncResponse,
		UsernameAndPasswordTO usernameAndPasswordBean) {
		String serviceKey = httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY);
		String username = usernameAndPasswordBean.getUsername();
		String password = usernameAndPasswordBean.getPassword();
		MediatorRestClient mediatorRestClient = new MediatorRestClient();
		mediatorRestClient.setWebTarget("http://localhost:8080/MediatorServer/rest/service/"); 
		
		try {
			authenticatorBean.isUsernameAndPasswordValid(serviceKey, username, password);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						KeyPair newKeyPair = PublicKeyCryptography.createKeyPair();
						RSAPublicKey publicKey = (RSAPublicKey) newKeyPair.getPublic();
						PrivateKey privateKey = newKeyPair.getPrivate();
						String publicKeyBase64String = KeyHelper.getBase64StringFromPublicKey(publicKey);
						
						Future<Response> futureResponse = mediatorRestClient.requestNewFinalizationKey(serviceKey, publicKeyBase64String);
						Response response = futureResponse.get();
						if(response.getStatus() == 200){
							String mediatorPrivateExponentBase64String = response.readEntity(String.class);
							BigInteger mediatorPrivateExponent = KeyHelper.getBigIntegerFromBase64String(mediatorPrivateExponentBase64String);
							RSAPrivateKey userPrivateKey = (RSAPrivateKey) PublicKeyCryptography.calculateUserPrivateKey(privateKey, mediatorPrivateExponent);
							KeyPairTO keyPairTO = new KeyPairTO();
							keyPairTO.setModulus(KeyHelper.getBase64StringFromBigInteger(userPrivateKey.getModulus()));
							keyPairTO.setPrivateExponent(KeyHelper.getBase64StringFromBigInteger(userPrivateKey.getPrivateExponent()));
							keyPairTO.setPublicExponent(KeyHelper.getBase64StringFromBigInteger(publicKey.getPublicExponent()));
//							List<String> keyStringList = Arrays.asList(userPrivateKeyBase64String, userPublicKeyBase64String);
//							GenericEntity<List<String>> genericEntity = new GenericEntity<List<String>>(keyStringList) {};
							Response mediatorResponse = Response.ok(keyPairTO, MediaType.APPLICATION_JSON).build();
			                asyncResponse.resume(mediatorResponse);
						}
						else
						{
							asyncResponse(asyncResponse, "Mediator Unknown Error", Response.Status.INTERNAL_SERVER_ERROR);
						}
						
						
					} catch (Exception e) {
						e.printStackTrace();
						e.printStackTrace();
						asyncResponse(asyncResponse, "Unknown Error", Response.Status.INTERNAL_SERVER_ERROR);
					}
				}
			}).start();
		} catch (LoginException e) {
			asyncResponse(asyncResponse, "Problem matching service key, username and password", Response.Status.UNAUTHORIZED);
		}
	}

	@Override
	public void signFile(HttpHeaders httpHeaders, AsyncResponse asyncResponse, PartiallySignatureTO partiallySignatureTO) {
		String serviceKey = httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY);
		
		MediatorRestClient mediatorRestClient = new MediatorRestClient();
		mediatorRestClient.setWebTarget("http://localhost:8080/MediatorServer/rest/service/"); 
		
		try {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Future<Response> futureResponse = mediatorRestClient.signFile(serviceKey, partiallySignatureTO);
						String completeSignatureBase64String= futureResponse.get().readEntity(String.class);
						
						Response response = Response.ok(completeSignatureBase64String, MediaType.APPLICATION_JSON).build();
						asyncResponse.resume(response);
					} catch (Exception e) {
						e.printStackTrace();
						asyncResponse(asyncResponse, "Unknown Error", Response.Status.INTERNAL_SERVER_ERROR);
					}
				}
			}).start();
		}
		catch (Exception e) {
			e.printStackTrace();
			asyncResponse(asyncResponse, "Unknown Error", Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	protected void send(AsyncResponse asyncResponse, MessageTO message) {
		message.setSendDate(new Date());
		Response response = Response.ok(message, MediaType.APPLICATION_JSON).build();
		asyncResponse.resume(response);
	}
	
	private void asyncResponse(AsyncResponse asyncResponse, String responseMessage, Response.Status responseStatus)
	{
		JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
		jsonObjBuilder.add("message", responseMessage);
		JsonObject jsonObj = jsonObjBuilder.build();
		Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonObj.toString()).build();
		asyncResponse.resume(response);
	}
}
