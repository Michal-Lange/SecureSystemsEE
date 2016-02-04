package net.ddns.falcoboss.registrationserver.security;


import java.io.IOException;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import net.ddns.falcoboss.common.transport.objects.HTTPHeaderNames;
 
@Provider
@PreMatching
@Local
@Stateless
public class RequestFilter implements ContainerRequestFilter {
 
    private final static Logger log = Logger.getLogger(RequestFilter.class.getName());
 
    @EJB
    AuthenticatorBean authenticatorBean;
    
    @Override
    public void filter(ContainerRequestContext requestContext) 
    		throws IOException {
    	String path = requestContext.getUriInfo().getPath();
        log.info( "Filtering request path: " + path );
        String serviceKey = 
        		requestContext.getHeaderString(HTTPHeaderNames.SERVICE_KEY);
        if (!authenticatorBean.isServiceKeyValid(serviceKey)) {
            requestContext.abortWith(Response.status(
            		Response.Status.UNAUTHORIZED).build());
            return;
        }
 
        if (!path.startsWith( "service/login/" )) {
            String authToken = 
            		requestContext.getHeaderString(
            				HTTPHeaderNames.AUTH_TOKEN);
            if (!authenticatorBean.
            		isAuthTokenValid(serviceKey, authToken)) {
                requestContext.abortWith(
                		Response.status(
                				Response.Status.UNAUTHORIZED).build());
            }
        }
    }
}