package game.weather.extractors;

import game.weather.WeatherDataExtractor;
import game.weather.WeatherReportBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts the temperature value (°C) from the "main.temp" field
 * of an OpenWeatherMap JSON response.
 */
public class TemperatureExtractor implements WeatherDataExtractor<Double> {

    private static final Pattern PATTERN = Pattern.compile("\"temp\":([-\\d.]+)");
    private static final double DEFAULT_TEMP = 20.0;

    /**
     * Parses the {@code "temp"} field from the JSON response.
     * Returns {@value #DEFAULT_TEMP}°C if the response is {@code null} or unparseable.
     *
     * @param jsonResponse the raw JSON string from the OpenWeatherMap API
     * @return the temperature in degrees Celsius
     */
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

    /**
     * Extracts the temperature and forwards it to {@link WeatherReportBuilder#setTemperature}.
     *
     * @param builder the builder being assembled by the extractor chain
     * @param json    the raw JSON string from the OpenWeatherMap API
     */
    @Override
    public void populateReport(WeatherReportBuilder builder, String json) {
        builder.setTemperature(extract(json));
    }
}
