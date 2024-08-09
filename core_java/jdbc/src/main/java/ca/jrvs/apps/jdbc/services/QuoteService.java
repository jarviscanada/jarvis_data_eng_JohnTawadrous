package ca.jrvs.apps.jdbc.services;

import ca.jrvs.apps.jdbc.QuoteHttpHelper;
import ca.jrvs.apps.jdbc.entities.Quote;
import ca.jrvs.apps.jdbc.repos.QuoteDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

public class QuoteService {

    private final QuoteDao quoteDAO;
    private final QuoteHttpHelper httpHelper;
    private static final Logger logger = LoggerFactory.getLogger(QuoteService.class);

    public QuoteService(QuoteDao quoteDAO, QuoteHttpHelper httpHelper) {
        this.quoteDAO = quoteDAO;
        this.httpHelper = httpHelper;
    }

    /**
     * Fetches the latest quote data from an external API and saves it to the database.
     * @param ticker The stock ticker symbol.
     * @return An Optional containing the latest Quote if successful; otherwise, an empty Optional.
     */
    public Optional<Quote> fetchQuoteDataFromAPI(String ticker) {
        try {
            logger.info("Fetching quote data for ticker: {}", ticker);

            Quote quote = httpHelper.fetchQuoteInfo(ticker);

            if (quote.getTicker() == null) {
                logger.warn("Quote data for ticker {} is not valid", ticker);
                return Optional.empty();
            }

            // Save the fetched quote in the database
            quoteDAO.save(quote);
            return Optional.of(quote);
        } catch (Exception e) {
            logger.error("Failed to fetch or save quote data for ticker {}", ticker, e);
            return Optional.empty();
        }
    }

    /**
     * Retrieves the latest quote for the given ticker from the database.
     * If the quote is outdated or doesn't exist, fetches it from the API.
     * @param ticker The stock ticker symbol.
     * @return The latest Quote.
     * @throws NoSuchElementException if the quote cannot be found.
     */
    public Optional<Quote> getLatestQuote(String ticker) {
        // Try to find the quote in the database
        Optional<Quote> quoteOpt = quoteDAO.findById(ticker);

        if (quoteOpt.isPresent()) {
            return quoteOpt;
        } else {
            // If not found in the database, fetch it from the API
            Optional<Quote> fetchedQuoteOpt = fetchQuoteDataFromAPI(ticker);
            return fetchedQuoteOpt;
        }
    }


}
