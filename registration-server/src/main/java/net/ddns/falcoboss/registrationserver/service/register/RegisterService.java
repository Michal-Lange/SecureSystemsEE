package net.ddns.falcoboss.registrationserver.service.register;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.logging.Logger;

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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.ddns.falcoboss.common.cryptography.KeyHelper;
import net.ddns.falcoboss.common.cryptography.PublicKeyCryptography;
import net.ddns.falcoboss.common.transport.objects.HTTPHeaderNames;
import net.ddns.falcoboss.common.transport.objects.KeyPairTO;
import net.ddns.falcoboss.common.transport.objects.MessageTO;
import net.ddns.falcoboss.common.transport.objects.PartiallySignatureTO;
import net.ddns.falcoboss.common.transport.objects.UserTO;
import net.ddns.falcoboss.common.transport.objects.UsernameAndPasswordTO;
import net.ddns.falcoboss.registrationserver.messanger.MessangerBean;
import net.ddns.falcoboss.registrationserver.property.reader.PropertyReader;
import net.ddns.falcoboss.registrationserver.rest.client.MediatorRestClient;
import net.ddns.falcoboss.registrationserver.security.AuthenticatorBean;
import net.ddns.falcoboss.registrationserver.security.RequestFilter;
import net.ddns.falcoboss.registrationserver.usermanagement.Group;
import net.ddns.falcoboss.registrationserver.usermanagement.User;
import net.ddns.falcoboss.registrationserver.usermanagement.UserBean;

@Stateless(name = "RegisterService", mappedName = "ejb/RegisterService")
public class RegisterService implements RegisterServiceProxy {

	private static final long serialVersionUID = -6663599014192066936L;

	// private static final int _ThreadPool = 10;
	// private ExecutorService writer =
	// Executors.newFixedThreadPool(_ThreadPool);
	
	private final static Logger log = Logger.getLogger(RequestFilter.class.getName());

	private PropertyReader propertyReader;

	@EJB
	private AuthenticatorBean authenticatorBean;

	@EJB
	private UserBean userBean;

	@EJB
	private MessangerBean messangerBean;

	public PropertyReader getPropertyReader() {
		return propertyReader;
	}

	public void setPropertyReader(PropertyReader propertyReader) {
		this.propertyReader = propertyReader;
	}

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
			synchronized (messangerBean.getRecivedMessages()) {
				messangerBean.getRecivedMessages().remove(requestUsername);
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
		String serviceKey = httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY);
		User requestUser = userBean.findByServiceKey(serviceKey);
		String requestUsername = requestUser.getUsername();
		if (message.getSender().equals(requestUsername)) {
			messangerBean.sendMessage(message);
			JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
			jsonObjBuilder.add("message", "Executed sendMessage");
			JsonObject jsonObj = jsonObjBuilder.build();
			return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
		} else {
			JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
			jsonObjBuilder.add("message", "Message sender and request user are different!");
			JsonObject jsonObj = jsonObjBuilder.build();
			return getNoCacheResponseBuilder(Response.Status.BAD_REQUEST).entity(jsonObj.toString()).build();
		}
	}

	@Override
	public void reciveMessage(@Context HttpHeaders httpHeaders, @Suspended AsyncResponse asyncResponse) {
		String serviceKey = httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY);
		User requestUser = userBean.findByServiceKey(serviceKey);
		String requestUsername = requestUser.getUsername();
		messangerBean.reciveMessage(requestUsername, asyncResponse);
	}

	@Override
	public Response logout(@Context HttpHeaders httpHeaders) {
		try {
			String serviceKey = httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY);
			String authToken = httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN);
			authenticatorBean.logout(serviceKey, authToken);
			User requestUser = userBean.findByServiceKey(serviceKey);
			String requestUsername = requestUser.getUsername();
			synchronized (messangerBean.getListeners()) {
				messangerBean.getListeners().remove(requestUsername);
			}
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
		if (!authenticatorBean.isUsernameAndPasswordValid(serviceKey, username, password)) {
			asyncResponse(asyncResponse, "Problem matching service key, username and password",
					Response.Status.UNAUTHORIZED);
		}
		try {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						propertyReader = new PropertyReader();
						getPropertyReader().readPropertyValues();
						mediatorRestClient.setWebTarget(getPropertyReader().getServiceUrl());
						KeyPair newKeyPair = PublicKeyCryptography.createKeyPair();
						RSAPublicKey publicKey = (RSAPublicKey) newKeyPair.getPublic();
						PrivateKey privateKey = newKeyPair.getPrivate();
						String publicKeyBase64String = KeyHelper.getBase64StringFromPublicKey(publicKey);

						Future<Response> futureResponse = mediatorRestClient.requestNewFinalizationKey(serviceKey,
								publicKeyBase64String);
						Response mediatorResponse = futureResponse.get();
						if (mediatorResponse.getStatus() == 200) {
							String mediatorPrivateExponentBase64String = mediatorResponse.readEntity(String.class);
							BigInteger mediatorPrivateExponent = KeyHelper
									.getBigIntegerFromBase64String(mediatorPrivateExponentBase64String);
							RSAPrivateKey userPrivateKey = (RSAPrivateKey) PublicKeyCryptography
									.calculateUserPrivateKey(privateKey, mediatorPrivateExponent);
							KeyPairTO keyPairTO = new KeyPairTO();
							keyPairTO.setModulus(KeyHelper.getBase64StringFromBigInteger(userPrivateKey.getModulus()));
							keyPairTO.setPrivateExponent(
									KeyHelper.getBase64StringFromBigInteger(userPrivateKey.getPrivateExponent()));
							keyPairTO.setPublicExponent(
									KeyHelper.getBase64StringFromBigInteger(publicKey.getPublicExponent()));
							// List<String> keyStringList =
							// Arrays.asList(userPrivateKeyBase64String,
							// userPublicKeyBase64String);
							// GenericEntity<List<String>> genericEntity = new
							// GenericEntity<List<String>>(keyStringList) {};
							Response response = Response.ok(keyPairTO, MediaType.APPLICATION_JSON).build();
							asyncResponse.resume(response);
						} else {
							asyncResponse(asyncResponse, "Mediator Unknown Error",
									Response.Status.INTERNAL_SERVER_ERROR);
						}
					} catch (Exception e) {
						e.printStackTrace();
						asyncResponse(asyncResponse, "Unknown Error", Response.Status.INTERNAL_SERVER_ERROR);
					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
			asyncResponse(asyncResponse, "Unknown Error", Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void signFile(HttpHeaders httpHeaders, AsyncResponse asyncResponse,
			PartiallySignatureTO partiallySignatureTO) {
		String serviceKey = httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY);
		MediatorRestClient mediatorRestClient = new MediatorRestClient();
		try {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						propertyReader = new PropertyReader();
						getPropertyReader().readPropertyValues();
						mediatorRestClient.setWebTarget(getPropertyReader().getServiceUrl());
						Future<Response> futureResponse = mediatorRestClient.signFile(serviceKey, partiallySignatureTO);
						Response mediatorResponse = futureResponse.get();
						if (mediatorResponse.getStatus() == 200) {
							String completeSignatureBase64String = futureResponse.get().readEntity(String.class);
							Response response = Response.ok(completeSignatureBase64String, MediaType.APPLICATION_JSON)
									.build();
							asyncResponse.resume(response);
						} else {
							asyncResponse(asyncResponse, "Mediator Unknown Error",
									Response.Status.INTERNAL_SERVER_ERROR);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
			asyncResponse(asyncResponse, "Unknown Error", Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	protected void send(AsyncResponse asyncResponse, MessageTO message) {
		message.setSendDate(new Date());
		Response response = Response.ok(message, MediaType.APPLICATION_JSON).build();
		asyncResponse.resume(response);
	}

	private void asyncResponse(AsyncResponse asyncResponse, String responseMessage, Response.Status responseStatus) {
		JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
		jsonObjBuilder.add("message", responseMessage);
		JsonObject jsonObj = jsonObjBuilder.build();
		Response response = Response.status(responseStatus).entity(jsonObj.toString()).build();
		asyncResponse.resume(response);
	}

	@Override
	public void addUser(HttpHeaders httpHeaders, AsyncResponse asyncResponse, UserTO userTO) {
		try{
			String serviceKey = httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY);
			User requestUser = userBean.findByServiceKey(serviceKey);
			if(requestUser.getGroups().contains(Group.ADMINISTRATOR))
			{
				if(userBean.find(userTO.getUsername())==null){
					User user = new User(userTO);
					String newUserServiceKey = UUID.randomUUID().toString();
					user.setServiceKey(newUserServiceKey);
					userBean.save(user);
					Response response = Response.ok(newUserServiceKey, MediaType.APPLICATION_JSON).build();
					asyncResponse.resume(response);
					
				}
				else
				{
					asyncResponse(asyncResponse, "User exist!", Response.Status.BAD_REQUEST);
				}
			}
			else {
				log.info(requestUser.getUsername() + ": not authorized login!");
				asyncResponse(asyncResponse, "Unknown Error", Response.Status.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			asyncResponse(asyncResponse, "Unknown Error", Response.Status.INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		}
	}
	
	@Override
	public void changePassword(HttpHeaders httpHeaders, AsyncResponse asyncResponse, UserTO userTO) {
		try{
			String serviceKey = httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY);
			User requestUser = userBean.findByServiceKey(serviceKey);
			if(requestUser.getGroups().contains(Group.ADMINISTRATOR))
			{
				User user = userBean.find(userTO.getUsername());
				if(user!=null){
					user.setPassword(userTO.getNewPassword());
					userBean.update(user);
				}
				else
				{
					asyncResponse(asyncResponse, "User exist!", Response.Status.BAD_REQUEST);
				}
			}
			else if(requestUser.getUsername().equals(userTO.getUsername()) &&
					requestUser.getPassword().equals(userTO.getPassword())) {
					requestUser.setPassword(userTO.getNewPassword());
					userBean.update(requestUser);
			}
			else
			{
				log.info(requestUser.getUsername() + ": not authorized login!");
				asyncResponse(asyncResponse, "Unknown Error", Response.Status.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			asyncResponse(asyncResponse, "Unknown Error", Response.Status.INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		}
	}
	
	@Override
	public void updateUser(HttpHeaders httpHeaders, AsyncResponse asyncResponse, UserTO userTO) {
		try{
			String serviceKey = httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY);
			User requestUser = userBean.findByServiceKey(serviceKey);
			if(requestUser.getGroups().contains(Group.ADMINISTRATOR))
			{
				if(userBean.find(userTO.getUsername())!=null){
					User user = new User(userTO);
					userBean.update(user);
				}
				else
				{
					asyncResponse(asyncResponse, "User not found!", Response.Status.BAD_REQUEST);
				}
			}
			else {
				log.info(requestUser.getUsername() + ": not authorized login!");
				asyncResponse(asyncResponse, "Unknown Error", Response.Status.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			asyncResponse(asyncResponse, "Unknown Error", Response.Status.INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		}
	}

	@Override
	public void deleteUser(HttpHeaders httpHeaders, AsyncResponse asyncResponse, UserTO userTO) {
		try{
			String serviceKey = httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY);
			User requestUser = userBean.findByServiceKey(serviceKey);
			if(requestUser.getGroups().contains(Group.ADMINISTRATOR))
			{
				if(userBean.find(userTO.getUsername())!=null){
					userBean.remove(userTO.getUsername());
				}
				else
				{
					asyncResponse(asyncResponse, "User not found!", Response.Status.BAD_REQUEST);
				}
			}
			else {
				log.info(requestUser.getUsername() + ": not authorized login!");
				asyncResponse(asyncResponse, "Unknown Error", Response.Status.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			asyncResponse(asyncResponse, "Unknown Error", Response.Status.INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		}

	}
}
