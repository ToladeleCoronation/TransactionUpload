package com.coronation.upload.services.impl;

import com.coronation.upload.domain.DataConnection;
import com.coronation.upload.repo.ConnectionRepository;
import com.coronation.upload.services.ConnectionService;
import com.coronation.upload.util.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by Toyin on 7/26/19.
 */
@Service
public class ConnectionServiceImpl implements ConnectionService {
    private ConnectionRepository connectionRepository;
    private Utilities utilities;
    @Value("${app.secret.key}")
    private String appKey;

    @Autowired
    public ConnectionServiceImpl(ConnectionRepository connectionRepository, Utilities utilities) {
        this.connectionRepository = connectionRepository;
        this.utilities = utilities;
    }

    @Override
    public DataConnection findByName(String name) {
        return connectionRepository.findByNameEquals(name);
    }

    @Override
    public DataConnection save(DataConnection connection) throws Exception {
        if (connection.getPassword() != null && !connection.getPassword().isEmpty()) {
            connection.setPassword(utilities.encrypt(connection.getPassword(), appKey));
        }
        return connectionRepository.saveAndFlush(connection);
    }

    @Override
    public DataConnection edit(DataConnection previous, DataConnection newConnection) throws Exception {
        previous.setUsername(newConnection.getUsername());
        if (newConnection.getPassword() != null && !newConnection.getPassword().isEmpty()) {
            previous.setPassword(utilities.encrypt(newConnection.getPassword(), appKey));
        } else {
            previous.setPassword(newConnection.getPassword());
        }
        previous.setModifiedAt(LocalDateTime.now());
        return connectionRepository.saveAndFlush(previous);
    }

    @Override
    public List<DataConnection> findAll() {
        List<DataConnection> changedValue= connectionRepository.findAll();
         for(DataConnection con: changedValue)
         {
             con.setPassword(utilities.decrypt(con.getPassword(), appKey));
         }

        return changedValue;

    }

    @Override
    public DataConnection findById(Long id) throws Exception {
        Optional<DataConnection> optional = connectionRepository.findById(id);
        if (optional.isPresent()) {
            DataConnection connection = optional.get();
            connection.setPassword(utilities.decrypt(connection.getPassword(), appKey));
            return connection;
        } else {
            return null;
        }
    }
}
