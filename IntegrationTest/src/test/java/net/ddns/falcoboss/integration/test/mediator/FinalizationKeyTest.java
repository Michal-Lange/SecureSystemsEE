package net.ddns.falcoboss.integration.test.mediator;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.concurrent.Future;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.ddns.falcoboss.common.KeyHelper;
import net.ddns.falcoboss.common.PublicKeyCryptography;
import net.ddns.falcoboss.registrationserver.rest.client.MediatorRestClient;

public class FinalizationKeyTest {

	MediatorRestClient mediatorRestClient = null;
    @Before
    public void initClient() {
    	mediatorRestClient = new MediatorRestClient();
		mediatorRestClient.setWebTarget("http://localhost:8080/mediator-server/rest/service/");
		
		
	}
    
    @Test
    public void testFinalizationKeyGeneration(){
    	KeyPair newKeyPair = null;
		try {
			newKeyPair = PublicKeyCryptography.createKeyPair();
			RSAPublicKey publicKey = (RSAPublicKey) newKeyPair.getPublic();
			RSAPrivateKey privateKey = (RSAPrivateKey) newKeyPair.getPrivate();
			
			BigInteger privateKeyExponent = privateKey.getPrivateExponent();
			
			String publicKeyBase64String = KeyHelper.getBase64StringFromPublicKey(publicKey);
			Future<Response> futureResponse = mediatorRestClient.requestNewFinalizationKey("f80ebc87-ad5c-4b29-9366-5359768df5a1", publicKeyBase64String);
			
			String mediatorPrivateExponentBase64String = futureResponse.get().readEntity(String.class);

			BigInteger mediatorPrivateExponent = KeyHelper.getBigIntegerFromBase64String(mediatorPrivateExponentBase64String);
			
			RSAPrivateKey userPrivateKey = (RSAPrivateKey) PublicKeyCryptography.calculateUserPrivateKey(privateKey, mediatorPrivateExponent);
			BigInteger userPrivateExponent = userPrivateKey.getPrivateExponent();
			
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPrivateCrtKeySpec pkSpec = keyFactory.getKeySpec(privateKey, RSAPrivateCrtKeySpec.class);
			BigInteger pMinusOne = pkSpec.getPrimeP().subtract(BigInteger.ONE);
			BigInteger qMinusOne = pkSpec.getPrimeQ().subtract(BigInteger.ONE);
			BigInteger fi = (pMinusOne.multiply(qMinusOne));
			
			BigInteger assembledPrivateExponent = (mediatorPrivateExponent.add(userPrivateExponent)).mod(fi);
			
			Assert.assertEquals(privateKeyExponent, assembledPrivateExponent);
			
			Assert.assertNotNull(userPrivateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
