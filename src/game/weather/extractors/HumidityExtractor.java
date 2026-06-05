package game.weather.extractors;

import game.weather.WeatherDataExtractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts the humidity percentage from the "main.humidity" field
 * of an OpenWeatherMap JSON response.
 */
public class HumidityExtractor implements WeatherDataExtractor<Integer> {

    private static final Pattern PATTERN = Pattern.compile("\"humidity\":(\\d+)");
    private static final int DEFAULT_HUMIDITY = 50;

    @Override
    public Integer extract(String jsonResponse) {
        if (jsonResponse == null) return DEFAULT_HUMIDITY;
        Matcher m = PATTERN.matcher(jsonResponse);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (NumberFormatException ignored) {}
        }
        return DEFAULT_HUMIDITY;
    }

    @Override
    public String getFieldName() {
        return "humidity";
    }
}
