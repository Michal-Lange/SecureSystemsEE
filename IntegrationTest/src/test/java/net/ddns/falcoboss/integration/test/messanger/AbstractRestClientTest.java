package net.ddns.falcoboss.integration.test.messanger;

import org.junit.Before;

import net.ddns.falcoboss.common.cryptography.SHA512;
import net.ddns.falcoboss.javaclient.rest.client.RestClient;

public abstract class AbstractRestClientTest {
	RestClient restClient;
	RestClient restClient2;
	RestClient restClient3;
	
	String password1Hash;
	String password2Hash;
	String password3Hash;
	
    @Before
    public void initClient() throws Exception {
		password1Hash = SHA512.hashText("password1");
		password2Hash = SHA512.hashText("password2");
		password3Hash = SHA512.hashText("password3");
		
    	restClient = new RestClient();
  		restClient.initClient();
  		restClient.setWebTarget("http://localhost:8080/registration-server/rest/service/");
  		
  		restClient2 = new RestClient();
  		restClient2.initClient();
  		restClient2.setWebTarget("http://localhost:8080/registration-server/rest/service/");
  		
  		restClient3 = new RestClient();
  		restClient3.initClient();
  		restClient3.setWebTarget("https://localhost:8181/registration-server/rest/service/");
    }
}
