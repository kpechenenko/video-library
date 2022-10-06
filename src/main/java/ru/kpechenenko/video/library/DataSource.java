package ru.kpechenenko.video.library;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class DataSource {
    private static final String PATH_TO_APPLICATION_PROPERTIES_FILE = "./src/main/resources/application.properties";
    private static final HikariDataSource dataSource = new HikariDataSource(
        new HikariConfig(PATH_TO_APPLICATION_PROPERTIES_FILE)
    );

    private DataSource() {
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}