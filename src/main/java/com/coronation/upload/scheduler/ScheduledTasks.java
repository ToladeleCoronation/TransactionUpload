package com.coronation.upload.scheduler;

import com.coronation.upload.domain.DataTable;
import com.coronation.upload.domain.Task;
import com.coronation.upload.domain.enums.GenericStatus;
import com.coronation.upload.exception.InvalidDataException;
import com.coronation.upload.services.TransactionService;
import com.coronation.upload.util.JsonConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
public class ScheduledTasks {
    @Autowired
    private TransactionService transactionService;

    private Logger logger = LogManager.getLogger(ScheduledTasks.class);


    @Scheduled(fixedRate = 60000)
    public void schedulePaymentTask() {
        processDueTasks();
        //processDueInsufficientBalance();
    }

   //@Scheduled(fixedRate = 60000)
    @Scheduled(cron = "0 0 12 * * ?")
    public void scheduleInsuuficientFundTask() {
        getAllTask();
        logger.info("Data Transfer Successful at =====>>>> "+ LocalDate.now());
    }

    @Async
    public void processDueTasks() {
        Set<Task> dueTasks = transactionService.getDueTasks();
        logger.info(JsonConverter.getJson(dueTasks));
        dueTasks.forEach(task -> {
            if (task.getAccount().getStatus().equals(GenericStatus.ACTIVE)) {
                transactionService.processTask(task);

            }
        });
    }

    @Async
    public void processDueInsufficientBalance() {
        List<DataTable> dueTask = transactionService.getTableNames();
        logger.info(JsonConverter.getJson(dueTask));
        dueTask.forEach(task -> {
            try {
                transactionService.processInsufficientBalance(task);
            } catch (InvalidDataException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Async
    public void getAllTask() {
        List<Task> dueTask = transactionService.getAllTasks();
        logger.info(JsonConverter.getJson(dueTask));
        dueTask.forEach(task -> {
            try {
                transactionService.insertRecordAccountPhone(task);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
