package backend.repository;

import backend.dbModel.OnRampTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OnRampTransactionRepository extends JpaRepository<OnRampTransaction, UUID> {
}
