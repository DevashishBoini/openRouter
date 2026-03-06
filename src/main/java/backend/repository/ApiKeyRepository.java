package backend.repository;

import backend.dbModel.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {

    // JpaRepository<T, ID>         T → Entity type, ID → Primary Key type
    // Spring Data JPA automatically implements basic database CRUD logic methods on ID based, and below methods through methodName parsing

    List<ApiKey> findByUserIdAndDeletedFalse(UUID userId);
    Optional<ApiKey> findByIdAndUserId(UUID id, UUID userId);



}
