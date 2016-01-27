package net.ddns.falcoboss.javaclient.rest.client;

import org.junit.Before;

public abstract class AbstractRestClientTest {
	RestClient restClient;
	RestClient restClient2;
    @Before
    public void initClient() {
    	restClient = new RestClient();
  		restClient.initClient();
  		restClient.setWebTarget("http://localhost:8080/registration-server/rest/service/");
  		
  		restClient2 = new RestClient();
  		restClient2.initClient();
  		restClient2.setWebTarget("http://localhost:8080/registration-server/rest/service/");
    }
}
