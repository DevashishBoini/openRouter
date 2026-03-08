package gateway.repository;

import gateway.dbModel.ModelProviderMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModelProviderMappingRepository extends JpaRepository<ModelProviderMapping, UUID> {

    @Query("SELECT m FROM ModelProviderMapping m WHERE m.model.id = :modelId")
    List<ModelProviderMapping> findByModelId(@Param("modelId") UUID modelId);
}
