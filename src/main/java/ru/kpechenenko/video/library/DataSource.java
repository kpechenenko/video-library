package ru.kpechenenko.video.library;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class DataSource {
    private static final String PATH_TO_APPLICATION_PROPERTIES_FILE = "./src/main/resources/application.properties";
    private final HikariDataSource dataSource;

    public DataSource() {
        this(PATH_TO_APPLICATION_PROPERTIES_FILE);
    }

    public DataSource(String pathToPropertiesFile) {
        this.dataSource = new HikariDataSource(new HikariConfig(pathToPropertiesFile));
    }

    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }
}
