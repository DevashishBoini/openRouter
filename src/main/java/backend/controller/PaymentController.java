package backend.controller;

import backend.annotation.CurrentUser;
import backend.annotation.SuccessMessage;
import backend.dto.Responses.OnRampResponse;
import backend.security.UserPrincipal;
import backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller responsible for handling HTTP requests related to payment operations.
 *
 * <p>This controller exposes endpoints for onramping credits to user accounts.
 * Currently provides a simple endpoint to add 1000 credits. Future integration with
 * payment providers like Razorpay is planned.</p>
 *
 * <p>Base URL: <code>/api/v1/payments</code></p>
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Onramp credits to the authenticated user's account.
     * Currently adds a fixed amount of 1000 credits.
     *
     * @param user authenticated user extracted from the security context in {@link UserPrincipal} format
     * @return {@link OnRampResponse} containing transaction details with HTTP status {@code 200 OK}
     */
    @PostMapping("/onramp")
    @ResponseStatus(HttpStatus.OK)
    @SuccessMessage("Credits onramped successfully")
    public OnRampResponse onrampCredits(@CurrentUser UserPrincipal user) {
        log.info("onrampCredits - Request received: userId={}", user.userId());
        OnRampResponse response = paymentService.onrampCredits(user.userId());
        log.info("onrampCredits - Response sent: userId={}, transactionId={}", user.userId(), response.transactionId());
        return response;
    }
}
