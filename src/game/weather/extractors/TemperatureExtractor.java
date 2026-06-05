package game.weather.extractors;

import game.weather.WeatherDataExtractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts the temperature value (°C) from the "main.temp" field
 * of an OpenWeatherMap JSON response.
 */
public class TemperatureExtractor implements WeatherDataExtractor<Double> {

    private static final Pattern PATTERN = Pattern.compile("\"temp\":([-\\d.]+)");
    private static final double DEFAULT_TEMP = 20.0;

    @Override
    public Double extract(String jsonResponse) {
        if (jsonResponse == null) return DEFAULT_TEMP;
        Matcher m = PATTERN.matcher(jsonResponse);
        if (m.find()) {
            try {
                return Double.parseDouble(m.group(1));
            } catch (NumberFormatException ignored) {}
        }
        return DEFAULT_TEMP;
    }

    @Override
    public String getFieldName() {
        return "temperature";
    }
}
