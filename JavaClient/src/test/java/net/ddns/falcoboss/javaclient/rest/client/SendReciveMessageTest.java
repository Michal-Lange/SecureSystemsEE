package net.ddns.falcoboss.javaclient.rest.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import net.ddns.falcoboss.common.cryptography.SHA512;
import net.ddns.falcoboss.common.transport.objects.MessageTO;

public class SendReciveMessageTest extends AbstractRestClientTest {
	@Test
    public void testConnection() throws Exception{
		Response response = restClient.login("username1", password1Hash, "f80ebc87-ad5c-4b29-9366-5359768df5a1");
		Assert.assertEquals(200, response.getStatus());
        Assert.assertNotNull(restClient.getAuthToken().get("auth_token"));
        
        MessageTO message = new MessageTO();
        message.setSender("username1");
        message.setRecipient("username2");
        
        message.setText("Test message 1!");
        response = restClient.sendMessage(message);
        
        message.setText("Test message 2!");
        response = restClient.sendMessage(message);
        
        message.setText("Test message 3!");
        response = restClient.sendMessage(message);
        
		Assert.assertEquals(200, response.getStatus());
		
		response = restClient.logout();
    	Assert.assertEquals(204, response.getStatus());
    	
    	
		response = restClient.login("username2", password2Hash, "3b91cab8-926f-49b6-ba00-920bcf934c2a");
    	Assert.assertEquals(200, response.getStatus());
        Assert.assertNotNull(restClient.getAuthToken().get("auth_token"));
        
        for(int i=0; i<3; i++)
        {
        	final Future<Response> futureResponse1 = restClient.reciveMessage();
            message = futureResponse1.get().readEntity(MessageTO.class);
            System.out.println(message.getText());
        }

        response = restClient.logout();
    	Assert.assertEquals(204, response.getStatus());
    }
    
    @After
    public void logout(){
    }
}
