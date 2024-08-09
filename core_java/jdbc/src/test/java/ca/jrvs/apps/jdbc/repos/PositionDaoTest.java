package ca.jrvs.apps.jdbc.repos;

import static org.junit.jupiter.api.Assertions.*;

import ca.jrvs.apps.jdbc.entities.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PositionDaoTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private PositionDao positionDao;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    void findById_existingPosition_returnsPosition() throws SQLException {
        // Arrange
        String ticker = "AAPL";
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("symbol")).thenReturn(ticker);
        when(resultSet.getInt("number_of_shares")).thenReturn(100);
        when(resultSet.getDouble("value_paid")).thenReturn(15000.0);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Act
        Optional<Position> result = positionDao.findById(ticker);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ticker, result.get().getTicker());
        assertEquals(100, result.get().getNumOfShares());
        assertEquals(15000.0, result.get().getValuePaid());
    }

    @Test
    void findById_nonExistingPosition_returnsEmpty() throws SQLException {
        // Arrange
        String ticker = "AAPL";
        when(resultSet.next()).thenReturn(false);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Act
        Optional<Position> result = positionDao.findById(ticker);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void deleteById_existingPosition_deletesPosition() throws SQLException {
        // Arrange
        String ticker = "AAPL";

        // Act
        positionDao.deleteById(ticker);

        // Assert
        verify(preparedStatement, times(1)).setString(1, ticker);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void deleteAll_deletesAllPositions() throws SQLException {
        // Act
        positionDao.deleteAll();

        // Assert
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void exists_positionExists_returnsTrue() throws SQLException {
        // Arrange
        String ticker = "AAPL";
        when(resultSet.next()).thenReturn(true);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Act
        boolean exists = positionDao.exists(ticker);

        // Assert
        assertTrue(exists);
    }

    @Test
    void exists_positionDoesNotExist_returnsFalse() throws SQLException {
        // Arrange
        String ticker = "AAPL";
        when(resultSet.next()).thenReturn(false);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Act
        boolean exists = positionDao.exists(ticker);

        // Assert
        assertFalse(exists);
    }
}