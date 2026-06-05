package game.weather;

/**
 * Extracts a single typed value from a raw OpenWeatherMap JSON response string
 * and writes it directly into a {@link WeatherReportBuilder}.
 *
 * Adding a new field to {@link WeatherReport} requires only a new implementation
 * of this interface — no changes to {@link WeatherSystem}.
 *
 * @param <T> the Java type of the extracted value
 */
public interface WeatherDataExtractor<T> {

    /**
     * Parse the given JSON string and return the target field's value.
     *
     * @param jsonResponse the raw JSON response from the weather API
     * @return the extracted value, or a sensible default if parsing fails
     */
    T extract(String jsonResponse);

    /**
     * Extracts the value from {@code json} and writes it to the appropriate
     * field on {@code builder}. Implementations know their own concrete type,
     * so no {@code instanceof} check is needed by the caller.
     *
     * @param builder the report builder to populate
     * @param json    the raw JSON response from the weather API
     */
    void populateReport(WeatherReportBuilder builder, String json);
}
