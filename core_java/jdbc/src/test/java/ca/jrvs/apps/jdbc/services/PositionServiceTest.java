package ca.jrvs.apps.jdbc.services;

import static org.junit.jupiter.api.Assertions.*;

import ca.jrvs.apps.jdbc.entities.Position;
import ca.jrvs.apps.jdbc.repos.PositionDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.NoSuchElementException;
import java.util.Optional;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PositionServiceTest {

    private PositionDao mockDao;
    private PositionService positionService;

    @Before
    public void setUp() {
        mockDao = Mockito.mock(PositionDao.class);
        positionService = new PositionService(mockDao);
    }

    @Test
    public void buy_existingPosition_updatesPosition() {
        // Arrange
        String ticker = "AAPL";
        Position existingPosition = new Position(ticker, 5, 150.00);
        Position newPosition = new Position(ticker, 10, 150.00);

        when(mockDao.findById(ticker)).thenReturn(Optional.of(existingPosition));
        when(mockDao.save(any(Position.class))).thenReturn(newPosition);

        // Act
        Position result = positionService.buy(ticker, 10, 150.00);

        // Assert
        verify(mockDao).deleteById(ticker);
        verify(mockDao).save(new Position(ticker, 10, 150.00));
        assertEquals(newPosition, result);
    }

    @Test
    public void buy_noExistingPosition_createsNewPosition() {
        // Arrange
        String ticker = "AAPL";
        Position newPosition = new Position(ticker, 10, 150.00);

        when(mockDao.findById(ticker)).thenReturn(Optional.empty());
        when(mockDao.save(any(Position.class))).thenReturn(newPosition);

        // Act
        Position result = positionService.buy(ticker, 10, 150.00);

        // Assert
        verify(mockDao, never()).deleteById(ticker);
        verify(mockDao).save(new Position(ticker, 10, 150.00));
        assertEquals(newPosition, result);
    }

    @Test(expected = NoSuchElementException.class)
    public void sell_noExistingPosition_throwsException() {
        // Arrange
        String ticker = "AAPL";

        when(mockDao.findById(ticker)).thenReturn(Optional.empty());

        // Act
        positionService.sell(ticker);

        // Assert
        // Exception is expected, no further assertions necessary
    }

    @Test
    public void sell_existingPosition_deletesPosition() {
        // Arrange
        String ticker = "AAPL";
        Position existingPosition = new Position(ticker, 10, 150.00);

        when(mockDao.findById(ticker)).thenReturn(Optional.of(existingPosition));

        // Act
        positionService.sell(ticker);

        // Assert
        verify(mockDao).deleteById(ticker);
    }

    @Test
    public void getPosition_existingPosition_returnsPosition() {
        // Arrange
        String ticker = "AAPL";
        Position existingPosition = new Position(ticker, 10, 150.00);

        when(mockDao.findById(ticker)).thenReturn(Optional.of(existingPosition));

        // Act
        Position result = positionService.getPosition(ticker);

        // Assert
        assertEquals(existingPosition, result);
    }

    @Test
    public void getPosition_noExistingPosition_returnsNull() {
        // Arrange
        String ticker = "AAPL";

        when(mockDao.findById(ticker)).thenReturn(Optional.empty());

        // Act
        Position result = positionService.getPosition(ticker);

        // Assert
        assertNull(result);
    }
}