package gateway.repository;

import gateway.dbModel.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {

    @Query("SELECT a FROM ApiKey a WHERE a.deleted = false AND a.disabled = false")
    List<ApiKey> findAllActiveApiKeys();

    @Query("SELECT a FROM ApiKey a WHERE a.id = :id AND a.deleted = false AND a.disabled = false")
    ApiKey findActiveApiKeyById(@Param("id") UUID id);
}
