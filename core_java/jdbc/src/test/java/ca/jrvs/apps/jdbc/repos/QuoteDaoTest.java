package ca.jrvs.apps.jdbc.repos;

import static org.junit.jupiter.api.Assertions.*;

import ca.jrvs.apps.jdbc.entities.Quote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuoteDaoTest {

    @Mock
    private Connection dbConnection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private QuoteDao quoteDao;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(dbConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    void findById_existingQuote_returnsQuote() throws SQLException {
        // Arrange
        String ticker = "AAPL";
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("symbol")).thenReturn(ticker);
        when(resultSet.getDouble("open")).thenReturn(150.0);
        when(resultSet.getDouble("high")).thenReturn(155.0);
        when(resultSet.getDouble("low")).thenReturn(148.0);
        when(resultSet.getDouble("price")).thenReturn(152.0);
        when(resultSet.getInt("volume")).thenReturn(1000);
        when(resultSet.getDate("latest_trading_day")).thenReturn(new Date(System.currentTimeMillis()));
        when(resultSet.getDouble("previous_close")).thenReturn(149.0);
        when(resultSet.getDouble("change")).thenReturn(3.0);
        when(resultSet.getString("change_percent")).thenReturn("2%");
        when(resultSet.getTimestamp("timestamp")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Act
        Optional<Quote> result = quoteDao.findById(ticker);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ticker, result.get().getTicker());
        assertEquals(150.0, result.get().getOpen());
        assertEquals(155.0, result.get().getHigh());
        assertEquals(148.0, result.get().getLow());
        assertEquals(152.0, result.get().getPrice());
        assertEquals(1000, result.get().getVolume());
        assertEquals(new Date(System.currentTimeMillis()), result.get().getLatestTradingDay());
        assertEquals(149.0, result.get().getPreviousClose());
        assertEquals(3.0, result.get().getChange());
        assertEquals("2%", result.get().getChangePercent());
        assertNotNull(result.get().getTimestamp());
    }

    @Test
    void findById_nonExistingQuote_returnsEmpty() throws SQLException {
        // Arrange
        String ticker = "AAPL";
        when(resultSet.next()).thenReturn(false);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Act
        Optional<Quote> result = quoteDao.findById(ticker);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void deleteById_existingQuote_deletesQuote() throws SQLException {
        // Arrange
        String ticker = "AAPL";

        // Act
        quoteDao.deleteById(ticker);

        // Assert
        verify(preparedStatement).setString(1, ticker);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void deleteAll_deletesAllQuotes() throws SQLException {
        // Act
        quoteDao.deleteAll();

        // Assert
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void exists_quoteExists_returnsTrue() throws SQLException {
        // Arrange
        String ticker = "AAPL";
        when(resultSet.next()).thenReturn(true);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Act
        boolean exists = quoteDao.exists(ticker);

        // Assert
        assertTrue(exists);
    }

    @Test
    void exists_quoteDoesNotExist_returnsFalse() throws SQLException {
        // Arrange
        String ticker = "AAPL";
        when(resultSet.next()).thenReturn(false);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Act
        boolean exists = quoteDao.exists(ticker);

        // Assert
        assertFalse(exists);
    }

    private Quote createTestQuote() {
        Quote quote = new Quote();
        quote.setTicker("AAPL");
        quote.setOpen(150.0);
        quote.setHigh(155.0);
        quote.setLow(148.0);
        quote.setPrice(152.0);
        quote.setVolume(1000);
        quote.setLatestTradingDay(new java.util.Date());
        quote.setPreviousClose(149.0);
        quote.setChange(3.0);
        quote.setChangePercent("2%");
        quote.setTimestamp(new Timestamp(System.currentTimeMillis()));
        return quote;
    }
}