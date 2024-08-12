package ca.jrvs.apps.jdbc.services;

import ca.jrvs.apps.jdbc.entities.Position;
import ca.jrvs.apps.jdbc.repos.PositionDao;

import java.util.NoSuchElementException;
import java.util.Optional;

public class PositionService {

    private PositionDao dao;

    public PositionService(PositionDao dao) {
        this.dao = dao;
    }

    /**
     * Processes a buy order and updates the database accordingly
     * @param ticker The ticker symbol of the stock
     * @param numberOfShares The number of shares to buy
     * @param price The price per share
     * @return The position in our database after processing the buy
     */
    public Position buy(String ticker, int numberOfShares, double price){
        Optional<Position> optPosition = dao.findById(ticker);
        if(optPosition.isPresent()){
            dao.deleteById(ticker);
        }
        return dao.save(new Position(ticker, numberOfShares, price));
    }

    /**
     * Sells all shares of the given ticker symbol
     * @param ticker The ticker symbol of the stock to sell
     */
    public void sell(String ticker) {
        // Fetch the existing position for the ticker
        Optional<Position> optPosition = dao.findById(ticker);

        if (optPosition.isPresent()) {
            // Delete the position if it exists
            dao.deleteById(ticker);
        } else {
            throw new NoSuchElementException("Position with ticker " + ticker + " not found");
        }
    }

    /**
     * Fetches the current position for the given ticker symbol.
     * @param ticker The ticker symbol of the stock.
     * @return The Position object if found, or null if not found.
     */
    public Position getPosition(String ticker) {
        return dao.findById(ticker).orElse(null);
    }

}
