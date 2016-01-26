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

import net.ddns.falcoboss.common.HTTPHeaderNames;
 
@Provider
@PreMatching
@Local
@Stateless
public class RequestFilter implements ContainerRequestFilter {
 
    private final static Logger log = Logger.getLogger(RequestFilter.class.getName());
 
    @EJB
    AuthenticatorBean authenticatorBean;
    
    @Override
    public void filter(ContainerRequestContext requestCtx) throws IOException {
    	String path = requestCtx.getUriInfo().getPath();
        log.info( "Filtering request path: " + path );
        String serviceKey = requestCtx.getHeaderString(HTTPHeaderNames.SERVICE_KEY);
        if (!authenticatorBean.isServiceKeyValid(serviceKey)) {
            requestCtx.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
 
        if (!path.startsWith( "service/login/" )) {
            String authToken = requestCtx.getHeaderString(HTTPHeaderNames.AUTH_TOKEN);
            if (!authenticatorBean.isAuthTokenValid(serviceKey, authToken)) {
                requestCtx.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            }
        }
    }
}