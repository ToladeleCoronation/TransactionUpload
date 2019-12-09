package com.coronation.upload.services.impl;

import com.coronation.upload.domain.AccountReport;
import com.coronation.upload.domain.DataColumn;
import com.coronation.upload.domain.DataTable;
import com.coronation.upload.services.AccountReportService;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gold on 9/12/19.
 */
@Service
public class AccountReportImpl implements AccountReportService {
    private Connection connection;

    public AccountReportImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<AccountReport> getListOfAccountReport(DataTable dataTable, String phoneNumber, String accountNumber) throws SQLException {
        String identifier = null;
        List<AccountReport> accountReports = new ArrayList<>();
        for (DataColumn column : dataTable.getColumns()) {
            if (column.getIdentifier()) {
                identifier = column.getName();
                System.out.println(identifier + " also her");
            }
        }
        StringBuilder builder = new StringBuilder("select *,").append("count(" + identifier + ") as count from ").
                append(" custom.").
                append(dataTable.getName()).append(" where account_number like '%" + accountNumber +
                "%' or " + identifier + " like '%" + phoneNumber + "%' group by " + identifier + "");
        System.out.println(builder.toString() + " dude big head");
        PreparedStatement statement = connection.prepareStatement(builder.toString());
        ResultSet resultSet = statement.executeQuery();

        try {
            while (resultSet.next()) {
                AccountReport accountReport = new AccountReport();
                if (resultSet.getString("account_number") != null) {
                    accountReport.setAccountNumber(resultSet.getString("account_number"));
                } else {
                    accountReport.setAccountNumber("Account not found");
                }

                accountReport.setPhoneNumber(resultSet.getString(identifier));
                accountReport.setAmount(String.valueOf(resultSet.getBigDecimal("amount")));
                accountReport.setCount(String.valueOf(resultSet.getInt("count")));
                accountReport.setStatus(resultSet.getString("response_message"));
                accountReport.setTransactionDate(resultSet.getString("transaction_date"));
                accountReports.add(accountReport);
            }
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return accountReports;
    }
}
