package game.weather;

/**
 * Immutable snapshot of weather data returned by the OpenWeatherMap API.
 * Consumed by {@link WeatherEffect} implementations to decide whether to activate.
 */
public class WeatherReport {

    private final double temperature;
    private final int humidity;
    private final double windSpeed;
    private final String condition;

    public WeatherReport(double temperature, int humidity, double windSpeed, String condition) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.condition = condition;
    }

    /** Temperature in degrees Celsius. */
    public double getTemperature() { return temperature; }

    /** Relative humidity as a percentage (0–100). */
    public int getHumidity() { return humidity; }

    /** Wind speed in metres per second. */
    public double getWindSpeed() { return windSpeed; }

    /** OpenWeatherMap condition label, e.g. "Clear", "Rain", "Thunderstorm". */
    public String getCondition() { return condition; }

    @Override
    public String toString() {
        return String.format("%.1f°C | %d%% humidity | %.1f m/s wind | %s",
                temperature, humidity, windSpeed, condition);
    }
}
