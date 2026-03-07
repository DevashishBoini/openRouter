package backend.dbModel;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "model_provider_mappings")

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ModelProviderMapping {

    // Attributes

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false)
    @Positive
    private double inputTokenCost;

    @Column(nullable = false)
    @Positive
    private double outputTokenCost;


    // Foreign Keys and Relationships

    @ManyToOne(optional = false)
    @JoinColumn(name = "model_id", referencedColumnName = "id", nullable = false)
    private Model model;

    @ManyToOne(optional = false)
    @JoinColumn(name = "provider_id", referencedColumnName = "id", nullable = false)
    private Provider provider;
}