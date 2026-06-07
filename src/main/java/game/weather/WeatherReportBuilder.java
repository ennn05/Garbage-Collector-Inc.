package game.weather;

/**
 * Mutable builder for {@link WeatherReport}.
 *
 * Each {@link WeatherDataExtractor} implementation calls the appropriate setter
 * so the report is assembled without requiring runtime type checks ({@code instanceof})
 * in the caller.
 */
public class WeatherReportBuilder {

    private double temperature = 20.0;
    private int    humidity    = 50;
    private double windSpeed   = 5.0;
    private String condition   = "Clear";

    /** Sets the temperature field (degrees Celsius). @param temperature the value to set */
    public void setTemperature(double temperature) { this.temperature = temperature; }

    /** Sets the relative humidity field (0–100%). @param humidity the value to set */
    public void setHumidity(int humidity)          { this.humidity    = humidity;    }

    /** Sets the wind speed field (metres per second). @param windSpeed the value to set */
    public void setWindSpeed(double windSpeed)     { this.windSpeed   = windSpeed;   }

    /** Sets the condition label (e.g. {@code "Clear"}, {@code "Rain"}). @param condition the value to set */
    public void setCondition(String condition)     { this.condition   = condition;   }

    /**
     * Constructs and returns an immutable {@link WeatherReport} from the values
     * accumulated by the extractor chain.
     *
     * @return a new {@link WeatherReport}
     */
    public WeatherReport build() {
        return new WeatherReport(temperature, humidity, windSpeed, condition);
    }
}
