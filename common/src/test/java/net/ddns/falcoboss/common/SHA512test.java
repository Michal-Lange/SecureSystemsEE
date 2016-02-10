package net.ddns.falcoboss.common;

import org.junit.Assert;
import org.junit.Test;

import net.ddns.falcoboss.common.cryptography.SHA512;

public class SHA512test {

	@Test
	public void testHashText() throws Exception {
		String password1 = "passsword1";
		String password2 = "password2";
		String password1Hash = SHA512.hashText(password1);
		String password2Hash = SHA512.hashText(password2);
		System.out.println(password1 + ": " + password1Hash);
		System.out.println(password2 + ": " + password2Hash);
		Assert.assertEquals(password1Hash, "176d730e18e01d2877f7a0d26ca5ac377556e04406dee2782d4a0d76cf04f6431838fcbcc65566b0c897a0286acc7f06ce46eccffa5903208cdfc15a40db8b6d");
		Assert.assertEquals(password2Hash, "92a891f888e79d1c2e8b82663c0f37cc6d61466c508ec62b8132588afe354712b20bb75429aa20aa3ab7cfcc58836c734306b43efd368080a2250831bf7f363f");
		
	}

}
