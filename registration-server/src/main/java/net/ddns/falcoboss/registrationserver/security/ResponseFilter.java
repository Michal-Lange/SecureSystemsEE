package net.ddns.falcoboss.registrationserver.security;

import java.io.IOException;
import java.util.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

import net.ddns.falcoboss.common.transport.objects.HTTPHeaderNames;

@Provider
@PreMatching
public class ResponseFilter implements ContainerResponseFilter {
 
    private final static Logger log = Logger.getLogger(ResponseFilter.class.getName());
 
    @Override
    public void filter(ContainerRequestContext requestCtx, ContainerResponseContext responseContext) throws IOException {
 
        log.info("Filtering REST Response");
 
        responseContext.getHeaders().add(
        		"Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add(
        		"Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add(
        		"Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        responseContext.getHeaders().add(
        		"Access-Control-Allow-Headers", 
        		HTTPHeaderNames.SERVICE_KEY + ", " + 
        		HTTPHeaderNames.AUTH_TOKEN);
    }
}
