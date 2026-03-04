package backend.dbModel;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name="providers")
public class Provider {

    // Attributes

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String website;


    // Constructors

    protected Provider(){

    }

    public Provider(String name, String website){
        this.name = name;
        this.website = website;
    }


    // Getters

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWebsite() {
        return website;
    }


    // Setters


    public void setName(String name) {
        this.name = name;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
