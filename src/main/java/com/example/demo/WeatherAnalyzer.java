package com.example.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Predicate;

/**
WeatherAnalyzer

Functional interface-driven analyzer for weather records.
 Uses streams and lambdas throughout.

```java
var records = WeatherAnalyzer.loadCSV("weatherdata.csv");
double avg = WeatherAnalyzer.averageTemp(records, 8);
```
 */
public interface WeatherAnalyzer {

    /**
     * ## loadCSV
     *
     * Parses a CSV file into a list of WeatherRecords.
     *
     * ```java
     * List<WeatherRecord> data = WeatherAnalyzer.loadCSV("weatherdata.csv");
     * ```
     *
     * @param path path to the CSV file
     * @return list of WeatherRecord
     */
    static List<WeatherRecord> loadCSV(String path) {
        List<WeatherRecord> records = new ArrayList<>();
        try (var reader = new BufferedReader(new FileReader(path))) {
            reader.lines()
                    .skip(1)
                    .filter(line -> !line.isBlank())
                    .forEach(line -> {
                        String[] parts = line.split(",");
                        if (parts.length >= 4) {
                            try {
                                LocalDate date = LocalDate.parse(parts[0].trim());
                                double temp = Double.parseDouble(parts[1].trim());
                                double humidity = Double.parseDouble(parts[2].trim());
                                double precip = Double.parseDouble(parts[3].trim());
                                records.add(new WeatherRecord(date, temp, humidity, precip));
                            } catch (Exception ignored) {}
                        }
                    });
        } catch (IOException e) {
            System.err.println("Could not read file: " + path);
        }
        return records;
    }

    /**
     * ## averageTemp
     *
     * Calculates average temperature for a given month (1–12).
     *
     * ```java
     * double avg = WeatherAnalyzer.averageTemp(records, 7); // July average
     * ```
     *
     * @param records list of WeatherRecord
     * @param month   month number 1–12
     * @return average temperature, or 0.0 if no data
     */
    static double averageTemp(List<WeatherRecord> records, int month) {
        OptionalDouble avg = records.stream()
                .filter(r -> r.date().getMonthValue() == month)
                .mapToDouble(WeatherRecord::temperature)
                .average();
        return avg.orElse(0.0);
    }

    /**
     * ## daysAboveThreshold
     *
     * Returns records where temperature exceeds the threshold.
     *
     * ```java
     * var hotDays = WeatherAnalyzer.daysAboveThreshold(records, 30.0);
     * ```
     *
     * @param records   list of WeatherRecord
     * @param threshold temperature threshold in Celsius
     * @return filtered list
     */
    static List<WeatherRecord> daysAboveThreshold(List<WeatherRecord> records, double threshold) {
        Predicate<WeatherRecord> aboveThreshold = r -> r.temperature() > threshold;
        return records.stream()
                .filter(aboveThreshold)
                .toList();
    }

    /**
     * ## rainyDayCount
     *
     * Counts days where precipitation > 0.
     *
     * ```java
     * long rainy = WeatherAnalyzer.rainyDayCount(records);
     * ```
     *
     * @param records list of WeatherRecord
     * @return count of rainy days
     */
    static long rainyDayCount(List<WeatherRecord> records) {
        return records.stream()
                .filter(WeatherRecord::isRainy)
                .count();
    }

    /**
     * ## formatSummary
     *
     * Uses a text block to generate a formatted summary report.
     *
     * @param records list of WeatherRecord
     * @param month   month to summarize
     * @return formatted string
     */
    static String formatSummary(List<WeatherRecord> records, int month) {
        double avg = averageTemp(records, month);
        long rainy = rainyDayCount(records);
        long hotDays = daysAboveThreshold(records, 30.0).size();

        return """
                ╔══════════════════════════════════╗
                   Weather Summary Report
                ╚══════════════════════════════════╝
                 Month Selected : %d
                 Avg Temp       : %.1f °C
                 Rainy Days     : %d
                 Days Above 30°C: %d
                """.formatted(month, avg, rainy, hotDays);
    }
}