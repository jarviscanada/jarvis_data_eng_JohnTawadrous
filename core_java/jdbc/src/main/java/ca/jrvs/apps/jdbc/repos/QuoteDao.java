package ca.jrvs.apps.jdbc.repos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ca.jrvs.apps.jdbc.CrudDao;
import ca.jrvs.apps.jdbc.entities.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object (DAO) for managing `Quote` entities in the database.
 * Provides methods for creating, reading, updating, and deleting `Quote` records.
 */
public class QuoteDao implements CrudDao<Quote, String> {

    private final Connection dbConnection; // Database connection
    private final Logger logger = LoggerFactory.getLogger(QuoteDao.class);

    // SQL statements for CRUD operations
    private static final String INSERT = "INSERT INTO quote (symbol, open, high, low, price, volume, latest_trading_day, previous_close, change, change_percent, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID = "SELECT * FROM quote WHERE symbol = ?";
    private static final String SELECT_ALL = "SELECT * FROM quote";
    private static final String DELETE_BY_ID = "DELETE FROM quote WHERE symbol = ?";
    private static final String DELETE_ALL = "DELETE FROM quote";

    /**
     * Constructs a `QuoteDao` with a given database connection.
     *
     * @param dbConnection the database connection to use
     */
    public QuoteDao(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    /**
     * Saves a `Quote` entity by either creating a new record or updating an existing one.
     *
     * @param entity the `Quote` entity to save
     * @return the saved `Quote` entity
     */
    @Override
    public Quote save(Quote entity) {
        if (exists(entity.getTicker())) {
            update(entity); // Update existing record
        } else {
            create(entity); // Create new record
        }
        return entity;
    }

    /**
     * Creates a new `Quote` record in the database.
     *
     * @param entity the `Quote` entity to create
     */
    private void create(Quote entity) {
        try (PreparedStatement stmt = dbConnection.prepareStatement(INSERT)) {
            stmt.setString(1, entity.getTicker());
            stmt.setDouble(2, entity.getOpen());
            stmt.setDouble(3, entity.getHigh());
            stmt.setDouble(4, entity.getLow());
            stmt.setDouble(5, entity.getPrice());
            stmt.setInt(6, entity.getVolume());

            // Convert java.util.Date to java.sql.Date
            stmt.setDate(7, new java.sql.Date(entity.getLatestTradingDay().getTime()));

            stmt.setDouble(8, entity.getPreviousClose());
            stmt.setDouble(9, entity.getChange());
            stmt.setString(10, entity.getChangePercent());
            stmt.setTimestamp(11, new java.sql.Timestamp(entity.getTimestamp().getTime()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error creating Quote", e);
            throw new RuntimeException("Error creating Quote", e);
        }
    }

    /**
     * Updates an existing `Quote` record in the database.
     *
     * @param entity the `Quote` entity to update
     */
    private void update(Quote entity) {
        String update = "UPDATE quote SET open = ?, high = ?, low = ?, price = ?, volume = ?, latest_trading_day = ?, previous_close = ?, change = ?, change_percent = ?, timestamp = ? WHERE symbol = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(update)) {
            stmt.setDouble(1, entity.getOpen());
            stmt.setDouble(2, entity.getHigh());
            stmt.setDouble(3, entity.getLow());
            stmt.setDouble(4, entity.getPrice());
            stmt.setInt(5, entity.getVolume());

            // Convert java.util.Date to java.sql.Date
            stmt.setDate(6, new java.sql.Date(entity.getLatestTradingDay().getTime()));

            stmt.setDouble(7, entity.getPreviousClose());
            stmt.setDouble(8, entity.getChange());
            stmt.setString(9, entity.getChangePercent());
            stmt.setTimestamp(10, new java.sql.Timestamp(entity.getTimestamp().getTime()));
            stmt.setString(11, entity.getTicker());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating Quote", e);
            throw new RuntimeException("Error updating Quote", e);
        }
    }

    /**
     * Finds a `Quote` by its ticker symbol.
     *
     * @param ticker the ticker symbol of the `Quote` to find
     * @return an `Optional` containing the `Quote` if found, otherwise empty
     */
    @Override
    public Optional<Quote> findById(String ticker) {
        try (PreparedStatement stmt = dbConnection.prepareStatement(SELECT_BY_ID)) {
            stmt.setString(1, ticker);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Quote quote = buildQuote(rs);
                return Optional.of(quote);
            }
        } catch (SQLException e) {
            logger.error("Error finding Quote by ID", e);
            throw new RuntimeException("Error finding Quote by ID", e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all `Quote` records from the database.
     *
     * @return a list of all `Quote` records
     */
    @Override
    public Iterable<Quote> findAll() {
        List<Quote> quotes = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.prepareStatement(SELECT_ALL)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                quotes.add(buildQuote(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all Quotes", e);
            throw new RuntimeException("Error finding all Quotes", e);
        }
        return quotes;
    }

    /**
     * Deletes a `Quote` record by its ticker symbol.
     *
     * @param ticker the ticker symbol of the `Quote` to delete
     */
    @Override
    public void deleteById(String ticker) {
        try (PreparedStatement stmt = dbConnection.prepareStatement(DELETE_BY_ID)) {
            stmt.setString(1, ticker);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting Quote by ID", e);
            throw new RuntimeException("Error deleting Quote by ID", e);
        }
    }

    /**
     * Deletes all `Quote` records from the database.
     */
    @Override
    public void deleteAll() {
        try (PreparedStatement stmt = dbConnection.prepareStatement(DELETE_ALL)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting all Quotes", e);
            throw new RuntimeException("Error deleting all Quotes", e);
        }
    }

    /**
     * Checks if a `Quote` with the given ticker symbol exists in the database.
     *
     * @param ticker the ticker symbol to check
     * @return true if the `Quote` exists, false otherwise
     */
    @Override
    public boolean exists(String ticker) {
        return findById(ticker).isPresent();
    }

    /**
     * Builds a `Quote` object from the given `ResultSet`.
     *
     * @param rs the `ResultSet` containing the `Quote` data
     * @return the `Quote` object
     * @throws SQLException if there is an error accessing the `ResultSet`
     */
    private Quote buildQuote(ResultSet rs) throws SQLException {
        Quote quote = new Quote();
        quote.setTicker(rs.getString("symbol"));
        quote.setOpen(rs.getDouble("open"));
        quote.setHigh(rs.getDouble("high"));
        quote.setLow(rs.getDouble("low"));
        quote.setPrice(rs.getDouble("price"));
        quote.setVolume(rs.getInt("volume"));
        quote.setLatestTradingDay(rs.getDate("latest_trading_day"));
        quote.setPreviousClose(rs.getDouble("previous_close"));
        quote.setChange(rs.getDouble("change"));
        quote.setChangePercent(rs.getString("change_percent"));
        quote.setTimestamp(rs.getTimestamp("timestamp"));
        return quote;
    }
}
