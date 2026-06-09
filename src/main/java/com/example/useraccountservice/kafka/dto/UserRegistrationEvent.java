package com.example.useraccountservice.kafka.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationEvent {

    private String email;
    private String firstName;
    private String lastName;
    private String accountNumber;
    private String bankName = "DIGI BANK";
}
