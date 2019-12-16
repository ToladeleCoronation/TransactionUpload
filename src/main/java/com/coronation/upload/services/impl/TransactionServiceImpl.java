package com.coronation.upload.services.impl;

import com.coronation.upload.domain.*;
import com.coronation.upload.domain.DataTable;
import com.coronation.upload.domain.enums.DriverType;
import com.coronation.upload.domain.enums.GenericStatus;
import com.coronation.upload.domain.enums.JobPeriod;
import com.coronation.upload.dto.*;
import com.coronation.upload.exception.ApiException;
import com.coronation.upload.exception.InvalidDataException;
import com.coronation.upload.repo.*;
import com.coronation.upload.services.TransactionService;
import com.coronation.upload.util.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;

import javax.mail.MessagingException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Created by Toyin on 7/26/19.
 */
@Service
public class TransactionServiceImpl implements TransactionService {
    private Connection connection;
    private Utilities utilities;
    private UploadRepository uploadRepository;
    private ScheduleRepository scheduleRepository;
    private TaskRepository taskRepository;
    private TableRepository tableRepository;
    private MailBuilder mailBuilder;
    private UserRepository userRepository;
    private Mailer mailer;
    @Value("${app.secret.key}")
    private String appKey;

    @Value("${app.secret.lienId}")
    private String lienKey;

    @Value("${app.secret.reasonCode}")
    private String reasonCode;

    @Value("${app.smsMessage}")
    private String smsMessage;

    private Logger logger = LogManager.getLogger(TransactionServiceImpl.class);

    @Autowired
    public TransactionServiceImpl(Connection connection, Utilities utilities, UploadRepository uploadRepository, ScheduleRepository scheduleRepository, TaskRepository taskRepository, TableRepository tableRepository, MailBuilder mailBuilder,
                                  Mailer mailer, UserRepository userRepository) {
        this.connection = connection;
        this.utilities = utilities;
        this.uploadRepository = uploadRepository;
        this.scheduleRepository = scheduleRepository;
        this.taskRepository = taskRepository;
        this.tableRepository = tableRepository;
        this.mailBuilder = mailBuilder;
        this.mailer = mailer;
        this.userRepository = userRepository;
    }


    @Override
    public DataUpload uploadFile(MultipartFile[] files, Task task) throws InvalidDataException, IOException {
        DataUpload upload = new DataUpload();
        upload.setTask(task);

        if (task.getReconcile()) {
            if (files.length != 2) {
                throw new InvalidDataException("File not found for reconciliation");
            }
        }
        String fileName = GenericUtil.pathToFileName(files[0].getOriginalFilename());
        fileName = GenericUtil.appendDateToFileName(fileName);
        upload.setUploadFile(fileName);
        GenericUtil.writeBytesToFile(files[0].getBytes(), GenericUtil.getStoragePath() + fileName);

        if (task.getReconcile()) {
            fileName = GenericUtil.pathToFileName(files[1].getOriginalFilename());
            fileName = GenericUtil.appendDateToFileName(fileName);
            upload.setReconcileFile(fileName);
            GenericUtil.writeBytesToFile(files[1].getBytes(), GenericUtil.getStoragePath() + fileName);
        }

        XSSFWorkbook workbook = new XSSFWorkbook(files[0].getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        Row row;
        DataFormatter formatter = new DataFormatter();
        Map<String, String> reconcileMap = null;
        Set<String> reconcileSet = new HashSet<>();

        List<List<String>> savedData = new ArrayList<>();
        List<List<String>> duplicateData = new ArrayList<>();
        List<List<String>> unmatchedData = new ArrayList<>();
        List<List<String>> invalidData = new ArrayList<>();
        List<List<String>> exceptionData = new ArrayList<>();

        if (task.getReconcile()) {
            reconcileMap = reconciliationMap(files[1].getInputStream());
        }

        upload = uploadRepository.saveAndFlush(upload);

        while (rowIterator.hasNext()) {
            row = rowIterator.next();
            if (row.getRowNum() < 1) {
                continue;
            }
            String identifier = null;
            String reconcileField = null;
            String amountField = null;

            List<DataColumn> columns = task.getTable().getColumns();
            for (DataColumn column : columns) {
                column.setValue(formatter.formatCellValue(row.getCell(column.getOrder())).trim());
                if (column.getIdentifier()) {
                    identifier = column.getValue();
                    System.out.println(identifier + " i am here");
                } else if (column.getReconciliation()) {
                    reconcileField = column.getValue();
                    System.out.println("reconciliation file is : " + reconcileField);
                } else if (column.getAmountField()) {
                    amountField = column.getValue();
                }
            }

            if (identifier.isEmpty() || (task.getReconcile() &&
                    reconcileField.isEmpty()) || (task.getAmountInData() && amountField.isEmpty())) {
                if (upload.getInvalid() == null) {
                    upload.setInvalid(1);
                } else {
                    upload.setInvalid(upload.getInvalid() + 1);
                }
                insertRowData(columns, invalidData);
                continue;
            }

            if (task.getReconcile()) {
                if (reconcileSet.contains(reconcileField)) {
                    if (upload.getDuplicate() == null) {
                        upload.setDuplicate(1);
                    } else {
                        upload.setDuplicate(upload.getDuplicate() + 1);
                    }
                    insertRowData(columns, duplicateData);
                    continue;
                } else {
                    reconcileSet.add(reconcileField);
                }
            }

            if (task.getReconcile() && !reconciled(reconcileField, identifier, reconcileMap)) {
                if (upload.getUnmatched() == null) {
                    upload.setUnmatched(1);
                } else {
                    upload.setUnmatched(upload.getUnmatched() + 1);
                }
                insertRowData(columns, unmatchedData);
            } else {
                try {
                    insertData(columns, upload, task);
                    if (upload.getSuccess() == null) {
                        upload.setSuccess(1);
                    } else {
                        upload.setSuccess(upload.getSuccess() + 1);
                    }
                    insertRowData(columns, savedData);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    logger.error(ex.getMessage());
                    if (upload.getExceptions() == null) {
                        upload.setExceptions(1);
                    } else {
                        upload.setExceptions(upload.getExceptions() + 1);
                    }
                    insertRowData(columns, exceptionData);
                }
            }

            if (upload.getCount() == null) {
                upload.setCount(1);
            } else {
                upload.setCount(upload.getCount() + 1);
            }
        }

        try {
            if (!savedData.isEmpty()) {
                upload.setSuccessFile(saveExcelData(upload, savedData, Constants.SAVED_PREFIX));
            }
            if (!duplicateData.isEmpty()) {
                upload.setDuplicateFile(saveExcelData(upload, duplicateData,
                        Constants.DUPLICATE_PREFIX));
            }
            if (!invalidData.isEmpty()) {
                upload.setInvalidFile(saveExcelData(upload, invalidData, Constants.INVALID_PREFIX));
            }
            if (!exceptionData.isEmpty()) {
                upload.setExceptionsFile(saveExcelData(upload, exceptionData,
                        Constants.EXCEPTIONS_PREFIX));
            }
            if (!unmatchedData.isEmpty()) {
                upload.setUnmatchedFile(saveExcelData(upload, unmatchedData,
                        Constants.UNMATCHED_PREFIX));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage());
        }

        return uploadRepository.saveAndFlush(upload);
    }

    @Override
    public DataUpload rejectFile(DataUpload dataUpload) throws SQLException {
        dataUpload.setStatus(GenericStatus.REJECTED);
        dataUpload.setModifiedAt(LocalDateTime.now());
        StringBuilder builder = new StringBuilder("Insert into ").append(dataUpload.getTask().getTable().getRejected())
                .append(" select * from ").append(dataUpload.getTask().getTable().getName())
                .append(" where upload_id='" + dataUpload.getId() + "'");
        PreparedStatement statement = connection.prepareStatement(builder.toString());
        statement.executeUpdate();
        StringBuilder builder1 = new StringBuilder("delete from ").append(dataUpload.getTask().getTable().getName())
                .append(" where upload_id='" + dataUpload.getId() + "' ");
        PreparedStatement statements = connection.prepareStatement(builder1.toString());
        statements.executeUpdate();
        return uploadRepository.saveAndFlush(dataUpload);
    }

    @Override
    public DataUpload approveFile(DataUpload dataUpload) throws IOException, InvalidFormatException,
            InvalidDataException, SQLException {
        StringBuilder builder = new StringBuilder("UPDATE custom.").
                append(dataUpload.getTask().getTable().getName()).append(" SET approved = ?")
                .append(" WHERE upload_id = ?");

        PreparedStatement statement = connection.prepareStatement(builder.toString());
        statement.setBoolean(1, true);
        statement.setLong(2, dataUpload.getId());
        statement.executeUpdate();

        dataUpload.setModifiedAt(LocalDateTime.now());
        dataUpload.setStatus(GenericStatus.ACTIVE);
        return uploadRepository.saveAndFlush(dataUpload);
    }

    private String saveExcelData(DataUpload dataUpload, List<List<String>> data, String filePrefix)
            throws IOException {
        String fileName = filePrefix + "_" + dataUpload.getUploadFile();
        ExcelSaver.createLogFile(dataUpload.getTask().getTable().getColumns(), data,
                GenericUtil.getStoragePath() + fileName);
        return fileName;
    }

    private String saveExcelDataLog(DataUpload dataUpload, List<LogExporter> data, String filePrefix)
            throws IOException {
        filePrefix = filePrefix + "_" + LocalDateTime.now().toString();
        filePrefix = filePrefix.replaceAll(":", "").replaceAll(",", "").replaceAll("-", "");
        ExcelSaver.createLogFileReport(Constants.logHeader(), data,
                GenericUtil.getStoragePath() + filePrefix);
        System.out.println(filePrefix + " i got here again and again");
        return filePrefix;
    }

    private void insertRowData(List<DataColumn> dataColumns, List<List<String>> dataList) {
        List<String> data = new ArrayList<>();
        dataColumns.forEach(c -> data.add(c.getValue()));
        dataList.add(data);
    }


    private List<LogExporter> logData(Task task, DataUpload dataUpload) throws SQLException {
        System.out.println("hello sir " + dataUpload.getId());
        List<LogExporter> logExporter = new ArrayList<>();
        String identifier = null;
        String narration=null;
        for (DataColumn column : task.getTable().getColumns()) {
            if (column.getIdentifier()) {
                identifier = column.getName();
            }

            if(column.getNarration())
            {
                narration=column.getName();
            }
        }
        PreparedStatement statement = null;

        System.out.println("hi :" + identifier + " again " + task.getTable().getName());

        StringBuilder builder = new StringBuilder("select *,").append("count(" + identifier + ") as count from ").
                append(" custom.").
                append(task.getTable().getName()).append(" where upload_id=" + dataUpload.getId() + " group by " + identifier + "");
        System.out.println(builder.toString() + "  aggaaaainn");

        try {
            statement = connection.prepareStatement(builder.toString());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                LogExporter logExporter1 = new LogExporter();
                if (resultSet.getString("account_number") != null) {
                    logExporter1.setAccountNumber(resultSet.getString("account_number"));
                    logExporter1.setStatus(resultSet.getString("response_message"));
                    logExporter1.setResponse_code(resultSet.getString("response_code"));
                } else {
                    logExporter1.setAccountNumber("ACCOUNT NOT FOUND");
                    logExporter1.setStatus("ACCOUNT NOT FOUND");
                    logExporter1.setResponse_code("NOT PROCESSED");
                }
                logExporter1.setCount(String.valueOf(resultSet.getInt("count")));
                logExporter1.setPhoneNumber(resultSet.getString(identifier));
                logExporter1.setAmount(String.valueOf(resultSet.getBigDecimal("amount")));
                logExporter1.setNarration(resultSet.getString(narration));
                logExporter1.setDateDebitted(resultSet.getString("transaction_date"));
                logExporter.add(logExporter1);
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

        return logExporter;


    }

    @Override
    public Map<String, String> reconciliationMap(InputStream inputStream) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        Row row;
        DataFormatter formatter = new DataFormatter();
        Map<String, String> map = new HashMap<>();
        while (rowIterator.hasNext()) {
            row = rowIterator.next();
            if (row.getRowNum() < 1) {
                continue;
            }
            map.put(formatter.formatCellValue(row.getCell(0)).trim(), formatter.formatCellValue(row.getCell(1)).trim());
        }
        return map;
    }

    @Override
    public Set<Task> getDueTasks() {
        LocalDateTime localDateTime = LocalDateTime.now();
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();
        JobPeriod dayOfWeek = JobPeriod.getPeriodFromWeekDay(localDateTime.getDayOfWeek());
        Set<Schedule> schedules = new HashSet<>();
        if (LocalDate.now().lengthOfMonth() == localDateTime.getDayOfMonth()) {
            List<Schedule> monthlySchedules = scheduleRepository.findByCronParams(minute, hour, JobPeriod.END_OF_MONTH);
            schedules.addAll(monthlySchedules);
        }
        schedules.addAll(scheduleRepository.findByCronParams(minute, hour, JobPeriod.DAILY));
        Set<Task> tasks = new HashSet<>();
        for (Schedule schedule : schedules) {
            tasks.addAll(taskRepository.findByScheduleId(schedule.getId()));
        }
        return tasks;
    }

    @Override
    public DataUpload findUploadById(Long id) {
        return uploadRepository.findById(id).orElse(null);
    }

    @Override
    public Page<DataUpload> findAllUploads(BooleanExpression expression, Pageable pageable) {
        return uploadRepository.findAll(expression, pageable);
    }

    @Override
    public void insertData(List<DataColumn> columnData, DataUpload dataUpload, Task task) throws SQLException {
        String insertScript = generateInsertScript(columnData, dataUpload.getTask().getTable());
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(insertScript);
            statement.setLong(1, dataUpload.getId());
            statement.setBoolean(2, task.getBulkPayment());
            int count = 3;
            for (DataColumn column : columnData) {
                switch (column.getDataType()) {
                    case LONG:
                        statement.setLong(count, Long.parseLong(column.getValue()));
                        break;
                    case DOUBLE:
                        statement.setDouble(count, Double.parseDouble(column.getValue()));
                        break;
                    case BOOLEAN:
                        statement.setBoolean(count, Boolean.valueOf(column.getValue()));
                        break;
                    case INTEGER:
                        statement.setInt(count, Integer.parseInt(column.getValue()));
                        break;
                    case VARCHAR:
                        statement.setString(count, column.getValue());
                        break;
                    case LOCAL_DATE:
                        statement.setObject(count, LocalDate.parse(column.getValue()));
                        break;
                    case BIG_DECIMAL:
                        statement.setBigDecimal(count, new BigDecimal(column.getValue()));
                        break;
                    case LOCAL_DATETIME:
                        statement.setObject(count, GenericUtil.dateTimeFromString(column.getValue()));
                        break;
                }
                ++count;
            }
            statement.executeUpdate();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public String correctAccountNumber(Multimap<String, String> val, String identifier) {
        String phoneVal = null;
        String account = null;
        Map<String, String> currentNumberHolder = new HashMap<>();
        identifier = identifier.trim().replaceAll("[(-),+]", "");
        identifier = identifier.replaceAll(" ", "");
        for (Map.Entry<String, String> acctDetails : val.entries()) {
            phoneVal = acctDetails.getValue();
            if (identifier.length() > 9 && phoneVal.length() > 9) {
                identifier = identifier.substring(identifier.length() - 10);
                phoneVal = phoneVal.substring(phoneVal.length() - 10);

                if (phoneVal.equals(identifier) && acctDetails.getKey().length() == 10) {

                    account = acctDetails.getKey();
                    break;
                }

            }
        }


        return account;
    }


    @Override
    public String getAccountNumber(Object identifier, Task task, String testVal) throws Exception {
        String accountNumber = null;
//        Connection connection = null;
        PreparedStatement statement = null;
        try {
            StringBuilder builder = new StringBuilder("SELECT phone_number,account_number").
                    append(" FROM custom.").append(task.getTable().getPhoneAccountHolder()).append(" WHERE phone_number = ?");
            statement = connection.prepareStatement(builder.toString());
            statement.setString(1, testVal);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                accountNumber = resultSet.getString("account_number");
            }
            return accountNumber;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }


    }

    private String getAccountFetchSql(Task task) {
        StringBuilder builder = new StringBuilder("SELECT ").append(task.getAccountColumn()).append(" ,").append(task.getFetchColumn()).append(" FROM ").
                append(task.getFetchTable()).append(" WHERE ").
                append(task.getFetchColumn()).append(" like ?");
        return builder.toString();
    }

    private String getAccountFetch(Task task) {
        StringBuilder builder = new StringBuilder("SELECT ").append(task.getAccountColumn()).append(" ,").append(task.getFetchColumn()).append(" FROM ").
                append(task.getFetchTable());
        return builder.toString();
    }

    @Override
    public ResponseEntity<TransferResponses> processTransaction(List<Long> ids, String debitAccount, Task task, String trxnId)
            throws SQLException {
        GenericUtil genericUtil = new GenericUtil();
        TransferRequestt trxnRequest = new TransferRequestt();
        List<Recs> reequest = new ArrayList<>();
        Recs rec = new Recs();
        Recs recDeb = new Recs();
        TransferRequestt request = new TransferRequestt();
        TransferRequestt debit = new TransferRequestt();
        TrnAmt credit = new TrnAmt();
        TrnAmt debit1 = new TrnAmt();
        AcctId cre = new AcctId();
        AcctId deb = new AcctId();
        cre.setAcctId(task.getAccount().getAccountNumber());
        BigDecimal amount = new BigDecimal(0);
        rec.setAcctId(cre);
        rec.setCreditDebitFlg(Constants.credit);
        rec.setSerialNum(Constants.serialNum1);


        //request.setDebitAccountNumber(debitAccount);

        if (task.getAmountInData()) {
            credit.setAmountValue(getDebitSum(ids, task));
            debit1.setAmountValue(getDebitSum(ids, task));


        } else {
            credit.setAmountValue(task.getCharge().multiply(new BigDecimal(ids.size())));
            debit1.setAmountValue(task.getCharge().multiply(new BigDecimal(ids.size())));


        }
        credit.setCurrencyCode(Constants.accountCode);
        rec.setTrnAmt(credit);
        rec.setTrnParticulars(getNarration(task.getTable(),ids.get(0)));
        //request.setUniqueIdentifier(generateTransactionId());

        reequest.add(rec);
        deb.setAcctId(debitAccount);
        recDeb.setAcctId(deb);
        recDeb.setCreditDebitFlg(Constants.debit);
        recDeb.setSerialNum(Constants.serialNum2);
        debit1.setCurrencyCode(Constants.accountCode);
        recDeb.setTrnAmt(debit1);
        recDeb.setTrnParticulars(getNarration(task.getTable(),ids.get(0)));

        reequest.add(recDeb);
        trxnRequest.setRecs(reequest);
        trxnRequest.setReqUuid(trxnId);
        logger.info(JsonConverter.getJson(trxnRequest));
        ResponseEntity<TransferResponses> responseEntity = utilities.postTransfer(trxnRequest);
//        responseEntity.getBody().setTransactionId(request.getUniqueIdentifier());
//        responseEntity.getBody().setAmount(request.getTranAmount());
        return responseEntity;
    }

    public ResponseEntity<TransferResponses> processTransactionSingle(Long ids, String debitAccount, Task task, String trxnId)
            throws SQLException {
        GenericUtil genericUtil = new GenericUtil();
        TransferRequestt trxnRequest = new TransferRequestt();
        List<Recs> reequest = new ArrayList<>();
        Recs rec = new Recs();
        Recs recDeb = new Recs();
        TransferRequestt request = new TransferRequestt();
        TransferRequestt debit = new TransferRequestt();
        TrnAmt credit = new TrnAmt();
        TrnAmt debit1 = new TrnAmt();
        AcctId cre = new AcctId();
        AcctId deb = new AcctId();
        cre.setAcctId(task.getAccount().getAccountNumber());
        BigDecimal amount = new BigDecimal(0);
        rec.setAcctId(cre);
        rec.setCreditDebitFlg(Constants.credit);
        rec.setSerialNum(Constants.serialNum1);


        //request.setDebitAccountNumber(debitAccount);

        if (task.getAmountInData()) {
            credit.setAmountValue(getDebitSumSingle(ids, task));
            debit1.setAmountValue(getDebitSumSingle(ids, task));


        } else {
            credit.setAmountValue(task.getCharge().multiply(new BigDecimal(1)));
            debit1.setAmountValue(task.getCharge().multiply(new BigDecimal(1)));

        }
        credit.setCurrencyCode(Constants.accountCode);
        rec.setTrnAmt(credit);
        rec.setTrnParticulars(getNarration(task.getTable(),ids));
        //request.setUniqueIdentifier(generateTransactionId());

        reequest.add(rec);
        deb.setAcctId(debitAccount);
        recDeb.setAcctId(deb);
        recDeb.setCreditDebitFlg(Constants.debit);
        recDeb.setSerialNum(Constants.serialNum2);
        debit1.setCurrencyCode(Constants.accountCode);

        recDeb.setTrnAmt(debit1);
        recDeb.setTrnParticulars(getNarration(task.getTable(),ids));

        reequest.add(recDeb);
        trxnRequest.setRecs(reequest);
        trxnRequest.setReqUuid(trxnId);
        logger.info(JsonConverter.getJson(trxnRequest));
        ResponseEntity<TransferResponses> responseEntity = utilities.postTransfer(trxnRequest);
//        responseEntity.getBody().setTransactionId(request.getUniqueIdentifier());
//        responseEntity.getBody().setAmount(request.getTranAmount());
        return responseEntity;
    }

    private BigDecimal getDebitSum(List<Long> ids, Task task) throws SQLException {
        DataColumn dataColumn = task.getTable().getColumns().stream().
                filter(c -> c.getAmountField()).findAny().orElse(null);
        BigDecimal sum = BigDecimal.ZERO;
        StringBuilder builder = new StringBuilder("SELECT ").append(dataColumn.getName()).
                append(" FROM custom.").append(task.getTable().getName()).append(" WHERE id = ?");
        PreparedStatement statement = connection.prepareStatement(builder.toString());
        for (Long id : ids) {
            try {
                statement.setLong(1, id);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                BigDecimal amount = resultSet.getBigDecimal(1);
                sum = sum.add(amount);
            } catch (SQLException ex) {
                logger.error(ex.getMessage());
            }
        }
        return sum;

    }

    private BigDecimal getDebitSumSingle(Long ids, Task task) throws SQLException {
        DataColumn dataColumn = task.getTable().getColumns().stream().
                filter(c -> c.getAmountField()).findAny().orElse(null);
        BigDecimal sum = BigDecimal.ZERO;
        StringBuilder builder = new StringBuilder("SELECT ").append(dataColumn.getName()).
                append(" FROM custom.").append(task.getTable().getName()).append(" WHERE id = ?");
        PreparedStatement statement = connection.prepareStatement(builder.toString());

        try {
            statement.setLong(1, ids);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            BigDecimal amount = resultSet.getBigDecimal(1);
            sum = sum.add(amount);
        } catch (SQLException ex) {
            logger.error(ex.getMessage());
        }

        return sum;

    }

    @Override
    public boolean reconciled(String reconcileField, String identifier, Map<String, String> reconciliationList) {
        return reconciliationList.containsKey(reconcileField) &&
                reconciliationList.get(reconcileField).equals(identifier);
    }

    @Override
    public Map<String, List<Long>> getUnprocessedData(DataTable dataTable, Long uploadId)
            throws SQLException, InvalidDataException {
        Optional<DataColumn> column =
                dataTable.getColumns().stream().filter(dataColumn -> dataColumn.getIdentifier()).findAny();
        System.out.println(uploadId + "**** hi");
        if (column.isPresent()) {
            PreparedStatement statement = null;
            try {
                StringBuilder builder = new StringBuilder("SELECT * FROM custom.");
                builder.append(dataTable.getName()).append(" WHERE upload_id = '" + uploadId + "' and approved = 1 and debit_type=1 and (processed = 0 || retry_flag = 'Y')");
                statement = connection.prepareStatement(builder.toString());
                ResultSet resultSet = statement.executeQuery();
                Map<String, List<Long>> idMap = new HashMap<>();

                while (resultSet.next()) {
                    String identifier = null;
                    Long id = resultSet.getLong("id");
                    switch (column.get().getDataType()) {
                        case LONG:
                            identifier = String.valueOf(resultSet.getLong(column.get().getName()));
                            break;
                        case INTEGER:
                            identifier = String.valueOf(resultSet.getInt(column.get().getName()));
                            break;
                        case VARCHAR:
                            identifier = resultSet.getString(column.get().getName());
                            break;
                    }
                    if (idMap.containsKey(identifier)) {
                        idMap.get(identifier).add(id);
                    } else {
                        List<Long> ids = new ArrayList<>();
                        ids.add(id);
                        idMap.put(identifier, ids);
                    }
                }
                return idMap;
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } else {
            throw new InvalidDataException("No identifier found in data columns");
        }

    }


    public Multimap<String, Long> getBulkUnprocessedData(DataTable dataTable, Long uploadId)
            throws SQLException, InvalidDataException {
        Optional<DataColumn> column =
                dataTable.getColumns().stream().filter(dataColumn -> dataColumn.getIdentifier()).findAny();
        System.out.println(uploadId + "**** hi here");
        if (column.isPresent()) {
            PreparedStatement statement = null;
            try {
                StringBuilder builder = new StringBuilder("SELECT * FROM custom.");
                builder.append(dataTable.getName()).append(" WHERE upload_id = '" + uploadId + "' and approved = 1 and debit_type=0 and (processed = 0 || retry_flag = 'Y')");
                statement = connection.prepareStatement(builder.toString());
                ResultSet resultSet = statement.executeQuery();
                Multimap<String, Long> idMap = ArrayListMultimap.create();
                List<Long> ids = new ArrayList<>();
                while (resultSet.next()) {
                    String identifier = null;
                    Long id = resultSet.getLong("id");
                    switch (column.get().getDataType()) {
                        case LONG:
                            identifier = String.valueOf(resultSet.getLong(column.get().getName()));
                            break;
                        case INTEGER:
                            identifier = String.valueOf(resultSet.getInt(column.get().getName()));
                            break;
                        case VARCHAR:
                            identifier = resultSet.getString(column.get().getName());
                            break;
                    }
//                    if (idMap.containsKey(identifier)) {
//                        idMap.get(identifier).add(id);
//                    } else {

                    // ids.add(id);
                    idMap.put(identifier, id);
                    // }

                }
                return idMap;
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } else {
            throw new InvalidDataException("No identifier found in data columns");
        }
    }

    @Override
    public void updateProcessedData(Long ids, DataTable dataTable, TransferResponses response, LienResponse responses)
            throws SQLException {
        if (ids != null) {
            PreparedStatement statement = null;
            try {
                StringBuilder builder = new StringBuilder("UPDATE custom.");
                builder.append(dataTable.getName()).append(" SET transaction_id = ?,transaction_date=?, response_code = ?, " +
                        "response_message = ?," +
                        " processed = ?, retry_flag = ?,mode=?,appnumber=?,msgdate=?,status_message=?,response_description=?  WHERE id=? ");

                statement = connection.prepareStatement(builder.toString());
                statement.setString(1,
                        response.getTranId());
                statement.setString(2,
                        response.getTranDateTime());
                statement.setString(3,
                        response.getResponseCode());

                statement.setString(4,
                        response.getResponseDescription());
                statement.setBoolean(5, true);
                statement.setString(6,
                        "N");
                statement.setString(7,
                        responses.getMode());
                statement.setString(8,
                        responses.getAppNumber());
                statement.setString(9,
                        responses.getMsgDateTime());
                statement.setString(10,
                        responses.getStatus());
                statement.setString(11,
                        responses.getResponseDescription());
                statement.setLong(12, ids);

                statement.executeUpdate();
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void updateProcessedDataA(Long ids, DataTable dataTable, TransferResponses response, String trxnId, String lienResponse, String appcode, String accountNUmber, BigDecimal amount)
            throws SQLException {

        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String currentTime = LocalDateTime.now().toString();
        if (ids != null) {
            PreparedStatement statement = null;
            try {
                StringBuilder builder = new StringBuilder("UPDATE custom.");
                builder.append(dataTable.getName()).append(" SET transaction_id = ?,transaction_date=?, response_code = ?, " +
                        "response_message = ?," +
                        " processed = ?, retry_flag = ?,trxn_id=?,lien_response_description=?,lien_appcode=?,lien_date=?,account_number=?,amount=? WHERE id=? ");

                statement = connection.prepareStatement(builder.toString());
                statement.setString(1,
                        response.getTranId());
                statement.setString(2,
                        response.getTranDateTime());
                statement.setString(3,
                        response.getResponseCode());

                statement.setString(4,
                        response.getResponseDescription());
                statement.setBoolean(5, true);
                statement.setString(6,
                        "N");
                statement.setString(7,
                        trxnId);
                statement.setString(8,
                        lienResponse);
                statement.setString(9,
                        appcode);
                statement.setString(10,
                        currentTime);
                statement.setString(11,
                        accountNUmber);
                statement.setBigDecimal(12,
                        amount);
                statement.setLong(13, ids);

                statement.executeUpdate();
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public void updateProcessedDataBulk(List<Long> ids, DataTable dataTable, TransferResponses response, String trxnId, String resCode, String appCode, String accountNUmber, BigDecimal amount)
            throws SQLException {
        if (ids != null) {


            if (!ids.isEmpty()) {
                PreparedStatement statement = null;
                try {
                    StringBuilder builder = new StringBuilder("UPDATE custom.");
                    builder.append(dataTable.getName()).append(" SET transaction_id = ?,transaction_date=?, response_code = ?, " +
                            "response_message = ?," +
                            " processed = ?,trxn_id=?, retry_flag = ?,lien_response_description=?,lien_appcode=?,account_number=?,amount=?  WHERE id IN (");
                    for (int i = 0; i < ids.size(); i++) {
                        builder.append("?, ");
                    }
                    builder.deleteCharAt(builder.lastIndexOf(","));
                    builder.append(")");
                    statement = connection.prepareStatement(builder.toString());
                    statement.setString(1,
                            response.getTranId());
                    statement.setString(2,
                            response.getTranDateTime());
                    statement.setString(3,
                            response.getResponseCode());
                    statement.setString(4,
                            response.getResponseDescription());
                    statement.setBoolean(5, true);
                    statement.setString(6,
                            trxnId);
                    statement.setString(7,
                            "N");
                    statement.setString(8,
                            resCode);
                    statement.setString(9,
                            appCode);

                    statement.setString(10,
                            accountNUmber);

                    statement.setBigDecimal(11,
                            amount);
                    int index = 12;
                    for (Long id : ids) {
                        statement.setLong(index, id);
                        ++index;
                    }
                    statement.executeUpdate();
                } finally {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public TransferResponses transfer(TransferRequestt transferRequest) throws ApiException {
        ResponseEntity<TransferResponses> response = utilities.postTransfer(transferRequest);
        if (response.getStatusCode() != HttpStatus.OK) {
            ApiException exception = new ApiException("An error occurred while processing payment");
            exception.setStatusCode(response.getStatusCode().value());
            throw exception;
        } else {
            return response.getBody();
        }
    }


    @Async
    @Override
    public void processTask(Task task) throws SQLException {
        if (task.getBulkPayment() == true) {
            System.out.println("i got here");
            bulkPayment(task);

        } else if (task.getBulkPayment() == false) {
            System.out.println("i got here now");
            singlePayment(task);
        }

    }


    private void bulkPayment(Task task) {
        try {

            DataUpload task1 = uploadRepository.findByTaskAndStatus(task, GenericStatus.ACTIVE);

            System.out.println("**** upload id is: " + task1.getId());
            Map<String, List<Long>> unprocessed = getUnprocessedData(task.getTable(), task1.getId());
            logger.info(JsonConverter.getJson(unprocessed));
            String testVal = null;
            String uniqueVal = null;
            String pNum = null;
            BigDecimal currentval = new BigDecimal(0);
            for (Map.Entry<String, List<Long>> entry : unprocessed.entrySet()) {
                String accountNumber = null;

                if (task.getAccountInData()) {
                    accountNumber = entry.getKey();
                } else {
                    DataColumn identifierColumn = null;
                    for (DataColumn column : task.getTable().getColumns()) {
                        if (column.getIdentifier()) {
                            identifierColumn = column;
                            break;
                        }
                    }
                    if (identifierColumn != null) {
                        Object identifier = null;
                        switch (identifierColumn.getDataType()) {
                            case LONG:
                                identifier = Long.getLong(entry.getKey());
                                break;
                            case INTEGER:
                                identifier = Integer.parseInt(entry.getKey());
                                break;
                            case VARCHAR:
                                identifier = entry.getKey();
                                break;
                        }

                        try {
                            String identify = identifier.toString();
                            uniqueVal = identify;
                            identify = identify.trim().replaceAll("[(-),+]", "");
                            identify = identify.replaceAll(" ", "");
                            if (identify.length() > 9) {
                                testVal = identify.substring(identify.length() - 10);
                            }
                            accountNumber = getAccountNumber(identifier, task, testVal);
//                            System.out.println(testVal + " val val" + accountNumber);
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error(e.getMessage());
                            continue;
                        }
                    } else {
                        continue;
                    }
                }
                try {
                    if (accountNumber == null) {

                    } else {
                        String valGen = generateTransactionId();
                        String debitTrxnId = generateTransactionId1();
                        String lienResponse = null;
                        String appCode = null;
                        if (task.getAmountInData()) {
                            currentval = getDebitSum(entry.getValue(), task);

                            // System.out.println(amount+ "what are you doing here");

                        } else {
                            currentval = task.getCharge().multiply(new BigDecimal(entry.getValue().size()));

                            // System.out.println(task.getCharge()+ " this is it"+ amount);
                        }
                        ResponseEntity<TransferResponses> responseEntity = processTransaction
                                (entry.getValue(), accountNumber, task, debitTrxnId);

                        if (responseEntity.getBody().getResponseDescription().contains(Constants.INSUFFICIENT_FUND)) {

                            ResponseEntity<LienResponse> responseLien = processTransactionInsufficient(currentval, accountNumber, valGen);
                            logger.info("My value==>>>>: " + JsonConverter.getJson(responseLien));
                            if (responseLien.getBody().getStatus().equals(Constants.STATUS)) {
                                //updateProcessedData(entry.getValue(), task.getTable(), responseEntity.getBody(), responseLien.getBody());
                                insertUnprocessedSingle(entry.getValue().get(0), task.getTable(), accountNumber, testVal, currentval, task.getAccount().getAccountNumber(), getNarration(task.getTable(),entry.getValue().get(0)), responseLien.getBody(), valGen, "BULK");
                            } else {

                                lienResponse = responseLien.getBody().getResponseDescription();
                                appCode = responseLien.getBody().getAppNumber();


                            }

                        }
                        updateProcessedDataBulk(entry.getValue(), task.getTable(), responseEntity.getBody(), debitTrxnId, lienResponse, appCode, accountNumber, currentval);


                        logger.info(JsonConverter.getJson(entry.getValue() + " value is this " + responseEntity.getBody()));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                }
            }
            List<User> userList = userRepository.findAll();
            String filename = saveExcelDataLog(task1, logData(task, task1), Constants.SUCCESSFUL_TRXN);

            InputStreamSource iss = new InputStreamSource() {
                @Override
                public InputStream getInputStream() throws IOException {
                    // provide fresh InputStream
                    return new FileInputStream(GenericUtil.getStoragePath() + filename);
                }
            };

            sendMail(userList.stream().filter(user -> user.getDeleted() == false)
                    .collect(Collectors.toList()), filename, task1, iss);
            System.out.println(filename + " hiya");
            task1.setSuccessfulDedbit(filename);
            uploadRepository.saveAndFlush(task1);


        } catch (SQLException | InvalidDataException | IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

    }




    private void singlePayment(Task task) {
        try {
            DataUpload task1 = uploadRepository.findByTaskAndStatus(task, GenericStatus.ACTIVE);

            System.out.println("**** upload id is: " + task1.getId());
            Multimap<String, Long> unprocessed = getBulkUnprocessedData(task.getTable(), task1.getId());
            String testVal = null;

            for (Map.Entry<String, Long> entry : unprocessed.entries()) {
                String accountNumber = null;
                String pNum = null;
                BigDecimal amount = new BigDecimal(0);

                if (task.getAccountInData()) {
                    accountNumber = entry.getKey();
                } else {
                    DataColumn identifierColumn = null;
                    for (DataColumn column : task.getTable().getColumns()) {
                        if (column.getIdentifier()) {
                            identifierColumn = column;
                            break;
                        }
                    }
                    if (identifierColumn != null) {
                        Object identifier = null;
                        switch (identifierColumn.getDataType()) {
                            case LONG:
                                identifier = Long.getLong(entry.getKey());
                                break;
                            case INTEGER:
                                identifier = Integer.parseInt(entry.getKey());
                                break;
                            case VARCHAR:
                                identifier = entry.getKey();
                                break;
                        }
                        try {
                            String identify = identifier.toString();
                            identify = identify.trim().replaceAll("[(-),+]", "");
                            identify = identify.replaceAll(" ", "");
                            if (identify.length() > 9) {
                                testVal = identify.substring(identify.length() - 10);
                            }
                            accountNumber = getAccountNumber(identifier, task, testVal);
//                            System.out.println(testVal + " val val" + accountNumber);
                            pNum = identifier.toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error(e.getMessage());
                            continue;
                        }
                    } else {
                        continue;
                    }
                }
                try {
                    if (accountNumber == null) {

                    } else {
                        String valGen = generateTransactionId();
                        String debitTrxnId = generateTransactionId1();
                        String lienResponse = null;
                        String appCode = null;
                        if (task.getAmountInData()) {
                            amount = getDebitSumSingle(entry.getValue(), task);

                            // System.out.println(amount+ "what are you doing here");

                        } else {
                            amount = task.getCharge().multiply(new BigDecimal(1));

                            // System.out.println(task.getCharge()+ " this is it"+ amount);
                        }
                        ResponseEntity<TransferResponses> responseEntity = processTransactionSingle
                                (entry.getValue(), accountNumber, task, debitTrxnId);

                        if (responseEntity.getBody().getResponseDescription().contains(Constants.INSUFFICIENT_FUND)) {

                            ResponseEntity<LienResponse> responseLien = processTransactionInsufficient(amount, accountNumber, valGen);
                            logger.info("My value==>>>>: " + JsonConverter.getJson(responseLien));
                            if (responseLien.getBody().getStatus().equals(Constants.STATUS)) {
                                //updateProcessedData(entry.getValue(), task.getTable(), responseEntity.getBody(), responseLien.getBody());

                                insertUnprocessedSingle(entry.getValue(), task.getTable(), accountNumber, testVal, amount, task.getAccount().getAccountNumber(),getNarration(task.getTable(),entry.getValue()), responseLien.getBody(), valGen, "SINGLE");
                            } else {
                                lienResponse = responseLien.getBody().getResponseDescription();
                                appCode = responseLien.getBody().getAppNumber();

                            }

                        }
                        updateProcessedDataA(entry.getValue(), task.getTable(), responseEntity.getBody(), debitTrxnId, lienResponse, appCode, accountNumber, amount);


                        logger.info(JsonConverter.getJson(entry.getValue() + " value is this " + responseEntity.getBody()));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                }
            }
            String filename = saveExcelDataLog(task1, logData(task, task1), Constants.SUCCESSFUL_TRXN);
            List<User> userList = userRepository.findAll();


            InputStreamSource iss = new InputStreamSource() {
                @Override
                public InputStream getInputStream() throws IOException {
                    // provide fresh InputStream
                    return new FileInputStream(GenericUtil.getStoragePath() + filename);
                }
            };

            sendMail(userList.stream().filter(user -> user.getDeleted() == false)
                    .collect(Collectors.toList()), filename, task1, iss);
            System.out.println(filename + " hiya");
            task1.setSuccessfulDedbit(filename);
            uploadRepository.saveAndFlush(task1);


        } catch (SQLException | InvalidDataException | IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    private String generateInsertScript(List<DataColumn> columnData, DataTable dataTable) {
        StringBuilder builder = new StringBuilder("INSERT INTO custom.");
        builder.append(dataTable.getName()).append("(upload_id,debit_type, ");
        for (DataColumn column : columnData) {
            builder.append(column.getName()).append(", ");
        }
        builder = builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append(") VALUES(?,?, ");
        for (int i = 0; i < columnData.size(); i++) {
            builder.append("?, ");
        }
        builder = builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append(")");
        return builder.toString();
    }

    private String generateInsertScriptInsufficient(DataTable dataTable) {
        StringBuilder builder = new StringBuilder("INSERT INTO custom.");
        builder.append(dataTable.getInsufficientBalance()).append("(upload_id,phone_number,account_number,credit_account,narration,mode,appnumber,msgdate,status_message,response_description,trxn_id,debit_type,amount");
        builder.append(") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        return builder.toString();
    }

    private String generateInsertScriptAccountPhone(DataTable dataTable) {
        StringBuilder builder = new StringBuilder("INSERT INTO custom.");
        builder.append(dataTable.getPhoneAccountHolder()).append("(phone_number,account_number");
        builder.append(") VALUES(?,?) ");
        return builder.toString();
    }


    @Override
    public void insertUnprocessedSingle(Long ids, DataTable table, String accountNumber, String phoneNumber, BigDecimal amount, String creditAccount, String narration, LienResponse lienResponse, String trxnId, String debitType) throws SQLException {
        String insertScript = generateInsertScriptInsufficient(table);
        PreparedStatement statement = null;
        try {

            if (ids != null) {

                statement = connection.prepareStatement(insertScript);
                statement.setLong(1, ids);
                statement.setString(2, phoneNumber);
                statement.setString(3, accountNumber);
                statement.setString(4, creditAccount);
                statement.setString(5, narration);
                statement.setString(6, lienResponse.getMode());
                statement.setString(7, lienResponse.getAppNumber());
                statement.setString(8, lienResponse.getMsgDateTime());
                statement.setString(9, lienResponse.getStatus());
                statement.setString(10, lienResponse.getResponseDescription());
                statement.setString(11, trxnId);
                statement.setString(12, debitType);
                statement.setBigDecimal(13, amount);

                statement.executeUpdate();

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
    }

    @Override
    public List<DataTable> getTableNames() {
        return tableRepository.findAll();
    }

    @Async
    @Override
    public void processInsufficientBalance(DataTable dataTable) throws InvalidDataException, SQLException {

        Optional<Task> task = taskRepository.findByTable(dataTable);
        if (task.isPresent()) {
            DataUpload dataUpload = uploadRepository.findByTaskAndStatus(task.get(), GenericStatus.ACTIVE);


            updateInsufficientSingle(dataTable, dataUpload);

        }


    }

    void updateInsufficientSingle(DataTable dataTable, DataUpload dataUpload) throws SQLException, InvalidDataException {
        List<InsufficientBalance> unprocessed = getUnprocessedDinsertataInsufficientFund(dataTable, dataUpload);

        if (dataUpload != null) {
            for (InsufficientBalance insufficientBalance : unprocessed) {
                String trxn = generateTransactionId1();

                if (confirmSufficientBalance(insufficientBalance)) {
                    ResponseEntity<LienResponse> responseLien = removeLien(insufficientBalance);
                    logger.info("REMOVE LIENT====>" + JsonConverter.getJson(responseLien));
                    if (responseLien.getBody().getStatus().equals(Constants.STATUS)) {
                        ResponseEntity<TransferResponses> responseEntity = processTransactionInsufficient(insufficientBalance);
                        logger.info("TRANSFER API RESPONSE==>>:  " + JsonConverter.getJson(responseEntity.getBody()));

                        if (responseEntity.getBody().getResponseCode().equals(Constants.STATUS)) {
                            logger.info("Updating insufficient balance==>>:  " + JsonConverter.getJson(responseEntity.getBody()));
                            updateProcessedInsufficientBalance(insufficientBalance, dataTable, responseEntity.getBody(), trxn);
                        }
                    } else {
                        ResponseEntity<TransferResponses> responseEntity = processTransactionInsufficient(insufficientBalance);
                        logger.info("TRANSFER API RESPONSE==>>:  " + JsonConverter.getJson(responseEntity.getBody()));
                        if (responseEntity.getBody().getResponseCode().equals(Constants.STATUS)) {
                            logger.info("Updating insufficient balance==>>:  " + JsonConverter.getJson(responseEntity.getBody()));
                            updateProcessedInsufficientBalance(insufficientBalance, dataTable, responseEntity.getBody(), trxn);
                        }
                    }
                }
            }
        }
    }


    private Boolean confirmSufficientBalance(InsufficientBalance insufficientBalance) {
        AccountDetailRequest accountDetailRequest = new AccountDetailRequest(insufficientBalance.getAccountNumber());
        ResponseEntity<AccountDetailResponse> response = utilities.getAccountDetails(accountDetailRequest);
        if (response.getBody().getResponseCode().equals(Constants.ACCOUNT_RESPONSE_CODE)
                && response.getBody().getResponseText().equals(Constants.STATUS)) {
            BigDecimal outstandingBalance = insufficientBalance.getAmount();
            BigDecimal currentBalance = response.getBody().getEffectiveBalance();
            int res = outstandingBalance.compareTo(currentBalance);
            if (res == 0 || res == -1) {
                return true;
            }

        }
        return false;
    }

    private String getNarration(DataTable datatable, Long id) throws SQLException {
        PreparedStatement statement = null;
        String narration=null;
        for(DataColumn dataColumn:datatable.getColumns())
        {
            if(dataColumn.getNarration())
            {
                narration=dataColumn.getName();
            }
        }
        try
        {

            StringBuilder builder = new StringBuilder("SELECT * FROM custom.");
            builder.append(datatable.getName()).append(" WHERE id="+id+"");
            statement = connection.prepareStatement(builder.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                narration=resultSet.getString(narration);
            }
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return narration;
    }
    @Override
    public List<InsufficientBalance> getUnprocessedDinsertataInsufficientFund(DataTable datatable, DataUpload dataUpload) throws SQLException, InvalidDataException {

        Optional<DataColumn> column = datatable.getColumns().stream().filter(dataColumn -> dataColumn.getIdentifier()).findAny();
        List<InsufficientBalance> insufficientBalances = new ArrayList<>();

        if (column.isPresent() && dataUpload != null) {
            PreparedStatement statement = null;
            try {
                StringBuilder builder = new StringBuilder("SELECT * FROM custom.");
                builder.append(datatable.getInsufficientBalance()).append(" WHERE " +
                        "upload_id in (select id from custom.").append(datatable.getName()).append("  where upload_id='" + dataUpload.getId() + "') and processed = 0");
                statement = connection.prepareStatement(builder.toString());
                ResultSet resultSet = statement.executeQuery();
                Multimap<String, Long> idMap = ArrayListMultimap.create();
                int i = 0;
                while (resultSet.next()) {
                    InsufficientBalance insufficientBalance = new InsufficientBalance();
                    insufficientBalance.setId(resultSet.getLong("id"));
                    insufficientBalance.setAccountNumber(resultSet.getString("account_number"));
                    insufficientBalance.setAmount(resultSet.getBigDecimal("amount"));
                    insufficientBalance.setCreditAccount(resultSet.getString("credit_account"));
                    insufficientBalance.setNarration(resultSet.getString("narration"));
                    insufficientBalance.setAppid(resultSet.getString("appnumber"));

                    insufficientBalances.add(insufficientBalance);
                }

                return insufficientBalances;
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println("Value not present in getUnprocessedDinsertataInsufficientFund method ");
            return null;
        }
    }


    @Override
    public ResponseEntity<LienResponse> processTransactionInsufficient(BigDecimal amount, String accountNumber, String trxnId) {
        List<LienRequest> trxnRequest = new ArrayList<>();
        LienRequest request = new LienRequest();

        request.setAccountId(accountNumber);
        request.setAmountValue(amount);
        request.setAppNumber(trxnId);
        request.setCurrency(Constants.accountCode);
        request.setReasonCode(reasonCode);
        request.setRequestUUID(trxnId);

        logger.info(amount + " and " + accountNumber + "  and " + trxnId + " MY REQUEST====>>>> " + JsonConverter.getJson(request));
        ResponseEntity<LienResponse> responseEntity = utilities.postLien(request);
//        responseEntity.getBody().setTransactionId(request.getUniqueIdentifier());
//        responseEntity.getBody().setAmount(request.getTranAmount());
        return responseEntity;
    }

    @Override
    public ResponseEntity<LienResponse> removeLien(InsufficientBalance insufficientBalance) {
        List<LienRequest> trxnRequest = new ArrayList<>();
        LienRequest request = new LienRequest();

        request.setAccountId(insufficientBalance.getAccountNumber());
        request.setAmountValue(insufficientBalance.getAmount());
        request.setAppNumber(insufficientBalance.getAppid());
        request.setCurrency(Constants.accountCode);
        request.setReasonCode(reasonCode);
        request.setRequestUUID(insufficientBalance.getAppid());

        logger.info(insufficientBalance.getAmount() + " and " + insufficientBalance.getAccountNumber() + "  and " + insufficientBalance.getAppid() + " MY REQUEST Remove====>>>> " + JsonConverter.getJson(trxnRequest));
        ResponseEntity<LienResponse> responseEntity = utilities.removeLien(request);
//        responseEntity.getBody().setTransactionId(request.getUniqueIdentifier());
//        responseEntity.getBody().setAmount(request.getTranAmount());
        return responseEntity;
    }

    public String generateTransactionId() {
        return String.format("%s%015d", Constants.TRANX_LIEN, System.currentTimeMillis());
    }

    public String generateTransactionId1() {
        return String.format("%s%015d", Constants.AUTO_DEBIT, System.currentTimeMillis());
    }


    @Override
    public void updateProcessedInsufficientBalance(InsufficientBalance insufficientBalance, DataTable dataTable, TransferResponses transferResponses, String trxnId) throws SQLException {
        if (insufficientBalance.getId() != null) {
            PreparedStatement statement = null;
            try {
                StringBuilder builder = new StringBuilder("UPDATE custom.");
                builder.append(dataTable.getInsufficientBalance()).append(" SET processed=?, response_code = ?, " +
                        "response_message = ?, tranid=?," +
                        " transaction_date=?,trxnfer_id=?  where id=?");

                statement = connection.prepareStatement(builder.toString());
                statement.setBoolean(1,
                        true);
                statement.setString(2, transferResponses.getResponseCode());
                statement.setString(3, transferResponses.getResponseDescription());
                statement.setString(4, transferResponses.getTranId());
                statement.setString(5,
                        transferResponses.getTranDateTime());
                statement.setString(6,
                        trxnId);
                statement.setLong(7,
                        insufficientBalance.getId());


                statement.executeUpdate();
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public void updateProcessedInsufficientBalanceBulk(InsufficientBalance insufficientBalance, DataTable dataTable, TransferResponses transferResponses, String trxnId, DataUpload dataUpload) throws SQLException {
        if (insufficientBalance.getId() != null) {
            PreparedStatement statement = null;
            try {
                StringBuilder builder = new StringBuilder("UPDATE custom.");
                builder.append(dataTable.getInsufficientBalance()).append(" SET processed=?, response_code = ?, " +
                        "response_message = ?, tranid=?," +
                        " transaction_date=?,trxnfer_id=?  where account_number=? and upload_id " +
                        "in (select id from custom.").append(dataTable.getName() + " where upload_id='" + dataUpload.getId() + "')");

                statement = connection.prepareStatement(builder.toString());
                statement.setBoolean(1,
                        true);
                statement.setString(2, transferResponses.getResponseCode());
                statement.setString(3, transferResponses.getResponseDescription());
                statement.setString(4, transferResponses.getTranId());
                statement.setString(5,
                        transferResponses.getTranDateTime());
                statement.setString(6,
                        trxnId);
                statement.setLong(7,
                        insufficientBalance.getId());


                statement.executeUpdate();
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public ResponseEntity<TransferResponses> processTransactionInsufficient(InsufficientBalance insufficientBalance) throws SQLException {
        TransferRequestt trxnRequest = new TransferRequestt();
        List<Recs> reequest = new ArrayList<>();
        Recs rec = new Recs();
        Recs recDeb = new Recs();
        TransferRequestt request = new TransferRequestt();
        TransferRequestt debit = new TransferRequestt();
        TrnAmt credit = new TrnAmt();
        TrnAmt debit1 = new TrnAmt();
        AcctId cre = new AcctId();
        AcctId deb = new AcctId();
        cre.setAcctId(insufficientBalance.getCreditAccount());
        BigDecimal amount = new BigDecimal(0);
        rec.setAcctId(cre);
        rec.setCreditDebitFlg(Constants.credit);
        rec.setSerialNum(Constants.serialNum1);


        //request.setDebitAccountNumber(debitAccount);


        credit.setAmountValue(insufficientBalance.getAmount());
        // System.out.println(task.getCharge()+ " this is it"+ amount);

        credit.setCurrencyCode(Constants.accountCode);
        rec.setTrnAmt(credit);
        rec.setTrnParticulars(insufficientBalance.getNarration());
        //request.setUniqueIdentifier(generateTransactionId());

        reequest.add(rec);
        deb.setAcctId(insufficientBalance.getAccountNumber());
        recDeb.setAcctId(deb);
        recDeb.setCreditDebitFlg(Constants.debit);
        recDeb.setSerialNum(Constants.serialNum2);
        debit1.setCurrencyCode(Constants.accountCode);
        debit1.setAmountValue(insufficientBalance.getAmount());
        recDeb.setTrnAmt(debit1);
        recDeb.setTrnParticulars(insufficientBalance.getNarration());

        reequest.add(recDeb);
        trxnRequest.setRecs(reequest);
        trxnRequest.setReqUuid("CREDIT" + generateTransactionId());
        logger.info(JsonConverter.getJson(trxnRequest));
        ResponseEntity<TransferResponses> responseEntity = utilities.postTransfer(trxnRequest);
//        responseEntity.getBody().setTransactionId(request.getUniqueIdentifier());
//        responseEntity.getBody().setAmount(request.getTranAmount());
        return responseEntity;
    }

    @Override
    public void insertRecordAccountPhone(Task task) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            String phoneVal = null;
            String url = task.getConnection().getUrl();
            Multimap<String, String> currentNumberHolder = ArrayListMultimap.create();
            Multimap<String, String> numberHolder = ArrayListMultimap.create();
            if (task.getConnection().getDriverType().equals(DriverType.MYSQL)) {
                if (!url.contains("serverTimezone=UTC")) {
                    if (url.contains("?")) {
                        if (url.endsWith("?")) {
                            url = url + "serverTimezone=UTC";
                        } else {
                            url = url + "&serverTimezone=UTC";
                        }
                    } else {
                        url = url + "?serverTimezone=UTC";
                    }
                }
            }
            connection = DriverManager.getConnection(url,
                    task.getConnection().getUsername(),
                    utilities.decrypt(task.getConnection().getPassword(), appKey));
            String accountFetchSql = getAccountFetch(task);
            statement = connection.prepareStatement(accountFetchSql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                System.out.println(resultSet.getString(task.getAccountColumn()).trim() + " and " + resultSet.getString(task.getFetchColumn()));
                numberHolder.put(resultSet.getString(task.getAccountColumn()).trim(), resultSet.getString(task.getFetchColumn()).trim());
            }

            for (Map.Entry acctDetails : numberHolder.entries()) {
                phoneVal = acctDetails.getValue().toString().replaceAll("[(-),+]", "");
                phoneVal = phoneVal.replaceAll(" ", "");
                if (phoneVal.length() > 10) {
                    phoneVal = phoneVal.substring(phoneVal.length() - 10);
                    currentNumberHolder.put(acctDetails.getKey().toString(), phoneVal);
                } else {
                    currentNumberHolder.put(acctDetails.getKey().toString(), phoneVal);
                }
            }

            insertAccountNow(currentNumberHolder, task.getTable());
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

    private void insertAccountNow(Multimap<String, String> val, DataTable dataTable) throws SQLException {


        String insertScript = generateInsertScriptAccountPhone(dataTable);
        String truncateTable = "Truncate table custom." + dataTable.getPhoneAccountHolder();
        PreparedStatement statement = null;
        PreparedStatement truncateStatement = null;
        truncateStatement = connection.prepareStatement(truncateTable);
        truncateStatement.executeUpdate();

        try {
            for (Map.Entry<String, String> acctDetails : val.entries()) {
                statement = connection.prepareStatement(insertScript);
                statement.setString(1, acctDetails.getValue());
                statement.setString(2, acctDetails.getKey());
                statement.executeUpdate();
            }


        } finally {
            if (statement != null || truncateStatement != null) {
                try {
                    statement.close();
                    truncateStatement.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<Task> getAllTasks() {

        return taskRepository.findAll();
    }


    public String getLastDayAndYearCorrect() {
        LocalDate currentDate = LocalDate.now();
        DayOfWeek dow = currentDate.getDayOfWeek();
        int dom = currentDate.getDayOfMonth();
        Month m = currentDate.getMonth();
        int y = currentDate.getYear();

        return " " + dow + " " + m +" "+dom+ ", " + y;
    }

    private void sendMail(List<User> userList, String filename, DataUpload dataUpload, InputStreamSource inputStreamResource) {
        String message = null;
        for (User user : userList) {
            Map<String, Object> variables = GenericUtil.getUploadDetails(dataUpload, user);
            variables.put("DateValue", getLastDayAndYearCorrect());
            message = mailBuilder.build(Constants.SUCCESSFUL_UPLOAD, variables);
            System.out.println("****Email sent to***** : " + user.getEmail());
            filename = filename + ".xlsx";
            try {
                mailer.mailUserAsyncAttach(user.getEmail(), message, Constants.SUCCESS_SUBJECT + " " + getLastDayAndYearCorrect(), filename, inputStreamResource);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }
}
