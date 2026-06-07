package game.weather.extractors;

import game.weather.WeatherDataExtractor;
import game.weather.WeatherReportBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts the weather condition label (e.g. "Clear", "Rain", "Thunderstorm")
 * from the first element of the "weather" array in an OpenWeatherMap JSON response.
 *
 * The pattern targets the "main" key inside the "weather" array to distinguish
 * it from the top-level "main" object.
 */
public class WeatherConditionExtractor implements WeatherDataExtractor<String> {

    private static final Pattern PATTERN =
            Pattern.compile("\"weather\":\\[\\{.*?\"main\":\"([^\"]+)\"",
                    Pattern.DOTALL);
    private static final String DEFAULT_CONDITION = "Clear";

    /**
     * Parses the {@code "main"} key inside the first {@code "weather"} array element.
     * Returns {@value #DEFAULT_CONDITION} if the response is {@code null} or the pattern
     * does not match.
     *
     * @param jsonResponse the raw JSON string from the OpenWeatherMap API
     * @return the condition label (e.g. {@code "Clear"}, {@code "Rain"})
     */
    @Override
    public String extract(String jsonResponse) {
        if (jsonResponse == null) return DEFAULT_CONDITION;
        Matcher m = PATTERN.matcher(jsonResponse);
        if (m.find()) {
            return m.group(1);
        }
        return DEFAULT_CONDITION;
    }

    /**
     * Extracts the condition label and forwards it to {@link WeatherReportBuilder#setCondition}.
     *
     * @param builder the builder being assembled by the extractor chain
     * @param json    the raw JSON string from the OpenWeatherMap API
     */
    @Override
    public void populateReport(WeatherReportBuilder builder, String json) {
        builder.setCondition(extract(json));
    }
}
