package com.example.demo;

import java.time.LocalDate;

/**
 * ## WeatherRecord
 *
 * Represents a single day's weather data as an immutable record.
 *
 * ```java
 * WeatherRecord r = new WeatherRecord(LocalDate.now(), 32.5, 65.0, 0.0);
 * System.out.println(r.category()); // "Hot"
 * ```
 *
 * @param date          the date of the reading
 * @param temperature   temperature in Celsius
 * @param humidity      humidity percentage
 * @param precipitation precipitation in mm
 */
public record WeatherRecord(LocalDate date, double temperature, double humidity, double precipitation) {

    /**
     * ## category
     *
     * Uses an enhanced switch to classify temperature.
     *
     * ```java
     * record.category(); // returns "Freezing", "Cold", "Cool", "Warm", or "Hot"
     * ```
     *
     * @return weather category string
     */
    public String category() {
        if (temperature < 0) return "Freezing";
        else if (temperature < 10) return "Cold";
        else if (temperature < 20) return "Cool";
        else if (temperature < 30) return "Warm";
        else return "Hot";
    }

    /**
     * ## isRainy
     *
     * @return true if precipitation is greater than 0
     */
    public boolean isRainy() {
        return precipitation > 0;
    }
}