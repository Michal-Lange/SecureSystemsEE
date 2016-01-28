package net.ddns.falcoboss.common;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyHelper {
	
	public static PrivateKey getPrivateKeyFromBase64ExponentAndModulus(String exponent, String modulus) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return getPrivateKeyFromBigIntegerExponentAndModulus(getBigIntegerFromBase64String(exponent),getBigIntegerFromBase64String(modulus));
	}
	
	public static PublicKey getPublicKeyFromBase64ExponentAndModulus(String exponent, String modulus) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return getPublicKeyFromBigIntegerExponentAndModulus(getBigIntegerFromBase64String(exponent),getBigIntegerFromBase64String(modulus));
	}
	
	public static PublicKey getPublicKeyFromBigIntegerExponentAndModulus(String exponent, String modulus) throws NoSuchAlgorithmException, InvalidKeySpecException {
		RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(getBigIntegerFromBase64String(modulus), getBigIntegerFromBase64String(exponent));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
		return publicKey;
	}
	
	public static PrivateKey getPrivateKeyFromBigIntegerExponentAndModulus(BigInteger exponent, BigInteger modulus) throws NoSuchAlgorithmException, InvalidKeySpecException {	
		RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(modulus, exponent);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
		return privateKey;
	}
	
	public static PublicKey getPublicKeyFromBigIntegerExponentAndModulus(BigInteger exponent, BigInteger modulus) throws NoSuchAlgorithmException, InvalidKeySpecException {
		RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, exponent);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
		return publicKey;
	}
	
	public static BigInteger getBigIntegerFromBase64String(String base64String) {
		byte[] bytes = Base64.getDecoder().decode(base64String);
		return new BigInteger(bytes);
	}
	
	public static String getBase64StringFromBigInteger(BigInteger bigInteger) {
		return Base64.getEncoder().encodeToString(bigInteger.toByteArray());
	}

	public static String getBase64StringFromPrivateKey(PrivateKey privateKey) {
		return Base64.getEncoder().encodeToString(privateKey.getEncoded());
	}

	public static String getBase64StringFromPublicKey(PublicKey publicKey) {
		return Base64.getEncoder().encodeToString(publicKey.getEncoded());
	}

	public static PublicKey getPublicKeyFromBase64String(String publicKeyBase64String) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64String);
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	    EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(keyBytes);
	    PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
	    return publicKey;
	}

	public static PrivateKey getPrivateKeyFromBase64String(String publicKeyBase64String) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64String);
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	    EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(keyBytes);
	    PrivateKey privavteKey = keyFactory.generatePrivate(privateKeySpec);
	    return privavteKey;
	}

	public static String getPrivateKeyBase64StringFromKeyPair(KeyPair keyPair) {
	    if (keyPair == null) return null;
	    return getBase64StrFromByte(keyPair.getPrivate().getEncoded());
	}

	public static String getPublicKeyBase64StringFromKeyPair(KeyPair keyPair) {
	    if (keyPair == null) return null;
	    return getBase64StrFromByte(keyPair.getPublic().getEncoded());
	}

	public static String getBase64StrFromByte(byte[] key) {
	    if (key == null || key.length == 0) return null;
	    return Base64.getEncoder().encodeToString(key);
	}
	
}
