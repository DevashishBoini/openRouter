package backend.security;

public class UserPrincipal {

    private final String email;

    public UserPrincipal(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}