package game.weather.extractors;

import game.weather.WeatherDataExtractor;
import game.weather.WeatherReportBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts the humidity percentage from the "main.humidity" field
 * of an OpenWeatherMap JSON response.
 */
public class HumidityExtractor implements WeatherDataExtractor<Integer> {

    private static final Pattern PATTERN = Pattern.compile("\"humidity\":(\\d+)");
    private static final int DEFAULT_HUMIDITY = 50;

    /**
     * Parses the {@code "humidity"} field from the JSON response.
     * Returns {@value #DEFAULT_HUMIDITY}% if the response is {@code null} or unparseable.
     *
     * @param jsonResponse the raw JSON string from the OpenWeatherMap API
     * @return the relative humidity as a percentage (0–100)
     */
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

    /**
     * Extracts the humidity value and forwards it to {@link WeatherReportBuilder#setHumidity}.
     *
     * @param builder the builder being assembled by the extractor chain
     * @param json    the raw JSON string from the OpenWeatherMap API
     */
    @Override
    public void populateReport(WeatherReportBuilder builder, String json) {
        builder.setHumidity(extract(json));
    }
}
