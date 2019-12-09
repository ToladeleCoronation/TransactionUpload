package com.coronation.upload.services;

import com.coronation.upload.domain.DataColumn;
import com.coronation.upload.domain.DataTable;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Toyin on 7/26/19.
 */
public interface TableService {
    DataTable createTable(DataTable dataTable) throws SQLException;
    String getCreateScript(DataTable dataTable);
    String getCreateScriptInsufficient(DataTable dataTable);
    String getCreateScriptInsuffcientBalance();
    DataTable findById(Long id);
    List<DataTable> findAll();
    DataTable findByName(String name);
    DataTable sanitizeTableStruct(DataTable dataTable);

    boolean noDuplicateColumn(List<DataColumn> columns);

    boolean identifiersValidated(List<DataColumn> columns, boolean canReconcile, boolean amountInField);
    public String getCreateScriptReject(DataTable dataTable);
}
