package com.coronation.upload.services;

import com.coronation.upload.domain.*;
import com.coronation.upload.dto.LienResponse;
import com.coronation.upload.dto.TransferRequestt;
import com.coronation.upload.dto.TransferResponses;
import com.coronation.upload.exception.ApiException;
import com.coronation.upload.exception.InvalidDataException;
import com.google.common.collect.Multimap;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Toyin on 7/26/19.
 */
public interface TransactionService {
    DataUpload uploadFile(MultipartFile[] files, Task task) throws InvalidDataException, IOException;

    DataUpload rejectFile(DataUpload dataUpload);

    DataUpload approveFile(DataUpload dataUpload) throws IOException, InvalidFormatException, InvalidDataException, SQLException;

    Map<String, String> reconciliationMap(InputStream inputStream) throws IOException;

    Set<Task> getDueTasks();

    List<Task> getAllTasks();

    DataUpload findUploadById(Long id);
    void insertRecordAccountPhone(Task task) throws SQLException;

    Page<DataUpload> findAllUploads(BooleanExpression expression, Pageable pageable);

    void insertData(List<DataColumn> columnData, DataUpload dataUpload,Task task) throws SQLException;

    String getAccountNumber(Object identifier, Task task, String accountNumber) throws Exception;

    ResponseEntity<TransferResponses> processTransaction(List <Long> ids, String account, Task task,String trxnid) throws SQLException;

    boolean reconciled(String identifier, String value, Map<String, String> reconciliationList);

    Map<String, List<Long>> getUnprocessedData(DataTable dataTable) throws SQLException, InvalidDataException;

    void updateProcessedData(Long ids, DataTable dataTable, TransferResponses response,LienResponse responses) throws SQLException;

    void updateProcessedDataA(Long ids, DataTable dataTable,TransferResponses response,String val,String lienResponse,String appcode,String accountNumber) throws SQLException;

    void insertUnprocessedData(List <Long> id, DataTable table, String accountNumber, String phoneNumber, BigDecimal amount,String creditAccount,String narration,LienResponse lienResponse,String trxnId) throws SQLException;

    @Transactional(propagation = Propagation.REQUIRED)
    TransferResponses transfer(TransferRequestt transferRequest) throws ApiException;

    void processTask(Task task);

    String correctAccountNumber(Multimap<String, String> map, String identifier);

    List<DataTable> getTableNames();

    void processInsufficientBalance(DataTable dataTable) throws InvalidDataException, SQLException;

    List<InsufficientBalance> getUnprocessedDinsertataInsufficientFund(DataTable table) throws SQLException, InvalidDataException;

    ResponseEntity<LienResponse> processTransactionInsufficient(BigDecimal amount,String accountNumber,String val);
    ResponseEntity<LienResponse> removeLien(InsufficientBalance insufficientBalance);

    void updateProcessedInsufficientBalance(InsufficientBalance insufficientBalance, DataTable dataTable
            , TransferResponses transferResponses,String trxn)throws SQLException, InvalidDataException ;

    ResponseEntity<TransferResponses> processTransactionInsufficient(InsufficientBalance insufficientBalance) throws SQLException;

}
