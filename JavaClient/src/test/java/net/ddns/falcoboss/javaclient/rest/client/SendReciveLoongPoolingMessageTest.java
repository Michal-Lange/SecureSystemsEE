package net.ddns.falcoboss.javaclient.rest.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import net.ddns.falcoboss.common.Message;

public class SendReciveLoongPoolingMessageTest extends AbstractRestClientTest {
	@Test
    public void testConnection(){
		
		
		Response response = restClient.login("username1", "password1", "f80ebc87-ad5c-4b29-9366-5359768df5a1");
		Assert.assertEquals(200, response.getStatus());
        Assert.assertNotNull(restClient.getAuthToken().get("auth_token"));
        
        response = restClient2.login("username2", "password2", "3b91cab8-926f-49b6-ba00-920bcf934c2a");
    	Assert.assertEquals(200, response.getStatus());
        Assert.assertNotNull(restClient2.getAuthToken().get("auth_token"));
        
        Message messageRecived = null;
        final Future<Response> futureResponse1 = restClient2.reciveMessage();
        
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
        
        Message message = new Message();
        message.setSender("username1");
        message.setRecipient("username2");
        message.setText("Test async message 1!");
        response = restClient.sendMessage(message);
        Assert.assertEquals(200, response.getStatus());
           	
        try {
        	messageRecived = futureResponse1.get().readEntity(Message.class);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
        System.out.println(messageRecived.getText());
        
        response = restClient.logout();
    	Assert.assertEquals(204, response.getStatus());
    	
    	response = restClient2.logout();
    	Assert.assertEquals(204, response.getStatus());
    }
    
    @After
    public void logout(){
    }
}
