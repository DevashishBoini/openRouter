package backend.repository;

import backend.dbModel.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    List<Conversation> findByUserId(UUID userId);

    List<Conversation> findByApiKeyId(UUID apiKeyId);

    // ========== Analytics Queries ==========

    // Summary statistics
    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.user.id = :userId AND c.createdAt >= :startDate AND c.createdAt <= :endDate")
    Long countByUserIdAndDateRange(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(c.inputTokenCount + c.outputTokenCount) FROM Conversation c WHERE c.user.id = :userId AND c.createdAt >= :startDate AND c.createdAt <= :endDate")
    Long sumTotalTokensByUserIdAndDateRange(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(c.creditsCost) FROM Conversation c WHERE c.user.id = :userId AND c.createdAt >= :startDate AND c.createdAt <= :endDate")
    Double sumCostByUserIdAndDateRange(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG(c.responseTimeMs) FROM Conversation c WHERE c.user.id = :userId AND c.createdAt >= :startDate AND c.createdAt <= :endDate AND c.responseTimeMs IS NOT NULL")
    Double avgLatencyByUserIdAndDateRange(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Timeline - daily aggregation
    @Query("SELECT CAST(c.createdAt AS date), COUNT(c), SUM(c.creditsCost), SUM(c.inputTokenCount + c.outputTokenCount) " +
            "FROM Conversation c WHERE c.user.id = :userId AND c.createdAt >= :startDate AND c.createdAt <= :endDate " +
            "GROUP BY CAST(c.createdAt AS date) ORDER BY CAST(c.createdAt AS date)")
    List<Object[]> findTimelineByUserIdAndDateRange(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Cost breakdown by provider
    @Query("SELECT p.name, COUNT(c), SUM(c.creditsCost) " +
            "FROM Conversation c " +
            "JOIN c.modelProviderMapping mpm " +
            "JOIN mpm.provider p " +
            "WHERE c.user.id = :userId AND c.createdAt >= :startDate AND c.createdAt <= :endDate " +
            "GROUP BY p.name ORDER BY SUM(c.creditsCost) DESC")
    List<Object[]> findBreakdownByProvider(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Cost breakdown by model
    @Query("SELECT m.name, COUNT(c), SUM(c.creditsCost) " +
            "FROM Conversation c " +
            "JOIN c.modelProviderMapping mpm " +
            "JOIN mpm.model m " +
            "WHERE c.user.id = :userId AND c.createdAt >= :startDate AND c.createdAt <= :endDate " +
            "GROUP BY m.name ORDER BY SUM(c.creditsCost) DESC")
    List<Object[]> findBreakdownByModel(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Per API Key statistics
    @Query("SELECT c.apiKey.id, c.apiKey.name, COUNT(c), SUM(c.creditsCost), MAX(c.createdAt) " +
            "FROM Conversation c " +
            "WHERE c.user.id = :userId AND c.createdAt >= :startDate AND c.createdAt <= :endDate " +
            "GROUP BY c.apiKey.id, c.apiKey.name ORDER BY SUM(c.creditsCost) DESC")
    List<Object[]> findStatsByApiKey(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
