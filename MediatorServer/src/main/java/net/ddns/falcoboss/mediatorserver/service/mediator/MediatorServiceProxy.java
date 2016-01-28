package net.ddns.falcoboss.mediatorserver.service.mediator;

import java.io.Serializable;
import java.security.PublicKey;

import javax.ejb.Local;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import net.ddns.falcoboss.common.PartiallySignatureTO;

@Local
@Path("service")
public interface MediatorServiceProxy extends Serializable {
 
    @POST
    @Path("generate-finalization-key")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void generateMediatorKey(
        @Context HttpHeaders httpHeaders, @Suspended AsyncResponse async, final String publicKeyBase64String);
    
    @POST
    @Path("sign-file")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void signFile(
    		@Context HttpHeaders httpHeaders, @Suspended AsyncResponse async, final PartiallySignatureTO partiallySignatureTO); 
}