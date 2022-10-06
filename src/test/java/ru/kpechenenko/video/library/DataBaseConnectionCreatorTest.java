package ru.kpechenenko.video.library;

import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public final class DataBaseConnectionCreatorTest {
    @Test
    public void shouldReturnConnectionToExistingDatabase() {
        try (var connection = DataSource.getConnection()) {
            assertTrue(connection.isValid(1));
            assertFalse(connection.isClosed());
        } catch (SQLException e) {
            fail();
            e.printStackTrace();
        }
    }
}
