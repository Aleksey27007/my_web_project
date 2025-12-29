package com.totalizator.util;

import com.mysql.cj.jdbc.Driver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;


public class ConnectionPool {
    private static final Logger logger = LogManager.getLogger(ConnectionPool.class);
    private static final ReentrantLock instanceLock = new ReentrantLock();
    private static final String PROPERTIES_PATH = "src/main/resources/db.properties";
    private static final int CONNECTION_CAPACITY = 8;
    private static ConnectionPool instance;
    private final BlockingQueue<Connection> free = new LinkedBlockingQueue<>(CONNECTION_CAPACITY);
    private final BlockingQueue<Connection> used = new LinkedBlockingQueue<>(CONNECTION_CAPACITY);

    static {
        try {
            DriverManager.registerDriver(new Driver());
        } catch (SQLException e) {
            logger.warn("Driver has not register.");
            e.printStackTrace();
        }
    }

    
    private ConnectionPool() {
        Properties properties = new Properties();
        logger.info("Properties created");

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {

                Path pathProperties = Paths.get(PROPERTIES_PATH);
                try (InputStream fileInput = new FileInputStream(pathProperties.toFile())) {
                    logger.info("Try load input properties from file system");
                    properties.load(fileInput);
                }
            } else {
                logger.info("Try load input properties from classpath");
                properties.load(input);
            }
        } catch (IOException e) {
            logger.warn("Properties not found.");
            e.printStackTrace();
        }

        String dbUrl = properties.getProperty("db.url");
        String dbUser = properties.getProperty("db.user");
        String dbPassword = properties.getProperty("db.password");
        
        if (dbUrl == null || dbUser == null || dbPassword == null) {
            logger.error("Database properties are not set correctly. URL: {}, User: {}", dbUrl, dbUser);
            throw new RuntimeException("Database properties are not configured");
        }
        
        for (int i = 0; i < CONNECTION_CAPACITY; i++) {
            Connection connection = null;
            try {
                connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            } catch (SQLException e) {
                logger.error("Failed to create connection {}", i, e);
                e.printStackTrace();
            }
            if (connection != null) {
                free.add(connection);
            }
        }
        logger.info("Connection pool initialized with {} connections", free.size());
        
        if (free.isEmpty()) {
            logger.warn("Connection pool is empty! Check database connection settings.");
        }
    }

    
    public static ConnectionPool getInstance() {
        if (instance == null) {
            instanceLock.lock();
            try {
                if (instance == null) {
                    instance = new ConnectionPool();
                }
            } finally {
                instanceLock.unlock();
            }
        }
        return instance;
    }

    
    public Connection getConnection() {
        Connection connection;
        try {
            connection = free.take();
            used.put(connection);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while getting connection", e);
        }
        return connection;
    }

    
    public void releaseConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            used.remove(connection);
            free.put(connection);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while releasing connection", e);
        }
    }
}
