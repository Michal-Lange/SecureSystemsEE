package net.ddns.falcoboss.integration.test.messanger;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import net.ddns.falcoboss.common.transport.objects.MessageTO;

public class SendReciveLoongPoolingMessageTest extends AbstractRestClientTest {
	@Test
    public void testConnection() throws InterruptedException, ExecutionException{
		
		
		Response response = restClient.login("username1", password1Hash, "f80ebc87-ad5c-4b29-9366-5359768df5a1");
		Assert.assertEquals(200, response.getStatus());
        Assert.assertNotNull(restClient.getAuthToken().get("auth_token"));
        
        response = restClient2.login("username2", password2Hash, "3b91cab8-926f-49b6-ba00-920bcf934c2a");
    	Assert.assertEquals(200, response.getStatus());
        Assert.assertNotNull(restClient2.getAuthToken().get("auth_token"));
        
        MessageTO messageRecived = null;
        final Future<Response> futureResponse1 = restClient2.reciveMessage();
        
		Thread.sleep(3000);
        
        MessageTO message = new MessageTO();
        message.setSender("username1");
        message.setRecipient("username2");
        message.setText("Test async message 1!");
        response = restClient.sendMessage(message);
        Assert.assertEquals(200, response.getStatus());
       	messageRecived = futureResponse1.get().readEntity(MessageTO.class);
       	Assert.assertEquals("Test async message 1!", messageRecived.getText());
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
