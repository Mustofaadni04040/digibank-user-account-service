package com.example.useraccountservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatisticsDTO {

    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;
    private long totalAccounts;
    private long averageAccountPerUser;
    private long customersAccount;
    private long adminsAccount;
}
