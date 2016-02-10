package net.ddns.falcoboss.registrationserver.service.register;

import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.ddns.falcoboss.common.transport.objects.MessageTO;

public class RegisterServiceHelper {
	
	public static void send(AsyncResponse asyncResponse, MessageTO message) {
		message.setSendDate(new Date());
		Response response = Response.ok(message, MediaType.APPLICATION_JSON).build();
		asyncResponse.resume(response);
	}
	
	public void asyncResponse(AsyncResponse asyncResponse, String responseMessage, Response.Status responseStatus)	{
		JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
		jsonObjBuilder.add("message", responseMessage);
		JsonObject jsonObj = jsonObjBuilder.build();
		Response response = Response.status(responseStatus).entity(jsonObj.toString()).build();
		asyncResponse.resume(response);
	}
}
