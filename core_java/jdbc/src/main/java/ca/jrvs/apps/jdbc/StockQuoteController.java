package ca.jrvs.apps.jdbc;
import ca.jrvs.apps.jdbc.entities.Position;
import ca.jrvs.apps.jdbc.entities.Quote;
import ca.jrvs.apps.jdbc.services.PositionService;
import ca.jrvs.apps.jdbc.services.QuoteService;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

public class StockQuoteController {

    private QuoteService quoteService;
    private PositionService positionService;

    public StockQuoteController(QuoteService quoteService, PositionService positionService) {
        this.quoteService = quoteService;
        this.positionService = positionService;
    }

    /**
     * User interface for our application
     */
    public void initClient() {
        Scanner scanner = new Scanner(System.in);
        String command = "";

        System.out.println("Welcome to the Stock Quote Application!");

        while (!command.equalsIgnoreCase("exit")) {
            System.out.println("Please enter a command (buy, sell, quote, exit):");
            command = scanner.nextLine().trim();

            try {
                switch (command.toLowerCase()) {
                    case "quote":
                        handleQuote(scanner);
                        break;
                    case "buy":
                        handleBuy(scanner);
                        break;
                    case "sell":
                        handleSell(scanner);
                        break;
                    case "exit":
                        System.out.println("Exiting the application. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid command. Please try again.");
                }
            } catch (IllegalArgumentException | NoSuchElementException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private void handleQuote(Scanner scanner) {
        System.out.println("Enter the ticker symbol to get the latest quote:");
        String ticker = scanner.nextLine().trim();

        Optional<Quote> quoteOpt = quoteService.getLatestQuote(ticker);
        if (quoteOpt.isPresent()) {
            Quote quote = quoteOpt.get();
            System.out.println("Latest Quote for " + ticker + ": " + quote.getPrice());
        } else {
            System.out.println("No quote found for ticker " + ticker);
        }
    }

    private void handleBuy(Scanner scanner) {
        System.out.println("Enter the ticker symbol to buy:");
        String ticker = scanner.nextLine().trim();

        // Fetch the latest quote data from the API
        Optional<Quote> quoteOpt = quoteService.getLatestQuote(ticker);
        if (!quoteOpt.isPresent()) {
            System.out.println("No quote found for ticker " + ticker);
            return;
        }

        Quote quote = quoteOpt.get();
        System.out.println("Latest Quote for " + ticker + ":");
        System.out.println("Price: $" + quote.getPrice());
        System.out.println("High: $" + quote.getHigh());
        System.out.println("Low: $" + quote.getLow());
        System.out.println("Volume: " + quote.getVolume());
        System.out.println("Previous Close: $" + quote.getPreviousClose());
        System.out.println("Change: $" + quote.getChange());
        System.out.println("Change Percent: " + quote.getChangePercent());

        System.out.println("Enter the number of shares to buy:");
        int shares = Integer.parseInt(scanner.nextLine().trim());

        // Calculate the total price based on the fetched quote price
        double pricePerShare = quote.getPrice();
        double totalPrice = pricePerShare * shares;

        // Perform the buy operation
        Position position = positionService.buy(ticker, shares, totalPrice);
        System.out.println("Bought " + shares + " shares of " + ticker + " at $" + pricePerShare + " per share.");
        System.out.println("Position updated: " + position.getTicker() + " with " + position.getNumOfShares() + " shares.");
    }

    private void handleSell(Scanner scanner) {
        System.out.println("Enter the ticker symbol to sell:");
        String ticker = scanner.nextLine().trim();

        try {
            // Fetch the stock position details
            Position position = positionService.getPosition(ticker);

            if (position == null) {
                System.out.println("No position found for ticker symbol: " + ticker);
                return;
            }

            // Display position details
            System.out.println("Current Position Details:");
            System.out.println("Ticker: " + position.getTicker());
            System.out.println("Number of Shares: " + position.getNumOfShares());
            System.out.println("Value Paid: $" + position.getValuePaid());

            // Confirm with the user before selling
            System.out.println("Do you want to proceed with selling all shares of " + ticker + "? (yes/no)");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if ("yes".equals(confirmation)) {
                positionService.sell(ticker);
                System.out.println("Sold all shares of " + ticker + ".");
            } else {
                System.out.println("Sale of " + ticker + " has been cancelled.");
            }

        } catch (NoSuchElementException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }
}