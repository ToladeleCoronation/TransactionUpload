package com.coronation.upload.controllers;

import com.coronation.upload.domain.DataUpload;
import com.coronation.upload.domain.Task;
import com.coronation.upload.domain.enums.GenericStatus;
import com.coronation.upload.dto.ApprovalDto;
import com.coronation.upload.exception.InvalidDataException;
import com.coronation.upload.repo.predicate.CustomPredicateBuilder;
import com.coronation.upload.repo.predicate.Operation;
import com.coronation.upload.services.TaskService;
import com.coronation.upload.services.TransactionService;
import com.coronation.upload.util.GenericUtil;
import com.coronation.upload.util.PageUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Toyin on 8/1/19.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/uploads")
public class UploadController {
    private TaskService taskService;
    private TransactionService transactionService;
    private Logger logger = LogManager.getLogger(UploadController.class);

    @Autowired
    public UploadController(TaskService taskService, TransactionService transactionService) {
        this.taskService = taskService;
        this.transactionService = transactionService;
    }

    @PreAuthorize("hasAnyRole('IT_ADMIN')")
    @PostMapping("/tasks/{taskId}/files")
    public ResponseEntity<DataUpload> upload(@PathVariable("taskId") Long taskId,
                             @RequestParam("file") MultipartFile[] files) {
        Task task = taskService.findById(taskId);
        if (task == null) {
            ResponseEntity.notFound().build();
        } else if (!task.getAccount().getStatus().equals(GenericStatus.ACTIVE)) {
            return ResponseEntity.unprocessableEntity().build();
        } else if (task.getReconcile() && files.length != 2) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(transactionService.uploadFile(files, task));
        } catch (InvalidDataException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasAnyRole('OP_ADMIN')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<DataUpload> approveUpload(@PathVariable("id") Long id,
                            @RequestBody @Valid ApprovalDto approvalDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        DataUpload dataUpload = transactionService.findUploadById(id);
        if (dataUpload == null) {
            return ResponseEntity.notFound().build();
        }
        if (approvalDto.getApprove()) {
            try {
                return ResponseEntity.ok(transactionService.approveFile(dataUpload));
            } catch (IOException | InvalidFormatException | SQLException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            } catch (InvalidDataException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.ok(transactionService.rejectFile(dataUpload));
        }
    }

    @GetMapping("/{id}/download/{upload}")
    public @ResponseBody byte[] downloadUploadFile(@PathVariable("id") Long id,
                               @PathVariable("upload") Boolean upload) {
        DataUpload dataUpload = transactionService.findUploadById(id);
        if (dataUpload == null) {
            return new byte[0];
        }
        try {
            if (upload) {
                return GenericUtil.pathToByteArray(GenericUtil.getStoragePath() + dataUpload.getUploadFile());
            } else {
                return GenericUtil.pathToByteArray(GenericUtil.getStoragePath() + dataUpload.getReconcileFile());
            }
        } catch (IOException ex) {
            return new byte[0];
        }
    }

    @GetMapping("/download/{fileName}")
    public @ResponseBody byte[] downloadFile(@PathVariable("fileName") String fileName) {
        try {
            return GenericUtil.pathToByteArray(GenericUtil.getStoragePath() + fileName);
        } catch (IOException ex) {
            return new byte[0];
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataUpload> getById(@PathVariable("id") Long id) {
        DataUpload dataUpload = transactionService.findUploadById(id);
        if (dataUpload == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dataUpload);
    }

    @GetMapping
    public ResponseEntity<Page<DataUpload>> listAllUpload(@RequestParam(value="page", required = false, defaultValue = "0") int page,
              @RequestParam(value="pageSize", defaultValue = "10") int pageSize,
              @RequestParam(value="taskName", required = false) String taskName,
              @RequestParam(value="uploadFile", required = false) String uploadFile,
              @RequestParam(value="status", required = false) GenericStatus status) {

        BooleanExpression filter = new CustomPredicateBuilder<>("upload", DataUpload.class)
                .with("task.name", Operation.LIKE, taskName)
                .with("uploadFile", Operation.LIKE, uploadFile)
                .with("status", Operation.ENUM, status).build();
            Pageable pageRequest =
                PageUtil.createPageRequest(page, pageSize, Sort.by("createdAt").descending());
        return ResponseEntity.ok(transactionService.findAllUploads(filter, pageRequest));
    }
}
