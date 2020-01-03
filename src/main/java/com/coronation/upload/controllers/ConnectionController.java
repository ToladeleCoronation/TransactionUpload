package com.coronation.upload.controllers;

import com.coronation.upload.domain.DataConnection;
import com.coronation.upload.services.ConnectionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by Toyin on 8/1/19.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/connections")
public class ConnectionController {
    private ConnectionService connectionService;
    private Logger logger = LogManager.getLogger(ConnectionController.class);

    @Autowired
    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @PreAuthorize("hasAnyRole('INITIATOR')")
    @PostMapping
    public ResponseEntity<DataConnection> create(@RequestBody @Valid DataConnection connection,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        if (connectionService.findByName(connection.getName()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        try {
            return ResponseEntity.ok(connectionService.save(connection));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INITIATOR')")
    public ResponseEntity<DataConnection> edit(@PathVariable("id") Long id, @RequestBody @Valid DataConnection connection,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        DataConnection previous = null;
        try {
            previous = connectionService.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        if (previous == null) {
            return ResponseEntity.notFound().build();
        }

        DataConnection duplicate = connectionService.findByName(connection.getName());
        if (duplicate != null && !duplicate.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        try {
            return ResponseEntity.ok(connectionService.edit(previous, connection));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataConnection> findById(@PathVariable("id") Long id) {
        DataConnection connection = null;
        try {
            connection = connectionService.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        if (connection == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(connection);
    }

    @GetMapping
    public ResponseEntity<List<DataConnection>> findAll() {
        return ResponseEntity.ok(connectionService.findAll());
    }
}
