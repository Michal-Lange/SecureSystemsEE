package net.ddns.falcoboss.mediatorserver.service.mediator;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.ddns.falcoboss.common.cryptography.KeyHelper;
import net.ddns.falcoboss.common.cryptography.PublicKeyCryptography;
import net.ddns.falcoboss.common.transport.objects.HTTPHeaderNames;
import net.ddns.falcoboss.common.transport.objects.PartiallySignatureTO;
import net.ddns.falcoboss.mediatorserver.partkeys.PartKey;
import net.ddns.falcoboss.mediatorserver.partkeys.PartKeyBean;
import net.ddns.falcoboss.mediatorserver.service.signature.PartKeyGenerator;


@Stateless(name = "MediatorService", mappedName = "ejb/MediatorService")
public class MediatorService implements MediatorServiceProxy  {

	private static final long serialVersionUID = 8331795842796782882L;

	public final static int delta = 120;
	
	private final static Logger log = Logger.getLogger(MediatorService.class.getName());
	
	@EJB
	PartKeyBean partKeyBean;
	
	@Override
	public void generateMediatorKey(HttpHeaders httpHeaders, AsyncResponse asyncResponse, String publicKeyBase64String) {
		new Thread(new Runnable() {
            @Override
            public void run() {
            	String serviceKey = httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY);
            	if(!"".equals(publicKeyBase64String) && !"".equals(serviceKey))
            	{
            		log.info("PublicKey recived: " + publicKeyBase64String);
	            	PublicKey publicKey = null;
					try {
						publicKey = KeyHelper.getPublicKeyFromBase64String(publicKeyBase64String);
		        		BigInteger modulus = ((RSAPublicKey)publicKey).getModulus();
		        		BigInteger publicExponent = ((RSAPublicKey)publicKey).getPublicExponent();
		        		PartKey ownKey = partKeyBean.find("ownKey");
		        		PrivateKey ownPrivateKey = KeyHelper.getPrivateKeyFromBase64ExponentAndModulus(ownKey.getPrivateExponent(), ownKey.getPublicModulus());
		        		BigInteger finalizationKeyExponent = PartKeyGenerator.generateFinalizationKeyExponent(serviceKey, modulus.bitLength(), delta, ownPrivateKey);
		        		PartKey newPartKey = new PartKey();
		        		newPartKey.setServiceKey(serviceKey);
		        		newPartKey.setPrivateExponent(KeyHelper.getBase64StringFromBigInteger(finalizationKeyExponent));
		        		newPartKey.setPublicExponent(KeyHelper.getBase64StringFromBigInteger(publicExponent));
		        		newPartKey.setPublicModulus(KeyHelper.getBase64StringFromBigInteger(modulus));
		                partKeyBean.update(newPartKey);
		                String finalizationKeyExponentBase64String = KeyHelper.getBase64StringFromBigInteger(finalizationKeyExponent);
		                Response response = Response.ok(finalizationKeyExponentBase64String, MediaType.APPLICATION_JSON).build();
		                asyncResponse.resume(response);
	            	} catch (Exception e) {
	            		asyncResponse(asyncResponse, "Unknown Error", Response.Status.INTERNAL_SERVER_ERROR);
	        			e.printStackTrace();
	            	}
            	}
            	else
            	{
            		asyncResponse(asyncResponse, "Ivalid Service Key or Public Key", Response.Status.BAD_REQUEST);
            	}
            }
        }).start();
	}

	@Override
	public void signFile(HttpHeaders httpHeaders, AsyncResponse asyncResponse, PartiallySignatureTO partiallySignatureTO) {
		
		new Thread(new Runnable() {
            @Override
            public void run() {
            	String serviceKey = httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY);
            	log.info("Partially Signature recived from: " + serviceKey);
            	PartKey mediatorPartKey = partKeyBean.find(serviceKey);
            	RSAPrivateKey mediatorPrivateKey;
				try {
					mediatorPrivateKey = (RSAPrivateKey) KeyHelper.getPrivateKeyFromBase64ExponentAndModulus(mediatorPartKey.getPrivateExponent(), mediatorPartKey.getPublicModulus());
					BigInteger fileHash = KeyHelper.getBigIntegerFromBase64String(partiallySignatureTO.getFileHash());
	            	BigInteger mediatorSignedHash = PublicKeyCryptography.signFileHash(fileHash, mediatorPrivateKey);
	            	BigInteger userSignedHash = KeyHelper.getBigIntegerFromBase64String(partiallySignatureTO.getPatiallySignedFileHash());
	            	BigInteger completeSignature = (userSignedHash.multiply(mediatorSignedHash)).mod(mediatorPrivateKey.getModulus());
	            	String completeSignatureBase64String = KeyHelper.getBase64StringFromBigInteger(completeSignature);
	            	Response response = Response.ok(completeSignatureBase64String, MediaType.APPLICATION_JSON).build();
	                asyncResponse.resume(response);

				} catch (Exception e) {
					e.printStackTrace();
				}
            	
            	
            }
        }).start();
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