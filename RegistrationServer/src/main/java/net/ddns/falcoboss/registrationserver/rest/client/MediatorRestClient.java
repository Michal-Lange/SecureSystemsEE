package net.ddns.falcoboss.registrationserver.rest.client;

import java.security.PublicKey;
import java.util.concurrent.Future;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.ddns.falcoboss.common.HTTPHeaderNames;

public class MediatorRestClient {
	Client client;
    private WebTarget webTarget;
     
    public MediatorRestClient() {
    	initClient();
    }
    
	public void initClient() {
        this.client = ClientBuilder.newClient();
    }
	
	final public Future<Response> requestNewFinalizationKey(String userServiceKey, String publicKeyBase64String) {
		final Future<Response> futureResponse =
				this.webTarget.path("generate-finalization-key/").request().
				header(HTTPHeaderNames.SERVICE_KEY, userServiceKey).
				accept(MediaType.APPLICATION_JSON).
				async()
				.post(Entity.entity(publicKeyBase64String, MediaType.APPLICATION_JSON),
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
	
	public WebTarget getWebTarget() {
		return webTarget;
	}

	public void setWebTarget(String webTarget) {
		this.webTarget = this.client.target(webTarget);
	}
}
