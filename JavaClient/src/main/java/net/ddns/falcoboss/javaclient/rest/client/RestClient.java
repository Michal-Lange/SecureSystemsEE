package net.ddns.falcoboss.javaclient.rest.client;

import java.util.concurrent.Future;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import net.ddns.falcoboss.common.HTTPHeaderNames;
import net.ddns.falcoboss.common.Message;
import net.ddns.falcoboss.common.UsernameAndPassword;

public class RestClient {

    Client client;
    private WebTarget webTarget;
    private JSONObject authToken;
    private String serviceKey;
        
    public RestClient() {
    	initClient();
    }
    
	public void initClient() {
		 	SSLContext sc = null;
			try {
				sc = SSLContext.getInstance("TLSv1");
				System.setProperty("https.protocols", "TLSv1");
				TrustManager[] trustAllCerts = { new InsecureTrustManager() };
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        } catch (Exception e) {
				e.printStackTrace();
			}
	        HostnameVerifier allHostsValid = new InsecureHostnameVerifier();
	        this.client = ClientBuilder.newBuilder().sslContext(sc).hostnameVerifier(allHostsValid).build();
    }
	

	public Response login(String username, String password){
		UsernameAndPassword usernameAndPasswordBean = new UsernameAndPassword(username, password);
		Response response =
				this.webTarget.path("login/").request().
				header(HTTPHeaderNames.SERVICE_KEY, this.serviceKey).
				accept(MediaType.APPLICATION_JSON).
				post(Entity.entity(usernameAndPasswordBean, MediaType.APPLICATION_JSON));
	    if(response.getStatus() == 200){
	    	this.authToken = new JSONObject(response.readEntity(String.class));
	    }
	    return response;
	}
	
	public Response login(String login, String password, String serviceKey){
		this.serviceKey = serviceKey;
		UsernameAndPassword usernameAndPasswordBean = new UsernameAndPassword(login, password);
		Response response = 
				this.webTarget.path("login/").request().
				header(HTTPHeaderNames.SERVICE_KEY, this.serviceKey).
				accept(MediaType.APPLICATION_JSON).
				post(Entity.entity(usernameAndPasswordBean, MediaType.APPLICATION_JSON));
	    if(response.getStatus() == 200){
	    	this.authToken = new JSONObject(response.readEntity(String.class));
	    }
	    return response;
	}
	
	public Response logout(){
		Response response = 
				this.webTarget.path("logout/").request().
				header(HTTPHeaderNames.SERVICE_KEY, this.serviceKey).
				header(HTTPHeaderNames.AUTH_TOKEN, this.authToken.get("auth_token")).post(null);
		if(response.getStatus() == 204){
	    	setAuthToken(null);
		}
		return response;
	}
	
	public Response logout(String serviceKey){
		Response response = 
				this.webTarget.path("logout/").request().
				header(HTTPHeaderNames.SERVICE_KEY, serviceKey).
				header(HTTPHeaderNames.AUTH_TOKEN, this.authToken.get("auth_token")).post(null);
    	if(response.getStatus() == 204){
	    	setAuthToken(null);
	    }
	    return response;
	}
	
	public Response sendMessage(Message message){
		Response response =
				this.webTarget.path("send-message/").request().
				header(HTTPHeaderNames.SERVICE_KEY, this.serviceKey).
				header(HTTPHeaderNames.AUTH_TOKEN, this.authToken.get("auth_token")).
				accept(MediaType.APPLICATION_JSON).
				post(Entity.entity(message, MediaType.APPLICATION_JSON));
		return response;
	}
	
	final public Future<Response> reciveMessage() {
		final Future<Response> futureResponse =
				this.webTarget.path("recive-message/").request().
				header(HTTPHeaderNames.SERVICE_KEY, this.serviceKey).
				header(HTTPHeaderNames.AUTH_TOKEN, this.authToken.get("auth_token")).
				accept(MediaType.APPLICATION_JSON).
				async()
				.get(new InvocationCallback<Response>()
				{
					@Override
			        public void completed(Response response) {
						//Message message = response.readEntity(Message.class);
						System.out.println("InvocationCallback completed: reciveMessage method.");
			        }
					@Override
			        public void failed(Throwable throwable) {
						System.err.println("FAILURE!: " + throwable.getLocalizedMessage());
			        }
			    });
		return futureResponse;
	}
	
	final public Future<Response> requestNewKey(String username, String password) {
		UsernameAndPassword usernameAndPasswordBean = new UsernameAndPassword(username, password);
		final Future<Response> futureResponse =
				this.webTarget.path("request-new-key/").request().
				header(HTTPHeaderNames.SERVICE_KEY, this.serviceKey).
				header(HTTPHeaderNames.AUTH_TOKEN, this.authToken.get("auth_token")).
				accept(MediaType.APPLICATION_JSON).
				async()
				.post(Entity.entity(usernameAndPasswordBean, MediaType.APPLICATION_JSON),
				new InvocationCallback<Response>()
				{
					@Override
			        public void completed(Response response) {
						//Message message = response.readEntity(Message.class);
						System.out.println("InvocationCallback completed: requestNewKey method.");
			        }
					@Override
			        public void failed(Throwable throwable) {
						System.err.println("FAILURE!: " + throwable.getLocalizedMessage());
			        }
			    });
		return futureResponse;
	}

	public JSONObject getAuthToken() {
		return this.authToken;
	}

	public void setAuthToken(JSONObject authToken) {
		this.authToken = authToken;
	}

	public String getServiceKey() {
		return this.serviceKey;
	}

	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}

	public WebTarget getWebTarget() {
		return webTarget;
	}

	public void setWebTarget(String webTarget) {
		this.webTarget = this.client.target(webTarget);
	}
	
}
