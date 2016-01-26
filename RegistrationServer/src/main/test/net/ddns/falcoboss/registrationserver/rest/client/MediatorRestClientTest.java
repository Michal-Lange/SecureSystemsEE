package net.ddns.falcoboss.registrationserver.rest.client;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import net.ddns.falcoboss.common.KeyHelper;
import net.ddns.falcoboss.common.PublicKeyCryptography;


public class MediatorRestClientTest {

	MediatorRestClient mediatorRestClent;

    @Before
    public void initClient() {
    	mediatorRestClent = new MediatorRestClient();
    	mediatorRestClent.setWebTarget("http://localhost:8080/RegistrationServer/rest/service/");
    }
		
	@Test
	public void testRequestNewFinalizationKey() {
		
		KeyPair keyPair = null;
		try{				
			keyPair = PublicKeyCryptography.createKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		PublicKey publicKey = keyPair.getPublic();
		String publicKeyString = KeyHelper.getBase64StringFromPublicKey(publicKey);
		Future<Response> futureResponse = mediatorRestClent.requestNewFinalizationKey("testServiceKey", publicKeyString);
		try {
			String mediatorPrivateKeyExponentStrijng = futureResponse.get().readEntity(String.class);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
