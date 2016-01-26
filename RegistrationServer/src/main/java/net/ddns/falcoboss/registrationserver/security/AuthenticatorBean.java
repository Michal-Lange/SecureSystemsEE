package net.ddns.falcoboss.registrationserver.security;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.security.auth.login.LoginException;

import net.ddns.falcoboss.registrationserver.usermanagement.User;
import net.ddns.falcoboss.registrationserver.usermanagement.UserBean;

@Singleton
@LocalBean
public class AuthenticatorBean {
	
	private final static Logger log = Logger.getLogger(RequestFilter.class.getName());
	
	@EJB
	UserBean userBean;
	
	private Map<String, String> authorizationTokensStorage = new HashMap<String, String>();
       
    public String login(String serviceKey, String username, String password) throws LoginException {
    	User userFoundByServiceKey = userBean.findByServiceKey(serviceKey);
    	if(userFoundByServiceKey != null) {
    		String usernameMatch = userFoundByServiceKey.getUsername();
    		if(username.equals(usernameMatch))
    		{
        		User userFoundByUsername = userBean.find(username);
        		if (password.equals(userFoundByUsername.getPassword()) ) {
        			String authToken = UUID.randomUUID().toString();
        			authorizationTokensStorage.put(authToken, username);
        			return authToken;
        		}
    		}
    	}
    	throw new LoginException( "Don't Come Here Again!" );
    }
    
    public boolean isAuthTokenValid(String serviceKey, String authToken) {
        if (isServiceKeyValid(serviceKey) ) {
        	User userFoundByServiceKey = userBean.findByServiceKey(serviceKey);
        	String usernameMatch1 = userFoundByServiceKey.getUsername();
             if (authorizationTokensStorage.containsKey(authToken) ) {
                String usernameMatch2 = authorizationTokensStorage.get(authToken);
                 if (usernameMatch1.equals(usernameMatch2) ) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isServiceKeyValid(String serviceKey) {
    	User userFoundByServiceKey = userBean.findByServiceKey(serviceKey);
    	if(userFoundByServiceKey!=null) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
 
    public void logout(String serviceKey, String authToken) throws GeneralSecurityException {
    	User userFoundByServiceKey = userBean.findByServiceKey(serviceKey);
    	if (userFoundByServiceKey != null) {
            String usernameMatch1 = userFoundByServiceKey.getUsername();
            if (authorizationTokensStorage.containsKey(authToken) ) {
                String usernameMatch2 = authorizationTokensStorage.get(authToken);
                if (usernameMatch1.equals(usernameMatch2)) {
                    /**
                     * When a client logs out, the authentication token will be
                     * remove and will be made invalid.
                     */
                    authorizationTokensStorage.remove(authToken);
                    return;
                }
            }
        }
        throw new GeneralSecurityException("Invalid service key and authorization token match.");
    }
    
    public boolean isUsernameAndPasswordValid(String serviceKey, String username, String password) throws LoginException {
    	User userFoundByServiceKey = userBean.findByServiceKey(serviceKey);
    	if(userFoundByServiceKey != null) {
    		String usernameMatch = userFoundByServiceKey.getUsername();
    		if(username.equals(usernameMatch))
    		{
        		User userFoundByUsername = userBean.find(username);
        		if (password.equals(userFoundByUsername.getPassword()) ) {
        			return true;
        		}
    		}
    	}
    	throw new LoginException( "Don't Come Here Again!" );
    }
}
