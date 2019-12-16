package com.coronation.upload.services;

import com.coronation.upload.domain.AccountReport;
import com.coronation.upload.domain.AccountReportInsufficient;
import com.coronation.upload.domain.DataTable;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Gold on 9/12/19.
 */
public interface AccountReportService {
    List<AccountReport> getListOfAccountReport(DataTable dataTable, String phoneNumber, String accountNumber) throws SQLException;
    List<AccountReportInsufficient> insufficientBalance(String upload_id, DataTable tableId) throws SQLException;
}
