package backend.dbModel;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

@Entity
@Table(name = "apikeys")
public class ApiKey {

    // Attributes

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name="api_key_value", unique=true, nullable=false)
    private String apiKeyValue;

    @Column(nullable = false)
    @Positive
    private float creditsConsumed = 0;

    @Column(nullable=false)
    private boolean disabled = false;

    @Column(nullable=false)
    private boolean deleted = false;

    @Column(name="last_used", nullable=true)
    private LocalDateTime lastUsed;


    // Foreign Keys and Relationships

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;



    // Constructors

    protected ApiKey(){

    }


    public ApiKey(String name, String apiKeyValue, User user){
        this.name = name;
        this.apiKeyValue = apiKeyValue;
        this.user = user;
    }



    // Getters

    public UUID getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public User getUser() {
        return user;
    }

    public String getApiKeyValue() {
        return apiKeyValue;
    }

    public float getCreditsConsumed() {
        return creditsConsumed;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public LocalDateTime getLastUsed() {
        return lastUsed;
    }

    // Setters

    public void setName(String name) {
        this.name = name;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setApiKeyValue(String apiKeyValue) {
        this.apiKeyValue = apiKeyValue;
    }

    public void setCreditsConsumed(float creditsConsumed) {
        this.creditsConsumed = creditsConsumed;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void setLastUsed(LocalDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }
}
