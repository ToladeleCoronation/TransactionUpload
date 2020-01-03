package com.coronation.upload.services.impl;

import com.coronation.upload.domain.*;
import com.coronation.upload.services.AccountReportService;
import com.coronation.upload.util.ExcelSaver;
import com.coronation.upload.util.GenericUtil;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
        System.out.println(upload_id + " sup nropp");
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

    @Override
    public String getCompleteListOfAccountReport(DataTable dataTable, String phoneNumber, String accountNumber) throws SQLException, IOException {
        String identifier = null;
        Long insufficient = dataTable.getId();
        String accountNum = null;
        String numAccount = null;
        List<AccountReport> accountReports = new ArrayList<>();
        List<List<String>> savedData = new ArrayList<>();

        for (DataColumn column : dataTable.getColumns()) {
            if (column.getIdentifier()) {
                identifier = column.getName();
            }
        }
        DataColumn val = new DataColumn();
        val.setName("account_number");

        DataColumn val1 = new DataColumn();
        val1.setName("response_code");

        DataColumn val2 = new DataColumn();
        val2.setName("response_message");


        DataColumn val3 = new DataColumn();
        val3.setName("Account Number");

        DataColumn val4 = new DataColumn();
        val4.setName("Debit Status");

        DataColumn val5 = new DataColumn();
        val5.setName("Debit Status Message");

        StringBuilder builder = new StringBuilder("select ");
        List<DataColumn> myDataColumn = dataTable.getColumns();
        List<DataColumn> myDataHeader = dataTable.getColumns();

        myDataColumn.add(val);
        myDataColumn.add(val1);
        myDataColumn.add(val2);
        for (DataColumn dataColumn : myDataColumn) {
            builder.append(dataColumn.getName() + ",");
        }
        builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append(" from ").
                append("custom.").
                append(dataTable.getName()).append(" where account_number like '%" + accountNumber +
                "%' or " + identifier + " like '%" + phoneNumber + "%'");
        System.out.println("i am the identifier: " + builder);
        PreparedStatement statement = connection.prepareStatement(builder.toString());
        ResultSet resultSet = statement.executeQuery();

        try {
            while (resultSet.next()) {
                List<String> data = new ArrayList<>();
                accountNum = resultSet.getString("account_number");
                myDataColumn.forEach(c -> {
                    try {
                        data.add(resultSet.getString(c.getName()));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                savedData.add(data);

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
        accountNum = LocalDate.now() + "_" + accountNum + ".xlsx";
        myDataHeader.add(val3);
        myDataHeader.add(val4);
        myDataHeader.add(val5);

        ExcelSaver.createLogFile(myDataHeader, savedData,
                GenericUtil.getStoragePath() + accountNum);
        return accountNum;

    }


}
