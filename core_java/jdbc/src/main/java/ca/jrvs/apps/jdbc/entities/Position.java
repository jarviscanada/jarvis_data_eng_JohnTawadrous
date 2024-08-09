package ca.jrvs.apps.jdbc.entities;

public class Position {

    private String ticker; // Unique identifier for the stock
    private int numOfShares; // Number of shares owned
    private double valuePaid; // Total amount paid for the shares

    // Default constructor
    public Position() {
    }

    // Parameterized constructor
    public Position(String ticker, int numOfShares, double valuePaid) {
        this.ticker = ticker;
        this.numOfShares = numOfShares;
        this.valuePaid = valuePaid;
    }

    // Getter for ticker
    public String getTicker() {
        return ticker;
    }

    // Setter for ticker
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    // Getter for number of shares
    public int getNumOfShares() {
        return numOfShares;
    }

    // Setter for number of shares
    public void setNumOfShares(int numOfShares) {
        this.numOfShares = numOfShares;
    }

    // Getter for value paid
    public double getValuePaid() {
        return valuePaid;
    }

    // Setter for value paid
    public void setValuePaid(double valuePaid) {
        this.valuePaid = valuePaid;
    }

    @Override
    public String toString() {
        return String.format("Position{ticker='%s', numOfShares=%d, valuePaid=%.2f}", ticker, numOfShares, valuePaid);
    }
}
