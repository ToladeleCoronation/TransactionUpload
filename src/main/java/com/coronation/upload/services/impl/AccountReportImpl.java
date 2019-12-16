package com.coronation.upload.services.impl;

import com.coronation.upload.domain.AccountReport;
import com.coronation.upload.domain.AccountReportInsufficient;
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
        Long insufficient = dataTable.getId();
        List<AccountReport> accountReports = new ArrayList<>();
        for (DataColumn column : dataTable.getColumns()) {
            if (column.getIdentifier()) {
                identifier = column.getName();
            }
        }
        StringBuilder builder = new StringBuilder("select *,").append("count(" + identifier + ") as count from ").
                append(" custom.").
                append(dataTable.getName()).append(" where account_number like '%" + accountNumber +
                "%' or " + identifier + " like '%" + phoneNumber + "%' group by upload_id");
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
                accountReport.setInsufficient(insufficient);
                accountReport.setId(resultSet.getLong("id"));
                accountReport.setPhoneNumber(resultSet.getString(identifier));
                accountReport.setAmount(String.valueOf(resultSet.getBigDecimal("amount")));
                accountReport.setCount(String.valueOf(resultSet.getInt("count")));
                if (resultSet.getString("response_message").contains("Insufficient")) {
                    accountReport.setStatusOfresponseMessage(true);
                }
                accountReport.setStatus(resultSet.getString("response_message"));
                accountReport.setTransactionDate(resultSet.getString("transaction_date"));
                accountReport.setCreatedAt(resultSet.getString("created_at"));
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

    @Override
    public List<AccountReportInsufficient> insufficientBalance(String upload_id, DataTable dataTable) throws SQLException {
        List<AccountReportInsufficient> AccountReportInsufficient = new ArrayList<>();
        System.out.println(upload_id+ " sup nropp");
        StringBuilder builder = new StringBuilder("select * from ").
                append(" custom.").
                append(dataTable.getInsufficientBalance()).append(" where account_number='" + upload_id + "'");
        System.out.println(builder.toString());
        PreparedStatement statement = connection.prepareStatement(builder.toString());
        ResultSet resultSet = statement.executeQuery();

        try {
            while (resultSet.next()) {
                AccountReportInsufficient accountReport = new AccountReportInsufficient();
                if (resultSet.getString("account_number") != null) {
                    accountReport.setAccountNumber(resultSet.getString("account_number"));
                } else {
                    accountReport.setAccountNumber("Account not found");
                }
                accountReport.setLeinRemoved(resultSet.getBoolean("processed"));
                accountReport.setPlacedDate(resultSet.getString("msgdate"));
                accountReport.setPhoneNumber(resultSet.getString("phone_number"));
                accountReport.setAmount(String.valueOf(resultSet.getBigDecimal("amount")));
                accountReport.setLienid(resultSet.getString("appnumber"));
                accountReport.setResponse_message(resultSet.getString("response_message"));
                accountReport.setTransaction_date(resultSet.getString("transaction_date"));

                AccountReportInsufficient.add(accountReport);
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
        return AccountReportInsufficient;
    }
}
