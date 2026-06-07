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

    public void setTemperature(double temperature) { this.temperature = temperature; }
    public void setHumidity(int humidity)          { this.humidity    = humidity;    }
    public void setWindSpeed(double windSpeed)     { this.windSpeed   = windSpeed;   }
    public void setCondition(String condition)     { this.condition   = condition;   }

    public WeatherReport build() {
        return new WeatherReport(temperature, humidity, windSpeed, condition);
    }
}
