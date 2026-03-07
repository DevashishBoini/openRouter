package backend.service;

import backend.dbModel.OnRampTransaction;
import backend.dbModel.User;
import backend.dto.Responses.OnRampResponse;
import backend.exception.ResourceNotFoundException;
import backend.repository.OnRampTransactionRepository;
import backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final UserRepository userRepository;
    private final OnRampTransactionRepository onRampTransactionRepository;

    private static final double ONRAMP_AMOUNT = 1000.0;
    private static final String TRANSACTION_STATUS = "SUCCESS";

    @Transactional
    public OnRampResponse onrampCredits(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        double currentBalance = user.getCredits();
        double newBalance = currentBalance + ONRAMP_AMOUNT;
        user.setCredits(newBalance);
        userRepository.save(user);

        OnRampTransaction transaction = OnRampTransaction.builder()
                .amount(ONRAMP_AMOUNT)
                .status(TRANSACTION_STATUS)
                .user(user)
                .build();

        OnRampTransaction savedTransaction = onRampTransactionRepository.save(transaction);

        log.info("Onramped {} credits for user {}. New balance: {}", ONRAMP_AMOUNT, userId, newBalance);

        return new OnRampResponse(
                savedTransaction.getId(),
                ONRAMP_AMOUNT,
                newBalance
        );
    }
}
