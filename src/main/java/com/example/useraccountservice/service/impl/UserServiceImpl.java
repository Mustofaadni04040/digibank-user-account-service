package com.example.useraccountservice.service.impl;

import com.example.useraccountservice.dto.*;
import com.example.useraccountservice.entity.Account;
import com.example.useraccountservice.entity.User;
import com.example.useraccountservice.exceptions.NotFoundException;
import com.example.useraccountservice.repository.AccountRepository;
import com.example.useraccountservice.repository.UserRepository;
import com.example.useraccountservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        return null;
    }

    @Override
    public ApiResponse<Page<UserDTO>> getAllUsers(String roleName, Pageable pageable) {
        return null;
    }

    @Override
    public ApiResponse<UserStatisticsDTO> getUserStatistics() {
        return null;
    }

    @Override
    public ApiResponse<String> toggleUserStatus() {
        return null;
    }

    private UserWithAccountDTO mapToUserWithAccount(User user, Account account) {
        return UserWithAccountDTO.builder()
                .user(modelMapper.map(user, UserDTO.class))
                .account(modelMapper.map(account, AccountDTO.class))
                .build();
    }
}
