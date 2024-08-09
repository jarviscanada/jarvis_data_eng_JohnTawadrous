package ca.jrvs.apps.jdbc;

import ca.jrvs.apps.jdbc.entities.Quote;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

public class QuoteHttpHelper {

    private static final Logger logger = LoggerFactory.getLogger(QuoteHttpHelper.class);
    private String apiKey;
    private OkHttpClient client;

    public QuoteHttpHelper(String apiKey, OkHttpClient client) {
        this.apiKey = apiKey;
        this.client = client;
    }

    public Quote fetchQuoteInfo(String symbol) throws IllegalArgumentException {
        Request request = new Request.Builder()
                .url("https://alpha-vantage.p.rapidapi.com/query?function=GLOBAL_QUOTE&symbol=" + symbol + "&datatype=json")
                .get()
                .addHeader("x-rapidapi-key", apiKey)
                .addHeader("x-rapidapi-host", "alpha-vantage.p.rapidapi.com")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }
            String jsonValue = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonValue);
            JsonNode quoteNode = rootNode.path("Global Quote");
            Quote quote = objectMapper.treeToValue(quoteNode, Quote.class);
            quote.setTimestamp(Timestamp.from(Instant.now()));
            return quote;
        } catch (IOException e) {
            logger.error("Error fetching quote info for symbol: " + symbol, e);
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();
        QuoteHttpHelper helper = new QuoteHttpHelper("b6e8215b13msh0de0d65e4721eaep12e2ffjsnc28a2f035c37", client);

        try {
            Quote quote = helper.fetchQuoteInfo("AAPL");
            System.out.println(quote);
        } catch (IllegalArgumentException e) {
            logger.error("Error retrieving quote", e);
        }
    }
}
