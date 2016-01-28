package net.ddns.falcoboss.javaclient.rest.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.json.JSONObject;
import org.junit.Before;

import net.ddns.falcoboss.common.cryptography.SHA512;

public abstract class AbstractConnectionTest {
    Client client;
    WebTarget webTarget;
    JSONObject authToken;
    
	String password1Hash;
	String password2Hash;
    @Before
    public void initClient() throws Exception {
    	password1Hash = SHA512.hashText("password1");
    	password2Hash = SHA512.hashText("password2");
		
        this.client = ClientBuilder.newClient();
        this.webTarget = this.client.target("http://localhost:8080/registration-server/rest/service/");
    }
}
