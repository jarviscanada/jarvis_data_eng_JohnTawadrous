package ca.jrvs.apps.jdbc.services;

import static org.junit.jupiter.api.Assertions.*;

import ca.jrvs.apps.jdbc.QuoteHttpHelper;
import ca.jrvs.apps.jdbc.entities.Quote;
import ca.jrvs.apps.jdbc.repos.QuoteDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuoteServiceTest {

    @Mock
    private QuoteDao quoteDao;

    @Mock
    private QuoteHttpHelper httpHelper;

    @InjectMocks
    private QuoteService quoteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fetchQuoteDataFromAPI_validTicker_savesAndReturnsQuote() {
        // Arrange
        String ticker = "AAPL";
        Quote quote = new Quote();
        quote.setTicker(ticker);
        quote.setOpen(150.0);
        quote.setHigh(152.0);
        quote.setLow(148.0);
        quote.setPrice(149.0);
        quote.setVolume(1000000);
        quote.setChange(1.0);
        quote.setChangePercent("0.68%");
        quote.setLatestTradingDay(new Date());
        quote.setPreviousClose(148.0);

        when(httpHelper.fetchQuoteInfo(ticker)).thenReturn(quote);

        // Act
        Optional<Quote> result = quoteService.fetchQuoteDataFromAPI(ticker);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(quote, result.get());
        verify(quoteDao, times(1)).save(quote);
    }

    @Test
    void fetchQuoteDataFromAPI_invalidTicker_returnsEmptyOptional() {
        // Arrange
        String ticker = "INVALID";
        Quote quote = new Quote(); // Assume invalid ticker results in a quote with null or missing fields
        when(httpHelper.fetchQuoteInfo(ticker)).thenReturn(quote);

        // Act
        Optional<Quote> result = quoteService.fetchQuoteDataFromAPI(ticker);

        // Assert
        assertFalse(result.isPresent());
        verify(quoteDao, never()).save(any(Quote.class));
    }

    @Test
    void getLatestQuote_existingQuoteInDatabase_returnsQuote() {
        // Arrange
        String ticker = "AAPL";
        Quote quote = new Quote();
        quote.setTicker(ticker);
        quote.setOpen(150.0);
        quote.setHigh(152.0);
        quote.setLow(148.0);
        quote.setPrice(149.0);
        quote.setVolume(1000000);
        quote.setChange(1.0);
        quote.setChangePercent("0.68%");
        quote.setLatestTradingDay(new Date());
        quote.setPreviousClose(148.0);

        when(quoteDao.findById(ticker)).thenReturn(Optional.of(quote));

        // Act
        Optional<Quote> result = quoteService.getLatestQuote(ticker);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(quote, result.get());
        verify(quoteDao, times(1)).findById(ticker);
        verify(httpHelper, never()).fetchQuoteInfo(ticker);
    }

    @Test
    void getLatestQuote_noQuoteInDatabase_fetchesFromAPI() {
        // Arrange
        String ticker = "AAPL";
        Quote quote = new Quote();
        quote.setTicker(ticker);
        quote.setOpen(150.0);
        quote.setHigh(152.0);
        quote.setLow(148.0);
        quote.setPrice(149.0);
        quote.setVolume(1000000);
        quote.setChange(1.0);
        quote.setChangePercent("0.68%");
        quote.setLatestTradingDay(new Date());
        quote.setPreviousClose(148.0);

        when(quoteDao.findById(ticker)).thenReturn(Optional.empty());
        when(httpHelper.fetchQuoteInfo(ticker)).thenReturn(quote);

        // Act
        Optional<Quote> result = quoteService.getLatestQuote(ticker);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(quote, result.get());
        verify(quoteDao, times(1)).findById(ticker);
        verify(httpHelper, times(1)).fetchQuoteInfo(ticker);
        verify(quoteDao, times(1)).save(quote);
    }
}