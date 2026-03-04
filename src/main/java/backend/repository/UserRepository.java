package backend.repository;

import backend.dbModel.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // JpaRepository<T, ID>         T → Entity type, ID → Primary Key type
    // Spring Data JPA automatically implements basic database CRUD logic methods on ID based, and below methods through methodName parsing

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

}
