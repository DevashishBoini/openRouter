package backend.dbModel;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "onRampTransactions")

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OnRampTransaction {

    // Attributes

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false)
    @Positive
    private double amount;

    @Column(nullable = false)
    private String status;


    // Foreign Keys and Relationships

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
}