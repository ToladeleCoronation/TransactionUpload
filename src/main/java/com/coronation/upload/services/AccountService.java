package com.coronation.upload.services;

import com.coronation.upload.domain.Account;
import com.coronation.upload.dto.AccountDetailResponse;
import com.coronation.upload.dto.ApprovalDto;
import com.coronation.upload.exception.ApiException;

import java.util.List;

/**
 * Created by Toyin on 4/8/19.
 */
public interface AccountService {
    Account create(Account account);
    Account approve(Account account, ApprovalDto approvalDto);
    Account findById(Long id);
    Account findByAccountNumber(String accountNumber);
    AccountDetailResponse fetchAccountDetails(String accountNumber) throws ApiException;
    List<Account> findAll();
}
