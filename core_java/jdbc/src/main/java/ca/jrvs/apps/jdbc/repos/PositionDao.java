package ca.jrvs.apps.jdbc.repos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ca.jrvs.apps.jdbc.CrudDao;
import ca.jrvs.apps.jdbc.entities.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositionDao implements CrudDao<Position, String> {

    private final Connection connection;
    private static final String INSERT = "INSERT INTO position (symbol, number_of_shares, value_paid) VALUES (?, ?, ?)";
    private static final String SELECT_ONE = "SELECT symbol, number_of_shares, value_paid FROM position WHERE symbol = ?";
    private static final String SELECT_ALL = "SELECT symbol, number_of_shares, value_paid FROM position";
    private static final String DELETE_ONE = "DELETE FROM position WHERE symbol = ?";
    private static final String DELETE_ALL = "DELETE FROM position";
    private static final String UPDATE = "UPDATE position SET number_of_shares = ?, value_paid = ? WHERE symbol = ?";

    private static final Logger logger = LoggerFactory.getLogger(PositionDao.class);

    /**
     * Constructs a PositionDao with the given database connection.
     * @param connection The database connection to use.
     */
    public PositionDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Saves a Position entity. If the entity exists, it updates it; otherwise, it creates a new one.
     * @param entity The Position entity to save.
     * @return The saved Position entity.
     * @throws IllegalArgumentException if the entity is null.
     */
    @Override
    public Position save(Position entity) throws IllegalArgumentException {
        if (exists(entity.getTicker())) {
            return update(entity);
        } else {
            return create(entity);
        }
    }

    /**
     * Creates a new Position in the database.
     * @param entity The Position entity to create.
     * @return The created Position entity.
     */
    private Position create(Position entity) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setString(1, entity.getTicker());
            statement.setInt(2, entity.getNumOfShares());
            statement.setDouble(3, entity.getValuePaid());
            statement.executeUpdate();
            return entity;
        } catch (SQLException e) {
            logger.error("Error creating position", e);
            throw new RuntimeException("Error creating position", e);
        }
    }

    /**
     * Updates an existing Position in the database.
     * @param entity The Position entity to update.
     * @return The updated Position entity.
     */
    private Position update(Position entity) {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setInt(1, entity.getNumOfShares());
            statement.setDouble(2, entity.getValuePaid());
            statement.setString(3, entity.getTicker());
            statement.executeUpdate();
            return entity;
        } catch (SQLException e) {
            logger.error("Error updating position", e);
            throw new RuntimeException("Error updating position", e);
        }
    }

    /**
     * Retrieves a Position by its ticker symbol.
     * @param id The ticker symbol of the Position to retrieve.
     * @return An Optional containing the Position if found, or an empty Optional if not.
     * @throws IllegalArgumentException if the id is null.
     */
    @Override
    public Optional<Position> findById(String id) throws IllegalArgumentException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ONE)) {
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Position position = new Position();
                position.setTicker(rs.getString("symbol"));
                position.setNumOfShares(rs.getInt("number_of_shares"));
                position.setValuePaid(rs.getDouble("value_paid"));
                return Optional.of(position);
            }
        } catch (SQLException e) {
            logger.error("Error finding position by id", e);
            throw new RuntimeException("Error finding position by id", e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all Position entities from the database.
     * @return An Iterable of all Position entities.
     */
    @Override
    public Iterable<Position> findAll() {
        List<Position> positions = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Position position = new Position();
                position.setTicker(rs.getString("symbol"));
                position.setNumOfShares(rs.getInt("number_of_shares"));
                position.setValuePaid(rs.getDouble("value_paid"));
                positions.add(position);
            }
        } catch (SQLException e) {
            logger.error("Error finding all positions", e);
            throw new RuntimeException("Error finding all positions", e);
        }
        return positions;
    }

    /**
     * Deletes a Position by its ticker symbol.
     * @param id The ticker symbol of the Position to delete.
     * @throws IllegalArgumentException if the id is null.
     */
    @Override
    public void deleteById(String id) throws IllegalArgumentException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_ONE)) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting position by id", e);
            throw new RuntimeException("Error deleting position by id", e);
        }
    }

    /**
     * Deletes all Position entities from the database.
     */
    @Override
    public void deleteAll() {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_ALL)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting all positions", e);
            throw new RuntimeException("Error deleting all positions", e);
        }
    }

    /**
     * Checks if a Position with the given ticker symbol exists.
     * @param id The ticker symbol to check.
     * @return True if the Position exists, otherwise false.
     */
    public boolean exists(String id) {
        return findById(id).isPresent();
    }
}
