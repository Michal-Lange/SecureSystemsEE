package net.ddns.falcoboss.javaclient.rest.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.json.JSONObject;
import org.junit.Before;

public abstract class AbstractConnectionTest {
    Client client;
    WebTarget webTarget;
    JSONObject authToken;
    @Before
    public void initClient() {
        this.client = ClientBuilder.newClient();
        this.webTarget = this.client.target("http://localhost:8080/registration-server/rest/service/");
    }
}
