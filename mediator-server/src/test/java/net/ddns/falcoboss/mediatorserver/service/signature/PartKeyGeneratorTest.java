package net.ddns.falcoboss.mediatorserver.service.signature;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.ddns.falcoboss.common.cryptography.KeyHelper;
import net.ddns.falcoboss.common.cryptography.PublicKeyCryptography;

public class PartKeyGeneratorTest {

	public final static int delta = 120;
	KeyPair newKeyPair = null;
	PrivateKey mediatorOwnKey = null;

	@Before
	public void generateKey() throws Exception {
		newKeyPair = PublicKeyCryptography.createKeyPair();

		String mediatorOwnModulus = "APGBsXYWAjLQewbQWxmoyQaY+EllH1MdXoGVUW0rmfP+g8XMw2AqAXgW5NHZK6dwIZ0CVzkBDf0M8JMWMDqUsGho0H+N1q+NIiwADmg061AEoS2rL9Jm5ulm7VPmShnoRuNog7s+PR3F8xSJMGbHctBQ1gRdqGst3q9NsehLdmJ731YiNCMhrC/X4TRgbudCL0CDVJ+J6mntf11HNzXO+c5EvAmyaf4zJDWIU0veosOCK6EMcFYv5HQWkmwEcIT5DdKdM8af10tMx36uC3+UlXYQDWHd96OupWRBLStyZIWcHJqWPrrf2GsJf5MoYt1ZHmZZQ4ee2d9vJA+zAzt6qjc=";
		String mediatorOwnPrivateExponent = "VBXV1cl/5nVUAGFW9q4fn95uxA8jQur81p1Ihnwh1CQPeTT76WV2sXs3HCFC479U1LfV6pEFb8+ri2q0TBEtAo1L2r1lvCWlejBi08FpFKkn/SCXO+h8CVO+2fFaZ37J/6+J/g2DdfRP2ByT75UN0p3yhf6d/wMvf1XL1ZdAlrThNuW/ju9JtLGlRnHLNStbofCwNV4c7NzmPT7KCSlgrDRfXQX7XLx+D+hFpZPWypErV+UlgRM0x2cfp+K2gE6Y+tYCBXLsZZ3I3q+bTDwmiOte+2sn0uop7HuC7CxvK+wUxqcOgkOMTWy2oA4U7OrtQXCL6hAHya/DlEGmCyH0UQ==";
		mediatorOwnKey = KeyHelper.getPrivateKeyFromBase64ExponentAndModulus(mediatorOwnPrivateExponent,
				mediatorOwnModulus);
	}

	@Test
	public void testKeySplit()
			throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {

		PublicKey commonPublicKey = newKeyPair.getPublic();
		RSAPrivateKey commonPrivateKey = (RSAPrivateKey) newKeyPair.getPrivate();

		BigInteger modulus = ((RSAPublicKey) commonPublicKey).getModulus();
		BigInteger commonPrivateExponent = commonPrivateKey.getPrivateExponent();
		BigInteger mediatorPrivateExponent = PartKeyGenerator.generateFinalizationKeyExponent("testServiceKey",
				modulus.bitLength(), delta, mediatorOwnKey);
		
		Assert.assertTrue(modulus.bitLength() < mediatorPrivateExponent.bitLength());

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		RSAPrivateCrtKeySpec pkSpec = keyFactory.getKeySpec(commonPrivateKey, RSAPrivateCrtKeySpec.class);
		BigInteger pMinusOne = pkSpec.getPrimeP().subtract(BigInteger.ONE);
		BigInteger qMinusOne = pkSpec.getPrimeQ().subtract(BigInteger.ONE);
		BigInteger fi = (pMinusOne.multiply(qMinusOne));

		BigInteger userPrivateExponent = (commonPrivateExponent.subtract(mediatorPrivateExponent)).mod(fi);

		Assert.assertEquals(commonPrivateKey.getPrivateExponent(),
				(userPrivateExponent.add(mediatorPrivateExponent)).mod(fi));
	}
}
