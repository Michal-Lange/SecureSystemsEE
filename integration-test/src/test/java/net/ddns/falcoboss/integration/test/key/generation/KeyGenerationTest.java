package net.ddns.falcoboss.integration.test.key.generation;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import net.ddns.falcoboss.javaclient.api.Facade;

public class KeyGenerationTest {
	@Test
	public void testKeyGeneration(){
		
		try {
			Facade facade = new Facade();
			Assert.assertEquals(200, facade.login("username1", "password1").getStatus());
			
			facade.requestNewKey("username1", "password1");
			
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
