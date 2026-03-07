package backend.dbModel;

import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Table(name = "conversations")

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Conversation {

    // Attributes

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false)
    private String input;

    @Column(nullable = false)
    @PositiveOrZero
    private int inputTokenCount;

    @Column(nullable = false)
    private String output;

    @Column(nullable = false)
    @PositiveOrZero
    private int outputTokenCount;


    // Foreign Keys and Relationships

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "api_key_id", referencedColumnName = "id", nullable = false)
    private ApiKey apiKey;

    @ManyToOne(optional = false)
    @JoinColumn(name = "model_provider_mapping_id", referencedColumnName = "id", nullable = false)
    private ModelProviderMapping modelProviderMapping;
}