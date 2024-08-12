package ca.jrvs.apps.jdbc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import ca.jrvs.apps.jdbc.repos.PositionDao;
import ca.jrvs.apps.jdbc.repos.QuoteDao;
import ca.jrvs.apps.jdbc.services.PositionService;
import ca.jrvs.apps.jdbc.services.QuoteService;
import okhttp3.OkHttpClient;

public class Main {

    public static void main(String[] args) {
        Map<String, String> properties = new HashMap<>();

        // Read properties from file
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/properties.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(":");
                if (tokens.length == 2) { // Ensure proper format
                    properties.put(tokens[0].trim(), tokens[1].trim());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load database driver
        try {
            Class.forName(properties.get("db-class"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Setup OkHttpClient and database connection
        OkHttpClient client = new OkHttpClient();
        String url = "jdbc:postgresql://" + properties.get("server") + ":" + properties.get("port") + "/" + properties.get("database");

        try (Connection dbConnection = DriverManager.getConnection(url, properties.get("username"), properties.get("password"))) {
            // Initialize DAOs and services
            QuoteDao qRepo = new QuoteDao(dbConnection);
            PositionDao pRepo = new PositionDao(dbConnection);
            QuoteHttpHelper rcon = new QuoteHttpHelper(properties.get("api-key"), client);
            QuoteService sQuote = new QuoteService(qRepo, rcon);
            PositionService sPos = new PositionService(pRepo);
            StockQuoteController con = new StockQuoteController(sQuote, sPos);

            // Start the application
            con.initClient();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
