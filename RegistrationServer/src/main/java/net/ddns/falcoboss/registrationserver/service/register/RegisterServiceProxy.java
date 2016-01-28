package net.ddns.falcoboss.registrationserver.service.register;

import java.io.Serializable;

import javax.ejb.Local;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.ddns.falcoboss.common.Message;
import net.ddns.falcoboss.common.PartiallySignatureTO;
import net.ddns.falcoboss.common.UsernameAndPassword;

@Local
@Path("service")
public interface RegisterServiceProxy extends Serializable {
 
    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(
        @Context HttpHeaders httpHeaders, final UsernameAndPassword usernameAndPasswordBean);
    
    @POST
    @Path("send-message")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendMessage(@Context HttpHeaders httpHeaders, final Message message); 
    
    @GET
    @Path("recive-message")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void reciveMessage(@Context HttpHeaders httpHeaders, @Suspended AsyncResponse async);
    
    @POST
    @Path("logout")
    public Response logout(@Context HttpHeaders httpHeaders);
    
    @POST
    @Path("request-new-key")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void requestNewKey(@Context HttpHeaders httpHeaders, @Suspended AsyncResponse async, final UsernameAndPassword usernameAndPasswordBean);

    @POST
    @Path("sign-file")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void signFile(@Context HttpHeaders httpHeaders, @Suspended AsyncResponse async, final PartiallySignatureTO partiallySignatureTO);
}
