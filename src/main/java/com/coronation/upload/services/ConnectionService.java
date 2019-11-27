package com.coronation.upload.services;

import com.coronation.upload.domain.DataConnection;

import java.util.List;

/**
 * Created by Toyin on 7/26/19.
 */
public interface ConnectionService {
    DataConnection findByName(String name);
    DataConnection save(DataConnection connection) throws Exception;
    DataConnection edit(DataConnection previous, DataConnection newConnection) throws Exception;
    List<DataConnection> findAll();
    DataConnection findById(Long id) throws Exception;
}
