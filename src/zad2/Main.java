/**
 * @author Bardski Grzegorz S20198
 */

package zad2;


import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    public static void main(String[] args) {
        Service s = new Service("Poland");
        String weatherJson = s.getWeather("Warsaw");
        Double rate1 = s.getRateFor("USD");
        Double rate2 = s.getNBPRate();
        // ...
        // część uruchamiająca GUI
        SwingUtilities.invokeLater(() -> createAndDisplayUI(s));
    }

    private static void createAndDisplayUI(Service frameService) {
        JFrame mainFrame = new JFrame("WebClients");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setPreferredSize(new Dimension(860, 640));
        mainFrame.setLayout(new BorderLayout());

        // Utworzenie panelu górnego
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(4, 2, 5, 5));

        topPanel.add(new JLabel("Country:"), 0);
        JTextField countryTextField = new JTextField("Spain");
        topPanel.add(countryTextField, 1);

        topPanel.add(new JLabel("City:"), 2);
        JTextField cityTextField = new JTextField("Madrid");
        topPanel.add(cityTextField, 3);

        topPanel.add(new JLabel("Currency:"), 4);
        JTextField currencyTextField = new JTextField("GBP");
        topPanel.add(currencyTextField, 5);


        // Utworzenie środkowego panelu
        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new GridLayout(3, 2));

        dataPanel.add(new JLabel("Weather:"), 0);
        JLabel weatherLabel = new JLabel("");
        dataPanel.add(weatherLabel, 1);

        dataPanel.add(new JLabel("Exchange Rate:"), 2);
        // JLabel exchangeRateLabel = new JLabel("3.14");

        JLabel exchangeRateLabel = new JLabel("");
        dataPanel.add(exchangeRateLabel, 3);

        dataPanel.add(new JLabel("Local currency to PLN:"), 4);

        JLabel currencyPLNLabel = new JLabel("");
        dataPanel.add(currencyPLNLabel, 5);

        JButton submitButton = new JButton("Submit");

        // Panel dolny ze stroną wiki z opisem miasta
        JFXPanel wikiPanel = new JFXPanel();

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Service newService = new Service(countryTextField.getText());

                    String newWeatherValue = newService.getWeather(cityTextField.getText());
                    weatherLabel.setText(newWeatherValue);

                    Double exchangeRateValue = newService.getRateFor(currencyTextField.getText());
                    exchangeRateLabel.setText(exchangeRateValue.toString());

                    Double currencyPLNValue = newService.getNBPRate();
                    currencyPLNLabel.setText(currencyPLNValue.toString());

                    Platform.runLater(() -> initializeFX(wikiPanel, newService.getCity()));
                } catch (Exception ex) {
                    weatherLabel.setText("Error");
                    exchangeRateLabel.setText("Error");
                    currencyPLNLabel.setText("Error");
                    ex.printStackTrace();
                }
            }
        });

        topPanel.add(submitButton);

        // Dodanie panelu górnego do głównego okna
        mainFrame.add(topPanel, "North");

        // Panel dolny - z odpowiedziami
        mainFrame.add(dataPanel, "South");

        // Panel centralny z wiki
        mainFrame.add(wikiPanel, "Center");
        mainFrame.setVisible(true);

        wikiPanel.setLocation(new Point(0, 0));

        mainFrame.pack();

        Platform.runLater(() -> initializeFX(wikiPanel, frameService.getCity()));
    }

    private static void initializeFX(JFXPanel jfxPanel, String city) {
        Group group = new Group();
        Scene scene = new Scene(group);
        jfxPanel.setScene(scene);
        WebView webView = new WebView();
        group.getChildren().add(webView);

        WebEngine wikiWebEngine = webView.getEngine();
        wikiWebEngine.load("https://en.wikipedia.org/wiki/" + city);
    }
}
