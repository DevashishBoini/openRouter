package backend.dbModel;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "onRampTransactions")
public class OnRampTransaction {

    // Attributes

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    @Positive
    private float amount;

    @Column(nullable = false)
    private String status;


    // Foreign Keys and Relationships

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;


    // Constructors

    protected OnRampTransaction(){

    }


    public OnRampTransaction(float amount, String status, User user){

        this.amount = amount;
        this.status = status;
        this.user = user;
    }

    // Getters


    public UUID getId() {
        return id;
    }

    public float getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public User getUser() {
        return user;
    }


    // Setters


    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
