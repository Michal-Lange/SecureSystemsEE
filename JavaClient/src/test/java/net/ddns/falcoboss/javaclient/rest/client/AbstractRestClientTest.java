package net.ddns.falcoboss.javaclient.rest.client;

import org.junit.Before;

import net.ddns.falcoboss.common.cryptography.SHA512;

public abstract class AbstractRestClientTest {
	RestClient restClient;
	RestClient restClient2;
	
	String password1Hash;
	String password2Hash;
	
    @Before
    public void initClient() throws Exception {
		password1Hash = SHA512.hashText("password1");
		password2Hash = SHA512.hashText("password2");

    	restClient = new RestClient();
  		restClient.initClient();
  		restClient.setWebTarget("http://localhost:8080/registration-server/rest/service/");
  		
  		restClient2 = new RestClient();
  		restClient2.initClient();
  		restClient2.setWebTarget("http://localhost:8080/registration-server/rest/service/");
    }
}
