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

    @Override
    public void populateReport(WeatherReportBuilder builder, String json) {
        builder.setWindSpeed(extract(json));
    }
}
