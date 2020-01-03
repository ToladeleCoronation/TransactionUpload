package com.coronation.upload.controllers;

import com.coronation.upload.domain.Account;
import com.coronation.upload.dto.AccountDetailResponse;
import com.coronation.upload.dto.ApprovalDto;
import com.coronation.upload.exception.ApiException;
import com.coronation.upload.services.AccountService;
import com.coronation.upload.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by Toyin on 4/11/19.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PreAuthorize("hasRole('INITIATOR')")
    @PostMapping("/number/{accountNumber}")
    public ResponseEntity<Account> create(@PathVariable("accountNumber") String accountNumber) {
        if (accountService.findByAccountNumber(accountNumber) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            try {
                AccountDetailResponse response = accountService.fetchAccountDetails(accountNumber);
                if (response.getResponseCode().equals(Constants.ACCOUNT_RESPONSE_CODE)) {
                    if (response.getStatus() != null) {
                        return ResponseEntity.unprocessableEntity().build();
                    } else {
                        Account account = response.toAccount();
                        return ResponseEntity.ok(accountService.create(account));
                    }
                } else {
                    return ResponseEntity.notFound().build();
                }
            } catch (ApiException ex) {
                return ResponseEntity.status(HttpStatus.valueOf(ex.getStatusCode())).build();
            }
        }
    }


    @PreAuthorize("hasRole('AUTHORIZER')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<Account> approve(@PathVariable("id") Long id,
                                           @RequestBody @Valid ApprovalDto approvalDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            Account account = accountService.findById(id);
            if (account == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(accountService.approve(account, approvalDto));
            }
        }
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<Account> fetchByAccountNumber(@PathVariable("accountNumber") String accountNumber) {
        Account account = accountService.findByAccountNumber(accountNumber);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }

    @GetMapping("/number/{accountNumber}/api")
    public ResponseEntity<Account> fetchByAccountNumberApi(@PathVariable("accountNumber") String accountNumber) {
        try {
            AccountDetailResponse response = accountService.fetchAccountDetails(accountNumber);
            if (response.getResponseCode().equals(Constants.ACCOUNT_RESPONSE_CODE)) {
                return ResponseEntity.ok(response.toAccount());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ApiException ex) {
            return ResponseEntity.status(HttpStatus.valueOf(ex.getStatusCode())).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAll() {
        return ResponseEntity.ok(accountService.findAll());
    }
}
