package com.coronation.upload.repo;

import com.coronation.upload.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * Created by Toyin on 4/8/19.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByAccountNumber(String accountNumber);
}
