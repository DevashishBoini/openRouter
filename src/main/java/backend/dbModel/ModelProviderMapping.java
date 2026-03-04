package backend.dbModel;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name="model_provider_mappings")
public class ModelProviderMapping {

    // Attributes

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable=false)
    @Positive
    private float inputTokenCost;

    @Column(nullable=false)
    @Positive
    private float outputTokenCost;


    // Foreign Keys and Relationships

    @ManyToOne(optional=false)
    @JoinColumn(name = "model_id", referencedColumnName = "id", nullable = false)
    private Model model;

    @ManyToOne(optional=false)
    @JoinColumn(name = "provider_id", referencedColumnName = "id", nullable = false)
    private Provider provider;


    // Constructors

    protected ModelProviderMapping(){

    }

    public ModelProviderMapping(float inputTokenCost, float outputTokenCost, Model model, Provider provider){

        this.inputTokenCost = inputTokenCost;
        this.outputTokenCost = outputTokenCost;
        this.model = model;
        this.provider = provider;
    }



    // Getters


    public UUID getId() {
        return id;
    }

    public float getInputTokenCost() {
        return inputTokenCost;
    }

    public float getOutputTokenCost() {
        return outputTokenCost;
    }

    public Model getModel() {
        return model;
    }

    public Provider getProvider() {
        return provider;
    }



    // Setters


    public void setInputTokenCost(float inputTokenCost) {
        this.inputTokenCost = inputTokenCost;
    }

    public void setOutputTokenCost(float outputTokenCost) {
        this.outputTokenCost = outputTokenCost;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }
}
