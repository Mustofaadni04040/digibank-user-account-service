package com.example.useraccountservice.service.impl;

import com.example.useraccountservice.dto.AccountDTO;
import com.example.useraccountservice.dto.ApiResponse;
import com.example.useraccountservice.entity.Account;
import com.example.useraccountservice.entity.User;
import com.example.useraccountservice.enums.AccountStatus;
import com.example.useraccountservice.exceptions.NotFoundException;
import com.example.useraccountservice.repository.AccountRepository;
import com.example.useraccountservice.repository.UserRepository;
import com.example.useraccountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse<AccountDTO> getMyAccount() {
        log.info("Fetching account for logged in user");
        String userEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication().getName());

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Account not found"));

        AccountDTO accountDTO = modelMapper.map(account, AccountDTO.class);

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Account Retrieved",
                accountDTO
        );
    }

    @Override
    public ApiResponse<AccountDTO> getAccountNumber(String accountNumber) {
        return null;
    }

    @Override
    public ApiResponse<AccountDTO> changeAccountStatus(String accountNumber, AccountStatus status) {
        return null;
    }

    @Override
    public ApiResponse<AccountDTO> getAllAccount(Pageable pageable) {
        return null;
    }
}
