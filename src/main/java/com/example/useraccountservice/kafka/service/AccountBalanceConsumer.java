package com.example.useraccountservice.kafka.service;

import com.example.useraccountservice.entity.Account;
import com.example.useraccountservice.enums.transaction.TransactionDirection;
import com.example.useraccountservice.exceptions.NotFoundException;
import com.example.useraccountservice.kafka.dto.BalanceUpdateEvent;
import com.example.useraccountservice.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountBalanceConsumer {

    private final AccountRepository accountRepository;
    private final AccountEventPublisher accountEventPublisher;

    @KafkaListener(topics = "balance-update-events", groupId = "account-group")
    @Transactional
    public void consumerBalanceUpdate(BalanceUpdateEvent event) {
        log.info("processing balance update for account number: {}", event.getAccountNumber());

        Account account = accountRepository.findByAccountNumber(event.getAccountNumber())
                .orElseThrow(() -> new NotFoundException("Account number not found"));

        if (event.getTransactionDirection() == TransactionDirection.CREDIT) {
            account.setBalance(account.getBalance().add(event.getAmount()));
        } else if (event.getTransactionDirection() == TransactionDirection.DEBIT) {
            account.setBalance(account.getBalance().subtract(event.getAmount()));
        }

        accountRepository.save(account);

        BalanceUpdateEvent balanceUpdateEventToPublishToNotification = BalanceUpdateEvent.builder()
                .email(account.getUser().getEmail())
                .firstName(account.getUser().getFirstName())
                .accountNumber(account.getAccountNumber())
                .amount(event.getAmount())
                .transactionDirection(event.getTransactionDirection())
                .reference(event.getReference())
                .description(event.getDescription())
                .currentBalance(account.getBalance())
                .build();

        accountEventPublisher.publishTransactionNotificationEvent(balanceUpdateEventToPublishToNotification);
    }
}
