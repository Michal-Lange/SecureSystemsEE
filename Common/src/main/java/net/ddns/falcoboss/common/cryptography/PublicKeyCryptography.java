package net.ddns.falcoboss.common.cryptography;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;


public class PublicKeyCryptography {
	private static final int KEY_LENGTH = 1024;

	public static KeyPair createKeyPair() throws NoSuchAlgorithmException {
	    KeyPair keyPair = null;
	    KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
	    keygen.initialize(KEY_LENGTH);
	    keyPair = keygen.generateKeyPair();
	    return keyPair;
	}
	
	public static PrivateKey calculateUserPrivateKey(PrivateKey privateKey, BigInteger mediatorPrivateExponent) throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		if(privateKey != null && mediatorPrivateExponent != null)
			throw new InvalidKeySpecException();
		RSAPrivateKey rsaPrivateKey  = ((RSAPrivateKey) privateKey);
		
		BigInteger modulus = rsaPrivateKey.getModulus();
		BigInteger privateExponent = rsaPrivateKey.getPrivateExponent();
		
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		RSAPrivateCrtKeySpec pkSpec = keyFactory.getKeySpec(privateKey, RSAPrivateCrtKeySpec.class);
		BigInteger pMinusOne = pkSpec.getPrimeP().subtract(BigInteger.ONE);
		BigInteger qMinusOne = pkSpec.getPrimeQ().subtract(BigInteger.ONE);
		BigInteger fi = (pMinusOne.multiply(qMinusOne));
		
		BigInteger userPrivateExponent = (privateExponent.subtract(mediatorPrivateExponent)).mod(fi);
		
		RSAPrivateKeySpec rsaUserPrivateKeySpec = new RSAPrivateKeySpec(modulus, userPrivateExponent);
		keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey userPrivateKey = keyFactory.generatePrivate(rsaUserPrivateKeySpec);
		return userPrivateKey;
	}
	
	public static BigInteger signFileHash(BigInteger fileHash, PrivateKey privateKey)
	{
		BigInteger privateExponent = ((RSAPrivateKey) privateKey).getPrivateExponent();
		BigInteger modulus = ((RSAPrivateKey) privateKey).getModulus();
		return fileHash.modPow(privateExponent, modulus);
	}
}
