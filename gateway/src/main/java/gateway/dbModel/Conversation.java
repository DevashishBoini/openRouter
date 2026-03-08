package gateway.dbModel;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
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

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String input;

    @Column(nullable = false)
    @PositiveOrZero
    private int inputTokenCount;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String output;

    @Column(nullable = false)
    @PositiveOrZero
    private int outputTokenCount;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private double creditsCost;

    @Column
    private Integer responseTimeMs;

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
