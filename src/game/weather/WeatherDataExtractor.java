package game.weather;

/**
 * Extracts a single typed value from a raw OpenWeatherMap JSON response string.
 *
 * Implementations parse one specific field (e.g. temperature, humidity) and
 * return a default if the field cannot be found.
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
     * Returns the logical field name this extractor produces.
     * Used by {@link WeatherSystem} to route the value into the correct
     * {@link WeatherReport} field.
     *
     * Valid values: "temperature", "humidity", "windSpeed", "condition"
     *
     * @return field name string
     */
    String getFieldName();
}
