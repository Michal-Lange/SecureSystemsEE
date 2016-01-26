package net.ddns.falcoboss.mediatorserver.service.signature;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

public class PartKeyGenerator {
	public static BigInteger generateFinalizationKeyExponent(String serviceKey, int modulusBitLength, int delta, PrivateKey ownPrivateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
		Signature signature = Signature.getInstance("SHA512withRSA");
		signature.initSign(ownPrivateKey);
		signature.update(serviceKey.getBytes());
	    byte[] signatureBytes = signature.sign();
		byte[] privatePartExponent = new byte[(modulusBitLength + delta)/8];
		SecureRandom secureRandom = new SecureRandom(signatureBytes);
		secureRandom.nextBytes(privatePartExponent);
		return new BigInteger(privatePartExponent);
	}
}
