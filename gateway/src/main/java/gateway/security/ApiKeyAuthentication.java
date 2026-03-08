package gateway.security;

import gateway.dbModel.ApiKey;
import gateway.dbModel.User;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom authentication object that holds the validated API key and user information.
 */
@Getter
public class ApiKeyAuthentication implements Authentication {

    private final ApiKey apiKey;
    private final User user;
    private boolean authenticated;

    public ApiKeyAuthentication(ApiKey apiKey, User user) {
        this.apiKey = apiKey;
        this.user = user;
        this.authenticated = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return apiKey;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return user.getEmail();
    }
}
