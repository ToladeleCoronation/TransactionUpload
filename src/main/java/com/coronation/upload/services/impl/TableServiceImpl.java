package com.coronation.upload.services.impl;

import com.coronation.upload.domain.DataColumn;
import com.coronation.upload.domain.DataTable;
import com.coronation.upload.domain.enums.DataType;
import com.coronation.upload.repo.ColumnRepository;
import com.coronation.upload.repo.TableRepository;
import com.coronation.upload.services.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Toyin on 7/26/19.
 */
@Service
public class TableServiceImpl implements TableService {
    private TableRepository tableRepository;
    private ColumnRepository columnRepository;
    private Connection connection;

    @Autowired
    public TableServiceImpl(TableRepository tableRepository,
                            ColumnRepository columnRepository, Connection connection) {
        this.tableRepository = tableRepository;
        this.columnRepository = columnRepository;
        this.connection = connection;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public DataTable createTable(DataTable dataTable) throws SQLException {
        List<DataColumn> columns = saveColumns(dataTable.getColumns());
        dataTable.setColumns(columns);
        dataTable = saveTable(dataTable);
        String createScript = getCreateScript(dataTable);
        String createScriptInsufficientBalance = getCreateScriptInsufficient(dataTable);
        String createAccountPhone=getCreateScriptAccountPhone(dataTable);
        createTable(createScript);
        createTableInsufficient(createScriptInsufficientBalance);
        createPhoneAccount(createAccountPhone);


        return dataTable;
    }

    private void createTable(String tableSql) throws SQLException {
        Statement statement = connection.createStatement();
        System.out.println(tableSql);
        statement.executeUpdate(tableSql);
    }

    private void createTableInsufficient(String tableSql) throws SQLException {
        Statement statement = connection.createStatement();
        System.out.println(tableSql);
        statement.executeUpdate(tableSql);
    }

    private void createPhoneAccount(String tableSql) throws SQLException {
        Statement statement = connection.createStatement();
        System.out.println(tableSql);
        statement.executeUpdate(tableSql);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    List<DataColumn> saveColumns(List<DataColumn> columns) {
        int count = 0;
        for (DataColumn column: columns) {
            column.setOrder(count);
            ++count;
        }
        return columnRepository.saveAll(columns);
    }

    @Transactional(propagation = Propagation.REQUIRED)
     DataTable saveTable(DataTable dataTable) {
        dataTable.setInsufficientBalance(dataTable.getName()+"_insufficientBalance");
        dataTable.setPhoneAccountHolder(dataTable.getName()+"_PhoneAccountHolder");
        return tableRepository.saveAndFlush(dataTable);
    }

    @Override
    public String getCreateScript(DataTable dataTable) {
        StringBuilder builder = new StringBuilder("create table custom.");
        builder.append(dataTable.getName()).append("( ").append(getCreateColumnString(dataTable.getColumns())).
                append(" )");
        return builder.toString();
    }

    @Override
    public String getCreateScriptInsufficient(DataTable dataTable) {
        StringBuilder builder = new StringBuilder("create table custom.");
        builder.append(dataTable.getInsufficientBalance()).append("( ").append(getCreateScriptInsuffcientBalance()).
                append(" )");
        return builder.toString();
    }


    public String getCreateScriptAccountPhone(DataTable dataTable) {
        StringBuilder builder = new StringBuilder("create table custom.");
        builder.append(dataTable.getPhoneAccountHolder()).append("( ").append(getCreateScriptAccountPhone()).
                append(" )");
        return builder.toString();
    }

    private String getCreateColumnString(List<DataColumn> columns) {
        StringBuilder builder = new StringBuilder("id bigint(20) NOT NULL AUTO_INCREMENT, ");
        columns.forEach(c -> {
            builder.append(c.getName()).append(" ");
            if (c.getDataType().equals(DataType.VARCHAR) && c.getDataSize() != null && c.getDataSize() > 0) {
                builder.append(c.getDataType().getValue().
                        replace("255", c.getDataSize().toString()));
            } else {
                builder.append(c.getDataType().getValue());
            }
            if (c.getUnique() || c.getReconciliation()) {
                builder.append(" UNIQUE");
            }
            if (c.getReconciliation() || c.getIdentifier() || c.getAmountField()) {
                builder.append(" NOT NULL");
            }
            builder.append(", ");
        });
        builder.append("upload_id bigint(20) NOT NULL, approved bit(1) NOT NULL DEFAULT 0, " +
                "processed bit(1) NOT NULL DEFAULT 0,trxn_id varchar(50), transaction_id varchar(50),account_number varchar(15),debit_type bit(1) NOT NULL DEFAULT 0,transaction_date varchar(50), retry_flag varchar(2), response_code varchar(10), " +
                "response_message varchar(255),lien_response_description varchar(255),lien_appcode varchar(200),lien_date varchar(50), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id`) ");
        return builder.toString();
    }

    @Override
    public DataTable findById(Long id) {
        return tableRepository.findById(id).orElse(null);
    }

    @Override
    public List<DataTable> findAll() {
        return tableRepository.findAll();
    }

    @Override
    public DataTable findByName(String name) {
        return tableRepository.findByNameEquals(name);
    }

    @Override
    public DataTable sanitizeTableStruct(DataTable dataTable) {
        dataTable.setName(dataTable.getName().replaceAll("\\s", "_").toLowerCase());
        dataTable.getColumns().forEach(c ->
            c.setName(c.getName().replaceAll("\\s", "_").toLowerCase()));
        return dataTable;
    }

    @Override
    public boolean noDuplicateColumn(List<DataColumn> columns) {
        List<DataColumn> nonDuplicate = new ArrayList<>();
        columns.forEach(c -> {
            if (nonDuplicate.stream().anyMatch(d -> d.getName().equals(c.getName()))) {
                return;
            } else {
                nonDuplicate.add(c);
            }
        });
        return columns.size() == nonDuplicate.size();
    }

    @Override
    public boolean identifiersValidated(List<DataColumn> columns, boolean canReconcile, boolean amountInField) {
        List<DataColumn> identifier = columns.stream().
                filter(c -> c.getIdentifier()).collect(Collectors.toList());
        if (identifier.size() != 1 || identifier.get(0).getReconciliation()
                || (amountInField && identifier.get(0).getAmountField())) {
            return false;
        }
        if (canReconcile) {
            identifier = columns.stream().
                    filter(c -> c.getReconciliation()).collect(Collectors.toList());
            if (identifier.size() != 1 || (amountInField && identifier.get(0).getAmountField())) {
                return false;
            }
        }

        if (amountInField) {
            identifier = columns.stream().
                    filter(c -> c.getAmountField()).collect(Collectors.toList());
            if (identifier.size() != 1) {
                return false;
            } else {
                identifier.get(0).setDataType(DataType.BIG_DECIMAL);
            }
        }

        return true;
    }

    @Override
    public String getCreateScriptInsuffcientBalance() {
        StringBuilder builder = new StringBuilder("id bigint(20) NOT NULL AUTO_INCREMENT, ");

        builder.append("upload_id bigint(20) NOT NULL, " +
                "processed bit(1) NOT NULL DEFAULT 0,phone_number varchar(15),account_number varchar(15),credit_account varchar(20), narration varchar(50),amount DECIMAL(13,0), " +
                "appnumber varchar(30),trxn_id varchar(30),debit_type varchar(15), mode varchar(15),msgdate varchar(25),status_message varchar(50),response_description varchar(50),response_code varchar(20),response_message varchar(50),transaction_date varchar(30),tranid varchar(10),trxnfer_id varchar(50)," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id`) ");
        return builder.toString();
    }

    public String getCreateScriptAccountPhone() {
        StringBuilder builder = new StringBuilder("id bigint(20) NOT NULL AUTO_INCREMENT, ");

        builder.append("phone_number varchar(15),account_number varchar(15)," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id`) ");
        return builder.toString();
    }
}
