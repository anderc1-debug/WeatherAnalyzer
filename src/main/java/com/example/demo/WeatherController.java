package com.example.demo;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.Month;
import java.util.List;

/**
 * ## WeatherController
 *
 * JavaFX controller wiring the UI to WeatherAnalyzer logic.
 * No explicit classes — uses records and interface static methods.
 */
public class WeatherController {

    @FXML private Label summaryLabel;
    @FXML private TextArea outputArea;
    @FXML private ComboBox<String> monthCombo;
    @FXML private TextField thresholdField;
    @FXML private BarChart<String, Number> tempChart;
    @FXML private Label fileLabel;

    private List<WeatherRecord> records = List.of();

    /**
     * ## initialize
     * Populates the month combo box on startup.
     */
    @FXML
    public void initialize() {
        var months = java.util.Arrays.stream(Month.values())
                .map(m -> m.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH))
                .toList();
        monthCombo.setItems(FXCollections.observableArrayList(months));
        monthCombo.getSelectionModel().selectFirst();
        thresholdField.setText("30.0");
    }

    /**
     * ## handleLoadFile
     * Opens a file chooser and loads the selected CSV.
     */
    @FXML
    public void handleLoadFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Weather CSV");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            records = WeatherAnalyzer.loadCSV(file.getAbsolutePath());
            fileLabel.setText("Loaded: " + file.getName() + " (" + records.size() + " records)");
            outputArea.setText("✅ File loaded successfully. Select a month and click Analyze.");
            populateChart();
        }
    }

    /**
     * ## handleAnalyze
     * Runs analysis based on selected month and threshold.
     */
    @FXML
    public void handleAnalyze() {
        if (records.isEmpty()) {
            outputArea.setText("⚠️ Please load a CSV file first.");
            return;
        }

        int month = monthCombo.getSelectionModel().getSelectedIndex() + 1;
        double threshold;
        try {
            threshold = Double.parseDouble(thresholdField.getText().trim());
        } catch (NumberFormatException e) {
            outputArea.setText("⚠️ Invalid threshold value.");
            return;
        }

        String summary = WeatherAnalyzer.formatSummary(records, month);

        var hotDays = WeatherAnalyzer.daysAboveThreshold(records, threshold);
        StringBuilder sb = new StringBuilder(summary);
        sb.append("\n Days above ").append(threshold).append("°C:\n");

        if (hotDays.isEmpty()) {
            sb.append("  None found.\n");
        } else {
            hotDays.forEach(r -> sb.append("  • ")
                    .append(r.date())
                    .append(" → ")
                    .append(r.temperature())
                    .append("°C [")
                    .append(r.category())
                    .append("]\n"));
        }

        outputArea.setText(sb.toString());
    }

    /**
     * ## populateChart
     * Builds a bar chart of monthly average temperatures.
     */
    private void populateChart() {
        tempChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Avg Temp (°C)");

        for (int m = 1; m <= 12; m++) {
            double avg = WeatherAnalyzer.averageTemp(records, m);
            if (avg != 0.0) {
                String monthName = Month.of(m).getDisplayName(
                        java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH);
                series.getData().add(new XYChart.Data<>(monthName, avg));
            }
        }
        tempChart.getData().add(series);
    }
}