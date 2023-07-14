/**
 * @author Bardski Grzegorz S20198
 */

package zad2;


import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class Service {
    private final static String WEATHER_API_KEY = "bfb8376f9652c03de99f903e78a20918";
    private String countryCurrency;
    private String city;

    public Service(String country) {
        // Po podaniu kraju mapujemy jego nazwę na walutę tam obowiązującą
        countryCurrency = readCurrencies(country);
    }

    public String getWeather(String city) {
        this.city = city;
        String endpoint = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + WEATHER_API_KEY;
        System.out.println("[*] Requesting weather for city: " + city);
        return handleConnection(endpoint);
    }

    public Double getRateFor(String currency) {
        String endpoint = "https://api.exchangerate.host/latest?base=" + countryCurrency + "?symbols=" + currency;
        String rateAnswer = handleConnection(endpoint);
        System.out.println("[*] Requesting rate: " + countryCurrency + " to: " + currency);
        JSONObject jsonObject = new JSONObject(rateAnswer);

        return jsonObject.getJSONObject("rates").getDouble(currency);
    }

    public Double getNBPRate() {
        if (countryCurrency.equals("PLN")) {
            return 0.00;
        } else {
            String nbpURL = "http://api.nbp.pl/api/exchangerates/rates/A/" + countryCurrency + "?format=json\n";
            System.out.println("[*] Requesting nbp rate for currency: " + countryCurrency);
            String rate = handleConnection(nbpURL);
            JSONObject jsonObject = new JSONObject(rate);

            return jsonObject.getJSONArray("rates").getJSONObject(0).getDouble("mid");
        }
    }

    private String handleConnection(String address) {
        StringBuilder responseBuilder = new StringBuilder();
        try {
            URL url = new URL(address);
            HttpURLConnection requestRate = (HttpURLConnection) url.openConnection();
            requestRate.setRequestMethod("GET");
            requestRate.connect();
//            System.out.println("[*] Connection established");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(requestRate.getInputStream()));
            String inputLine;

//            System.out.println("[*] Reading response");

            while ((inputLine = in.readLine()) != null) {
                responseBuilder.append(inputLine);
            }

//            System.out.println("[*] Finished reading response");
            in.close();
        } catch (MalformedURLException exc) {
            exc.printStackTrace();
            System.exit(1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return String.valueOf(responseBuilder);
    }

    private String readCurrencies(String countryLookup) {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("currency-mapping.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        TreeMap<String, String> currencyMap = new TreeMap<>();
        for (List<String> s : records) {
            currencyMap.put(s.get(0), s.get(3));
        }

        return currencyMap.get(countryLookup);
    }

    public String getCity() {
        return city;
    }
}