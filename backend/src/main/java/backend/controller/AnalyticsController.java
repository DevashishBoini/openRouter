package backend.controller;

import backend.annotation.CurrentUser;
import backend.annotation.SuccessMessage;
import backend.dto.Responses.*;
import backend.security.UserPrincipal;
import backend.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * REST controller for analytics endpoints.
 * Provides usage statistics, cost breakdowns, and timeline data.
 */
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * Get usage summary for the authenticated user.
     *
     * @param user authenticated user
     * @param period preset period: "today", "7days", "30days", "all"
     * @param startDate custom start date (optional, overrides period)
     * @param endDate custom end date (optional, used with startDate)
     * @return usage summary with total requests, cost, tokens, and success rate
     */
    @GetMapping("/summary")
    @ResponseStatus(HttpStatus.OK)
    @SuccessMessage("Usage summary retrieved successfully")
    public UsageSummaryResponse getSummary(
            @CurrentUser UserPrincipal user,
            @RequestParam(required = false, defaultValue = "30days") String period,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        log.info("getSummary - userId={}, period={}, startDate={}, endDate={}",
                user.userId(), period, startDate, endDate);

        LocalDateTime[] range = calculateDateRange(period, startDate, endDate);

        return analyticsService.getUsageSummary(user.userId(), range[0], range[1]);
    }

    /**
     * Get timeline data showing requests and costs over time.
     *
     * @param user authenticated user
     * @param period preset period: "today", "7days", "30days", "all"
     * @param startDate custom start date (optional, overrides period)
     * @param endDate custom end date (optional, used with startDate)
     * @return list of daily data points with request counts and costs
     */
    @GetMapping("/timeline")
    @ResponseStatus(HttpStatus.OK)
    @SuccessMessage("Timeline data retrieved successfully")
    public List<TimelineDataPoint> getTimeline(
            @CurrentUser UserPrincipal user,
            @RequestParam(required = false, defaultValue = "30days") String period,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        log.info("getTimeline - userId={}, period={}, startDate={}, endDate={}",
                user.userId(), period, startDate, endDate);

        LocalDateTime[] range = calculateDateRange(period, startDate, endDate);

        return analyticsService.getTimeline(user.userId(), range[0], range[1]);
    }

    /**
     * Get cost breakdown by provider and model.
     *
     * @param user authenticated user
     * @param period preset period: "today", "7days", "30days", "all"
     * @param startDate custom start date (optional, overrides period)
     * @param endDate custom end date (optional, used with startDate)
     * @return cost breakdown with percentages
     */
    @GetMapping("/breakdown")
    @ResponseStatus(HttpStatus.OK)
    @SuccessMessage("Cost breakdown retrieved successfully")
    public CostBreakdownResponse getBreakdown(
            @CurrentUser UserPrincipal user,
            @RequestParam(required = false, defaultValue = "30days") String period,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        log.info("getBreakdown - userId={}, period={}, startDate={}, endDate={}",
                user.userId(), period, startDate, endDate);

        LocalDateTime[] range = calculateDateRange(period, startDate, endDate);

        return analyticsService.getCostBreakdown(user.userId(), range[0], range[1]);
    }

    /**
     * Get usage statistics per API key.
     *
     * @param user authenticated user
     * @param period preset period: "today", "7days", "30days", "all"
     * @param startDate custom start date (optional, overrides period)
     * @param endDate custom end date (optional, used with startDate)
     * @return list of API key statistics
     */
    @GetMapping("/api-keys")
    @ResponseStatus(HttpStatus.OK)
    @SuccessMessage("API key statistics retrieved successfully")
    public List<ApiKeyStatsResponse> getApiKeyStats(
            @CurrentUser UserPrincipal user,
            @RequestParam(required = false, defaultValue = "30days") String period,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        log.info("getApiKeyStats - userId={}, period={}, startDate={}, endDate={}",
                user.userId(), period, startDate, endDate);

        LocalDateTime[] range = calculateDateRange(period, startDate, endDate);

        return analyticsService.getApiKeyStats(user.userId(), range[0], range[1]);
    }

    /**
     * Calculate start and end dates based on period or custom dates.
     *
     * @param period preset period string
     * @param customStartDate custom start date (overrides period)
     * @param customEndDate custom end date
     * @return array with [startDateTime, endDateTime]
     */
    private LocalDateTime[] calculateDateRange(String period, LocalDate customStartDate, LocalDate customEndDate) {
        LocalDateTime start;
        LocalDateTime end;

        // If custom dates provided, use them
        if (customStartDate != null) {
            start = customStartDate.atStartOfDay();
            end = customEndDate != null ?
                    customEndDate.atTime(LocalTime.MAX) :
                    LocalDateTime.now();
        } else {
            // Otherwise, use preset period
            LocalDate today = LocalDate.now();
            end = LocalDateTime.now();

            start = switch (period.toLowerCase()) {
                case "today" -> today.atStartOfDay();
                case "7days" -> today.minusDays(7).atStartOfDay();
                case "30days" -> today.minusDays(30).atStartOfDay();
                case "all" -> LocalDate.of(2020, 1, 1).atStartOfDay(); // Far past date
                default -> today.minusDays(30).atStartOfDay(); // Default to 30 days
            };
        }

        return new LocalDateTime[]{start, end};
    }
}
