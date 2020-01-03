package com.coronation.upload.controllers;

import com.coronation.upload.domain.DataTable;
import com.coronation.upload.dto.DataTables;
import com.coronation.upload.services.TableService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Toyin on 8/1/19.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/tables")
public class TableControllers {
    private TableService tableService;
    private Logger logger = LogManager.getLogger(TableControllers.class);

    @Autowired
    public TableControllers(TableService tableService) {
        this.tableService = tableService;
    }

    @PreAuthorize("hasAnyRole('INITIATOR')")
    @PostMapping
    public ResponseEntity<DataTable> create(@RequestBody @Valid DataTables dataTable1, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        DataTable datatable=new DataTable();
        datatable.setName(dataTable1.getName());
        datatable.setInsufficientBalance(dataTable1.getName());
        datatable.setColumns(dataTable1.getColumns());
        datatable.setAmountInData(dataTable1.getAmountInData());
        datatable.setCanReconcile(dataTable1.getCanReconcile());
        datatable.setDescription(dataTable1.getDescription());

        datatable = tableService.sanitizeTableStruct(datatable);
        if (tableService.findByName(datatable.getName()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (tableService.noDuplicateColumn(datatable.getColumns()) &&
            tableService.identifiersValidated(datatable.getColumns(), datatable.getCanReconcile(),
                    datatable.getAmountInData())) {
            try {
                return ResponseEntity.ok(tableService.createTable(datatable));
            } catch (SQLException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataTable> findById(@PathVariable("id") Long id) {
        DataTable dataTable = tableService.findById(id);
        if (dataTable == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dataTable);
    }

    @GetMapping
    public ResponseEntity<List<DataTable>> findAll() {
        return ResponseEntity.ok(tableService.findAll());
    }
}
