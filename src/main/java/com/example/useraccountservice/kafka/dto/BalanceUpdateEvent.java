package com.example.useraccountservice.kafka.dto;

import com.example.useraccountservice.enums.Currency;
import com.example.useraccountservice.enums.transaction.TransactionDirection;
import com.example.useraccountservice.enums.transaction.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BalanceUpdateEvent {

    // used by accountservice
    private String accountNumber;
    private BigDecimal amount;
    private TransactionDirection transactionDirection;
    private TransactionStatus transactionStatus;
    private String reference;
    private Currency currency;

    // used by notificationservice
    private String email;
    private String firstName;
    private BigDecimal currentBalance;
    private String description;
}
