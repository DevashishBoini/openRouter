package backend.dbModel;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name="models")
public class Model {

    // Attributes

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable=false)
    private String slug;


    // Foreign Keys and Relationships

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    private Company company;



    // Constructors

    protected Model(){

    }

    public Model(String name, Company company){

        this.name=name;
        this.company=company;
    }



    // Getters

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public Company getCompany() {
        return company;
    }



    // Setters


    public void setName(String name) {
        this.name = name;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
