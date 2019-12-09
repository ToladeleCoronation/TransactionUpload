package com.coronation.upload.controllers;

import com.coronation.upload.domain.AccountReport;
import com.coronation.upload.domain.DataTable;
import com.coronation.upload.services.AccountReportService;
import com.coronation.upload.services.TableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gold on 9/12/19.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/accountReport")
public class AccountReportController {

    private AccountReportService accountReportService;
    private TableService tableService;

    public AccountReportController(AccountReportService accountReportService, TableService tableService) {
        this.accountReportService = accountReportService;
        this.tableService = tableService;
    }

    @GetMapping
    public ResponseEntity<List<AccountReport>> listUser(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                        @RequestParam(value = "accountNumber", required = false) String accountNumber,
                                                        @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                                                        @RequestParam(value = "id", required = false) Long id) throws SQLException {
        System.out.println(id + " got here again");
        List<AccountReport> accountReport = new ArrayList<>();
        if (id == null) {
            return ResponseEntity.ok(accountReport);
        }
        DataTable dataTable = tableService.findById(id);
        return ResponseEntity.ok(accountReportService.getListOfAccountReport(dataTable, phoneNumber, accountNumber));


    }


}
