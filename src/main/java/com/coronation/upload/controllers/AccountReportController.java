package com.coronation.upload.controllers;

import com.coronation.upload.domain.AccountReport;
import com.coronation.upload.domain.AccountReportInsufficient;
import com.coronation.upload.domain.DataTable;
import com.coronation.upload.services.AccountReportService;
import com.coronation.upload.services.TableService;
import com.coronation.upload.util.GenericUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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


    @GetMapping("/{accountId}/{insufficientId}")
    public ResponseEntity<List<AccountReportInsufficient>> getByRole(@PathVariable("accountId") String accountId,
                                                                     @PathVariable("insufficientId") Long insufficientId) throws SQLException {
        if (accountId == null || insufficientId == null) {
            return ResponseEntity.notFound().build();
        }
        DataTable dataTable = tableService.findById(insufficientId);
        return ResponseEntity.ok(accountReportService.insufficientBalance(accountId, dataTable));
    }

    @GetMapping("/download")
    public @ResponseBody
    byte[] downloadAccountFile(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                               @RequestParam(value = "accountNumber", required = false) String accountNumber,
                               @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                               @RequestParam(value = "id", required = false) Long id) throws SQLException, IOException {
        List<AccountReport> accountReport = new ArrayList<>();

        DataTable dataTable = tableService.findById(id);
        try {


            return GenericUtil.pathToByteArray(GenericUtil.getStoragePath() + accountReportService.getCompleteListOfAccountReport(dataTable, phoneNumber, accountNumber));
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
