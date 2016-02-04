package net.ddns.falcoboss.integration.test.messanger;

import java.util.concurrent.Future;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import net.ddns.falcoboss.common.transport.objects.MessageTO;
import net.ddns.falcoboss.javaclient.api.Facade;


public class SmokeTest extends AbstractRestClientTest{
	@Test
    public void testConnection() throws Exception{
	Response response = restClient3.login("username3", password3Hash, "55555555-926f-49b6-ba00-920bcf934c2a");
	Assert.assertEquals(200, response.getStatus());
    Assert.assertNotNull(restClient3.getAuthToken().get("auth_token"));

	Assert.assertEquals(200, restClient.login("username1", password1Hash, "f80ebc87-ad5c-4b29-9366-5359768df5a1").getStatus());

	Assert.assertEquals(200, restClient2.login("username2", password2Hash, "3b91cab8-926f-49b6-ba00-920bcf934c2a").getStatus());

	for(int i=0; i<100; i++){
	    MessageTO message = new MessageTO();
	    message.setSender("username3");
	    message.setRecipient("username2");
	    restClient3.sendMessage(message);
	}
	
	for(int i=0; i<100; i++){
		Response response1 = restClient.reciveMessage().get();
		MessageTO message = response1.readEntity(MessageTO.class);
		System.out.println(message.getText());
	}
	}
}
