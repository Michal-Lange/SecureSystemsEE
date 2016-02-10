package net.ddns.falcoboss.registrationserver.service.dummy;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;

@Path("/dummyservice")
public class DummyService {

	@GET
	@Produces("text/plain")
	public String dummyService(){
		return "Welcome to Dummy Service";
	}
}
