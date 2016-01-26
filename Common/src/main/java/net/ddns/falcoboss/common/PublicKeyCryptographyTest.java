package net.ddns.falcoboss.common;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import org.junit.Assert;
import org.junit.Test;

public class PublicKeyCryptographyTest {

	@Test
	public void testCreateKeyPair() {
		KeyPair keyPair = null;
		PrivateKey privateKey = null;
		PublicKey publicKey = null;
		try {
			keyPair = PublicKeyCryptography.createKeyPair();
			String privateKeyString = KeyHelper.getBase64StringFromPrivateKey(keyPair.getPrivate());
			String publicKeyString = KeyHelper.getBase64StringFromPublicKey(keyPair.getPublic());
			privateKey = KeyHelper.getPrivateKeyFromBase64String(privateKeyString);
			publicKey = KeyHelper.getPublicKeyFromBase64String(publicKeyString);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

		Assert.assertEquals(keyPair.getPrivate(), privateKey);
		Assert.assertEquals(keyPair.getPublic(), publicKey);
		
		
		BigInteger publicModulus = ((RSAPublicKey)publicKey).getModulus();
		BigInteger publicExponent = ((RSAPublicKey)publicKey).getPublicExponent();
		
		BigInteger privateModulus = ((RSAPrivateKey)privateKey).getModulus();
		BigInteger privateExponent = ((RSAPrivateKey)privateKey).getPrivateExponent();

		String publicModulusString = Base64.getEncoder().encodeToString(publicModulus.toByteArray());
		String publicExponentString = Base64.getEncoder().encodeToString(publicExponent.toByteArray());
		
		String privateModulusString = Base64.getEncoder().encodeToString(privateModulus.toByteArray());
		String privateExponentString = Base64.getEncoder().encodeToString(privateExponent.toByteArray());
		
		System.out.println(publicModulusString);
		System.out.println(publicExponentString);
		
		System.out.println(privateModulusString);
		System.out.println(privateExponentString);
	}
	
	
}
