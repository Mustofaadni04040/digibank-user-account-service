package com.example.useraccountservice.service.impl;

import com.example.useraccountservice.dto.*;
import com.example.useraccountservice.entity.Account;
import com.example.useraccountservice.entity.User;
import com.example.useraccountservice.exceptions.BadRequestException;
import com.example.useraccountservice.exceptions.NotFoundException;
import com.example.useraccountservice.repository.AccountRepository;
import com.example.useraccountservice.repository.UserRepository;
import com.example.useraccountservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse<UserWithAccountDTO> getMyDetails() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Inside getuserdetails user email from authentication is: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Account not found"));

        UserWithAccountDTO userWithAccountDTO = mapToUserWithAccount(user, account);

        return new ApiResponse<>(HttpStatus.OK.value(), "Profile retrieved", userWithAccountDTO);
    }

    @Override
    public ApiResponse<UserWithAccountDTO> searchUser(String email, String accountNumber) {
        log.info("Searching for users");

        User user;
        Account account;
        if(email != null && !email.isBlank()) {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("User with email not found"));

            account = accountRepository.findByUser(user)
                    .orElseThrow(() -> new NotFoundException("Account not found"));
        } else if (accountNumber != null && !accountNumber.isBlank()) {
            account = accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(() -> new NotFoundException("Account not found"));
            user = account.getUser();
        } else {
            throw new BadRequestException("Email and Account Number is required");
        }

        UserWithAccountDTO userWithAccountDTO = mapToUserWithAccount(user, account);

        return new ApiResponse<>(HttpStatus.OK.value(), "Profile retrieved", userWithAccountDTO);
    }

    @Override
    public ApiResponse<Page<UserDTO>> getAllUsers(String roleName, Pageable pageable) {
        log.info("Getting all users");

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending()
        );

        Page<User> userPage;

        if (roleName != null && !roleName.isBlank()) {
            userPage = userRepository.findByRoleName(roleName.toUpperCase(), sortedPageable);
        } else {
            userPage = userRepository.findAll(sortedPageable);
        }

        Page<UserDTO> dtoPage = userPage.map(user -> modelMapper.map(user, UserDTO.class));

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "User fetched successfully",
                dtoPage
        );
    }

    @Override
    public ApiResponse<UserStatisticsDTO> getUserStatistics() {
        long total = userRepository.count();
        long enabled = userRepository.countByEnabledTrue();

        UserStatisticsDTO stats = UserStatisticsDTO
                .builder()
                .totalUsers(total)
                .activeUsers(enabled)
                .inactiveUsers(total - enabled)
                .totalAccounts(accountRepository.count())
                .averageAccountPerUser(accountRepository.count())
                .customersAccount(userRepository.countByRoleName("CUSTOMER"))
                .adminsAccount(userRepository.countByRoleName("ADMIN"))
                .build();

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Stats fetched successfully",
                stats
        );
    }

    @Override
    public ApiResponse<String> toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with ID not found"));

        user.setEnabled(!user.isEnabled());
        userRepository.save(user);

        String status = user.isEnabled() ? "Enabled" : "Disabled";
        log.info("User has been {}", status);

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "User status updated successfully",
                null
        );
    }

    private UserWithAccountDTO mapToUserWithAccount(User user, Account account) {
        return UserWithAccountDTO.builder()
                .user(modelMapper.map(user, UserDTO.class))
                .account(modelMapper.map(account, AccountDTO.class))
                .build();
    }
}
