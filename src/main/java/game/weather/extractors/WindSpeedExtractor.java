package game.weather.extractors;

import game.weather.WeatherDataExtractor;
import game.weather.WeatherReportBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts the wind speed (m/s) from the "wind.speed" field
 * of an OpenWeatherMap JSON response.
 */
public class WindSpeedExtractor implements WeatherDataExtractor<Double> {

    private static final Pattern PATTERN = Pattern.compile("\"speed\":([-\\d.]+)");
    private static final double DEFAULT_WIND = 5.0;

    /**
     * Parses the {@code "speed"} field from the {@code "wind"} object in the JSON response.
     * Returns {@value #DEFAULT_WIND} m/s if the response is {@code null} or unparseable.
     *
     * @param jsonResponse the raw JSON string from the OpenWeatherMap API
     * @return the wind speed in metres per second
     */
    @Override
    public Double extract(String jsonResponse) {
        if (jsonResponse == null) return DEFAULT_WIND;
        Matcher m = PATTERN.matcher(jsonResponse);
        if (m.find()) {
            try {
                return Double.parseDouble(m.group(1));
            } catch (NumberFormatException ignored) {}
        }
        return DEFAULT_WIND;
    }

    /**
     * Extracts the wind speed and forwards it to {@link WeatherReportBuilder#setWindSpeed}.
     *
     * @param builder the builder being assembled by the extractor chain
     * @param json    the raw JSON string from the OpenWeatherMap API
     */
    @Override
    public void populateReport(WeatherReportBuilder builder, String json) {
        builder.setWindSpeed(extract(json));
    }
}
