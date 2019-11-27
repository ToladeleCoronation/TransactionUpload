package com.coronation.upload.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Toyin on 7/29/19.
 */
@Configuration
public class DataConfig {
    @Value("${app.datasource.url}")
    private String customDbUrl;

    @Value("${app.datasource.username}")
    private String customDbUsername;

    @Value("${app.datasource.password}")
    private String customDbPassword;

    private Logger logger = LogManager.getLogger(DataConfig.class);

    @Bean
    public Connection connection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(customDbUrl, customDbUsername, customDbPassword);
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
    }
}
