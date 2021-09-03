import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class to parse a json response containing a stock price
 * Dependant on the json-simple package
 * (add as library the json-simple-1.1.1.jar file)
 */
public class StockPriceParser {
    /**
     * Parse out the price from a json string
     *
     * @param jsonString the string containing price : value
     * @return the price
     */
    public static double parsePrice(String jsonString) {
        double price = 0.0;
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
            price = (Double) (jsonObject.get("price"));
        } catch (org.json.simple.parser.ParseException e) {
            System.out.println("Invalid JSON to parse");
            e.printStackTrace();
            return 0.0;
        } catch (NullPointerException e) {
            System.out.println("No JSON string passed in");
            e.printStackTrace();
            return 0.0;
        }
        return price;
    }


    //GETS THE information USING GET method USING HttpURLConnection Object
    public Double call_me(String ticker) throws Exception {
        String url = "https://financialmodelingprep.com/api/v3/stock/real-time-price/";
        String inputLine;
        //appending the ticket into the url
        URL myUrl = new URL((url+ticker));
        //creating the connection by calling .openConnection();
        HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
        BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuffer stockLine = new StringBuffer();
        while ((inputLine = input.readLine()) != null) {
            stockLine.append(inputLine);
        }
        input.close();
        //return in String
        double price = parsePrice(stockLine.toString());
        return price;
    }
}

