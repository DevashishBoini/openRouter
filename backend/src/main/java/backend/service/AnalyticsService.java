package backend.service;

import backend.dto.Responses.*;
import backend.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final ConversationRepository conversationRepository;

    public UsageSummaryResponse getUsageSummary(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        Long totalRequests = conversationRepository.countByUserIdAndDateRange(userId, startDate, endDate);

        if (totalRequests == null || totalRequests == 0) {
            return new UsageSummaryResponse(0L, 0L, BigDecimal.ZERO, 0.0, 100.0);
        }

        Long totalTokens = conversationRepository.sumTotalTokensByUserIdAndDateRange(userId, startDate, endDate);
        Double totalCostDouble = conversationRepository.sumCostByUserIdAndDateRange(userId, startDate, endDate);
        Double avgLatency = conversationRepository.avgLatencyByUserIdAndDateRange(userId, startDate, endDate);

        BigDecimal totalCost = totalCostDouble != null ?
                BigDecimal.valueOf(totalCostDouble).setScale(4, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

        // Since conversations are only saved on success, success rate is 100%
        Double successRate = 100.0;

        log.info("getUsageSummary - userId={}, requests={}, cost={}", userId, totalRequests, totalCost);

        return new UsageSummaryResponse(
                totalRequests,
                totalTokens != null ? totalTokens : 0L,
                totalCost,
                avgLatency != null ? Math.round(avgLatency * 100.0) / 100.0 : 0.0,
                successRate
        );
    }

    public List<TimelineDataPoint> getTimeline(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = conversationRepository.findTimelineByUserIdAndDateRange(userId, startDate, endDate);

        List<TimelineDataPoint> timeline = new ArrayList<>();
        for (Object[] row : results) {
            LocalDate date = ((Date) row[0]).toLocalDate();
            Long requestCount = ((Number) row[1]).longValue();
            BigDecimal totalCost = row[2] != null ?
                    BigDecimal.valueOf(((Number) row[2]).doubleValue()).setScale(4, RoundingMode.HALF_UP) :
                    BigDecimal.ZERO;
            Long totalTokens = row[3] != null ? ((Number) row[3]).longValue() : 0L;

            timeline.add(new TimelineDataPoint(date, requestCount, totalCost, totalTokens));
        }

        log.info("getTimeline - userId={}, dataPoints={}", userId, timeline.size());
        return timeline;
    }

    public CostBreakdownResponse getCostBreakdown(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        // Get total cost for percentage calculation
        Double totalCostDouble = conversationRepository.sumCostByUserIdAndDateRange(userId, startDate, endDate);
        double totalCost = totalCostDouble != null ? totalCostDouble : 0.0;

        // By Provider
        List<Object[]> providerResults = conversationRepository.findBreakdownByProvider(userId, startDate, endDate);
        List<CostBreakdownResponse.BreakdownItem> byProvider = new ArrayList<>();

        for (Object[] row : providerResults) {
            String name = (String) row[0];
            Long requestCount = ((Number) row[1]).longValue();
            double cost = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;

            Double percentage = totalCost > 0 ? (cost / totalCost) * 100 : 0.0;

            byProvider.add(new CostBreakdownResponse.BreakdownItem(
                    name,
                    requestCount,
                    BigDecimal.valueOf(cost).setScale(4, RoundingMode.HALF_UP),
                    Math.round(percentage * 100.0) / 100.0
            ));
        }

        // By Model
        List<Object[]> modelResults = conversationRepository.findBreakdownByModel(userId, startDate, endDate);
        List<CostBreakdownResponse.BreakdownItem> byModel = new ArrayList<>();

        for (Object[] row : modelResults) {
            String name = (String) row[0];
            Long requestCount = ((Number) row[1]).longValue();
            double cost = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;

            Double percentage = totalCost > 0 ? (cost / totalCost) * 100 : 0.0;

            byModel.add(new CostBreakdownResponse.BreakdownItem(
                    name,
                    requestCount,
                    BigDecimal.valueOf(cost).setScale(4, RoundingMode.HALF_UP),
                    Math.round(percentage * 100.0) / 100.0
            ));
        }

        log.info("getCostBreakdown - userId={}, providers={}, models={}", userId, byProvider.size(), byModel.size());
        return new CostBreakdownResponse(byProvider, byModel);
    }

    public List<ApiKeyStatsResponse> getApiKeyStats(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = conversationRepository.findStatsByApiKey(userId, startDate, endDate);

        List<ApiKeyStatsResponse> stats = new ArrayList<>();
        for (Object[] row : results) {
            UUID apiKeyId = (UUID) row[0];
            String apiKeyName = (String) row[1];
            Long requestCount = ((Number) row[2]).longValue();
            BigDecimal totalCost = row[3] != null ?
                    BigDecimal.valueOf(((Number) row[3]).doubleValue()).setScale(4, RoundingMode.HALF_UP) :
                    BigDecimal.ZERO;
            LocalDateTime lastUsed = row[4] != null ? (LocalDateTime) row[4] : null;

            stats.add(new ApiKeyStatsResponse(
                    apiKeyId,
                    apiKeyName,
                    requestCount,
                    totalCost,
                    lastUsed
            ));
        }

        log.info("getApiKeyStats - userId={}, apiKeys={}", userId, stats.size());
        return stats;
    }
}
