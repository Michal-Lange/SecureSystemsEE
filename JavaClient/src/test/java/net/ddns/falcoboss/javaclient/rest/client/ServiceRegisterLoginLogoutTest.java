package net.ddns.falcoboss.javaclient.rest.client;


import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import net.ddns.falcoboss.common.cryptography.SHA512;
import net.ddns.falcoboss.common.transport.objects.HTTPHeaderNames;
import net.ddns.falcoboss.common.transport.objects.UsernameAndPasswordTO;

public class ServiceRegisterLoginLogoutTest extends AbstractConnectionTest{
	private String serviceKey = "f80ebc87-ad5c-4b29-9366-5359768df5a1";
	
    @Test
    public void testConnection() throws Exception{
    	String passwordHash = SHA512.hashText("password1");
    	UsernameAndPasswordTO usernameAndPasswordBean = new UsernameAndPasswordTO("username1",passwordHash);	
    	Response response = webTarget.path("login/").request().header(HTTPHeaderNames.SERVICE_KEY, serviceKey).
    			accept(MediaType.APPLICATION_JSON).post(Entity.entity(usernameAndPasswordBean, MediaType.APPLICATION_JSON));
        Assert.assertEquals(200, response.getStatus());
        authToken = new JSONObject(response.readEntity(String.class));
        Assert.assertNotNull(authToken.get("auth_token"));
    }
    
    @After
    public void logout(){
    	Response response = webTarget.path("logout/").request().header(HTTPHeaderNames.SERVICE_KEY, serviceKey).header(HTTPHeaderNames.AUTH_TOKEN, authToken.get("auth_token")).post(null);
    	Assert.assertEquals(204, response.getStatus());
    }
}
