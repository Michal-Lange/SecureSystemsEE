package net.ddns.falcoboss.mediatorserver.service.signature;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateCrtKeySpec;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.ddns.falcoboss.common.KeyHelper;
import net.ddns.falcoboss.common.PublicKeyCryptography;


public class PartKeyGeneratorTest {
	
	public final static int delta = 120;
    @Test
    public void testKeySplit() {
    	
    	KeyPair newKeyPair = null;
		try {
			
			
			newKeyPair = PublicKeyCryptography.createKeyPair();
			PublicKey commonPublicKey = newKeyPair.getPublic();
			RSAPrivateKey commonPrivateKey = (RSAPrivateKey) newKeyPair.getPrivate();
			
			PrivateKey mediatorOwnKey  = commonPrivateKey;
			
			BigInteger modulus = ((RSAPublicKey)commonPublicKey).getModulus();
			BigInteger commonPrivateExponent = commonPrivateKey.getPrivateExponent();
			
			BigInteger mediatorPrivateExponent = PartKeyGenerator.generateFinalizationKeyExponent("testServiceKey", modulus.bitLength(), delta, mediatorOwnKey);
			
			RSAPrivateKey mediatorPrivateKey = (RSAPrivateKey) KeyHelper.getPrivateKeyFromBigIntegerExponentAndModulus(mediatorPrivateExponent, modulus);
			RSAPrivateKey userPrivateKey = (RSAPrivateKey) PublicKeyCryptography.calculateUserPrivateKey(commonPrivateKey, mediatorPrivateExponent);
		
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPrivateCrtKeySpec pkSpec = keyFactory.getKeySpec(commonPrivateKey, RSAPrivateCrtKeySpec.class);
			BigInteger pMinusOne = pkSpec.getPrimeP().subtract(BigInteger.ONE);
			BigInteger qMinusOne = pkSpec.getPrimeQ().subtract(BigInteger.ONE);
			BigInteger fi = (pMinusOne.multiply(qMinusOne));

			BigInteger userPrivateExponent = (commonPrivateExponent.subtract(mediatorPrivateExponent)).mod(fi);
	
			Assert.assertEquals(commonPrivateKey.getPrivateExponent(), (userPrivateExponent.add(mediatorPrivateExponent)).mod(fi));
			
			byte[] message = "RSA Signature Test String".getBytes();
			BigInteger bigIntegerFileHash = new BigInteger(1,message);
			
			BigInteger commonSignatureB = bigIntegerFileHash.modPow(commonPrivateExponent, modulus);
			BigInteger userSignatureB = bigIntegerFileHash.modPow(userPrivateExponent, modulus);
			BigInteger mediatorSignatureB = bigIntegerFileHash.modPow(mediatorPrivateExponent, modulus);
			
			Assert.assertEquals(commonSignatureB,(userSignatureB.multiply(mediatorSignatureB)).mod(modulus));
			
			byte[] commonSignatureBy = commonSignatureB.toByteArray();
			byte[] userSignatureBy = userSignatureB.toByteArray();
			byte[] mediatorSignatureBy = mediatorSignatureB.toByteArray();
			
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(commonSignatureBy);
			byte[] commonSignatureByH = md.digest();
			
			md.update(userSignatureBy);
			byte[] userSignatureByH = md.digest();
			
			md.update(commonSignatureBy);
			byte[] mediatorSignatureByH = md.digest();
			
			
			Signature commonSignature = Signature.getInstance("SHA512withRSA");
			commonSignature.initSign(commonPrivateKey);
			commonSignature.update(message);
			byte[] commonSignBytes = commonSignature.sign();
			
			BigInteger commonSignPositiveBigInteger = new BigInteger(1,commonSignBytes);
			BigInteger commonSignBigInteger = new BigInteger(commonSignBytes);
			
			commonSignature.initVerify(commonPublicKey);
			commonSignature.update(message);
		    Assert.assertTrue(commonSignature.verify(commonSignBytes));
			
			Signature userPartSignature = Signature.getInstance("SHA512withRSA");
			userPartSignature.initSign(userPrivateKey);
			userPartSignature.update(message);
			byte[] userSignBytes = userPartSignature.sign();
			
			BigInteger userSignPositiveBigInteger = new BigInteger(1,userSignBytes);
			BigInteger userSignBigInteger = new BigInteger(userSignBytes);
			
			Signature mediatorPartSignature = Signature.getInstance("SHA512withRSA");
			mediatorPartSignature.initSign(mediatorPrivateKey);
			mediatorPartSignature.update(message);
			byte[] mediatorSignBytes = userPartSignature.sign();
			
			BigInteger mediatorPositiveSignBigInteger = new BigInteger(1,mediatorSignBytes);
			BigInteger mediatorSignBigInteger = new BigInteger(mediatorSignBytes);

			BigInteger assembledSignature = (userSignPositiveBigInteger.multiply(mediatorPositiveSignBigInteger)).mod(modulus);
			
			//Assert.assertEquals(commonSignBigInteger, assembledSignature);
			
			
		}catch(Exception e){
			e.printStackTrace();
			Assert.fail();
		}
    }
}
