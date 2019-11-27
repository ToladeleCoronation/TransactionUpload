package com.coronation.upload.services.impl;

import com.coronation.upload.domain.Account;
import com.coronation.upload.domain.enums.GenericStatus;
import com.coronation.upload.dto.AccountDetailRequest;
import com.coronation.upload.dto.AccountDetailResponse;
import com.coronation.upload.dto.ApprovalDto;
import com.coronation.upload.exception.ApiException;
import com.coronation.upload.repo.AccountRepository;
import com.coronation.upload.services.AccountService;
import com.coronation.upload.util.Constants;
import com.coronation.upload.util.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Toyin on 4/9/19.
 */
@Service
public class AccountServiceImpl implements AccountService {
    private AccountRepository accountRepository;
    private Utilities utilities;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, Utilities utilities) {
        this.accountRepository = accountRepository;
        this.utilities = utilities;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Account create(Account account) {
        return accountRepository.saveAndFlush(account);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Account approve(Account account, ApprovalDto approvalDto) {
        if (approvalDto.getApprove()) {
            account.setStatus(GenericStatus.ACTIVE);
        } else {
            account.setRejectReason(approvalDto.getReason());
            account.setStatus(GenericStatus.REJECTED);
        }
        return accountRepository.saveAndFlush(account);
    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public Account findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public AccountDetailResponse fetchAccountDetails(String accountNumber) throws ApiException {
        AccountDetailRequest accountDetailRequest = new AccountDetailRequest(accountNumber);
        ResponseEntity<AccountDetailResponse> response = utilities.getAccountDetails(accountDetailRequest);
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            ApiException exception = new ApiException("An error occurred while fetching account");
            exception.setStatusCode(response.getStatusCode().value());
            throw exception;
        } else if (response.getBody().getResponseCode().equals(Constants.ACCOUNT_RESPONSE_CODE)) {
            return response.getBody();
        } else {
            ApiException exception = new ApiException("Account was not found");
            exception.setStatusCode(HttpStatus.NOT_FOUND.value());
            throw exception;
        }
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }
}
