package net.ddns.falcoboss.mediatorserver.service.mediator;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.ddns.falcoboss.common.HTTPHeaderNames;
import net.ddns.falcoboss.common.KeyHelper;
import net.ddns.falcoboss.mediatorserver.partkeys.PartKey;
import net.ddns.falcoboss.mediatorserver.partkeys.PartKeyBean;
import net.ddns.falcoboss.mediatorserver.service.signature.PartKeyGenerator;

@Stateless(name = "MediatorService", mappedName = "ejb/MediatorService")
public class MediatorService implements MediatorServiceProxy  {
	
	private static final long serialVersionUID = -369844580949941559L;
	
	//public final static String secret = "secret";
	
	public final static int delta = 120;
	
	@EJB
	PartKeyBean partKeyBean;
	
	@Override
	public void generateMediatorKey(HttpHeaders httpHeaders, AsyncResponse asyncResponse, PublicKey publicKey) {
		
		new Thread(new Runnable() {
            @Override
            public void run() {
            	String serviceKey = httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY);
        		BigInteger modulus = ((RSAPublicKey)publicKey).getModulus();
        		BigInteger publicExponent = ((RSAPublicKey)publicKey).getPublicExponent();
        		
        		PartKey ownKey = partKeyBean.find("ownKey");
        		PrivateKey ownPrivateKey;
        		
        		BigInteger finalizationKeyExponent = null;
        		try {
        			ownPrivateKey = KeyHelper.getPrivateKeyFromBase64ExponentAndModulus(ownKey.getPrivateExponent(), ownKey.getPublicModulus());
        			finalizationKeyExponent = PartKeyGenerator.generateFinalizationKeyExponent(serviceKey, modulus.bitLength(), delta, ownPrivateKey);
        		} catch (Exception e) {
        			e.printStackTrace();
        		}

        		PartKey newPartKey = new PartKey();
        		
        		newPartKey.setServiceKey(serviceKey);
        		newPartKey.setPrivateExponent(Base64.getEncoder().encodeToString(finalizationKeyExponent.toByteArray()));
        		
        		newPartKey.setPublicExponent(Base64.getEncoder().encodeToString(publicExponent.toByteArray()));
        		newPartKey.setPublicModulus(Base64.getEncoder().encodeToString(modulus.toByteArray()));
        		
                partKeyBean.save(newPartKey);
                Response response = Response.ok(finalizationKeyExponent, MediaType.APPLICATION_JSON).build();
                asyncResponse.resume(response);
            }
        }).start();
	}

	@Override
	public void signFile(HttpHeaders httpHeaders, AsyncResponse async, String file) {

	}

} 