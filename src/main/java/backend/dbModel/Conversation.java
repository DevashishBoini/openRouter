package backend.dbModel;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name="conversations")
public class Conversation {

    // Attributes

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String input;

    @Column(nullable = false)
    @Positive
    private int inputTokenCount;

    @Column(nullable = false)
    private String output;

    @Column(nullable = false)
    @Positive
    private int outputTokenCount;


    // Foreign Keys and Relationships

    @ManyToOne(optional=false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne(optional=false)
    @JoinColumn(name = "api_key_id", referencedColumnName = "id", nullable = false)
    private ApiKey apiKey;

    @ManyToOne(optional=false)
    @JoinColumn(name = "model_provider_mapping_id", referencedColumnName = "id", nullable = false)
    private ModelProviderMapping modelProviderMapping;


    // Constructors

    protected Conversation() {

    }

    private Conversation(String input, int inputTokenCount, String output, int outputTokenCount, User user, ApiKey apiKey, ModelProviderMapping modelProviderMapping){
        this.input = input;
        this.inputTokenCount = inputTokenCount;
        this.output = output;
        this.outputTokenCount = outputTokenCount;
        this.user = user;
        this.apiKey = apiKey;
        this.modelProviderMapping = modelProviderMapping;
    }



    // Getters


    public UUID getId() {
        return id;
    }

    public String getInput() {
        return input;
    }

    public int getInputTokenCount() {
        return inputTokenCount;
    }

    public String getOutput() {
        return output;
    }

    public int getOutputTokenCount() {
        return outputTokenCount;
    }

    public User getUser() {
        return user;
    }

    public ApiKey getApiKey() {
        return apiKey;
    }

    public ModelProviderMapping getModelProviderMapping() {
        return modelProviderMapping;
    }



    // Setters


    public void setInput(String input) {
        this.input = input;
    }

    public void setInputTokenCount(int inputTokenCount) {
        this.inputTokenCount = inputTokenCount;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public void setOutputTokenCount(int outputTokenCount) {
        this.outputTokenCount = outputTokenCount;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setApiKey(ApiKey apiKey) {
        this.apiKey = apiKey;
    }

    public void setModelProviderMapping(ModelProviderMapping modelProviderMapping) {
        this.modelProviderMapping = modelProviderMapping;
    }
}
