package req5;

import game.weather.WeatherReport;
import game.weather.WeatherReportBuilder;
import game.weather.extractors.HumidityExtractor;
import game.weather.extractors.TemperatureExtractor;
import game.weather.extractors.WeatherConditionExtractor;
import game.weather.extractors.WindSpeedExtractor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for WeatherReportBuilder and the four WeatherDataExtractor implementations.
 * No API calls are made — extractors are tested with inline JSON strings.
 */
class WeatherExtractorTest {

    // ── WeatherReportBuilder ───────────────────────────────────────────────────

    @Test
    void builderHasCorrectDefaults() {
        WeatherReport report = new WeatherReportBuilder().build();

        assertEquals(20.0,    report.getTemperature(), 0.001);
        assertEquals(50,      report.getHumidity());
        assertEquals(5.0,     report.getWindSpeed(),   0.001);
        assertEquals("Clear", report.getCondition());
    }

    @Test
    void builderAppliesAllSetters() {
        WeatherReportBuilder builder = new WeatherReportBuilder();
        builder.setTemperature(35.0);
        builder.setHumidity(80);
        builder.setWindSpeed(12.0);
        builder.setCondition("Thunderstorm");
        WeatherReport report = builder.build();

        assertEquals(35.0,           report.getTemperature(), 0.001);
        assertEquals(80,             report.getHumidity());
        assertEquals(12.0,           report.getWindSpeed(),   0.001);
        assertEquals("Thunderstorm", report.getCondition());
    }

    // ── TemperatureExtractor ───────────────────────────────────────────────────

    @Test
    void temperatureExtractorParsesValueFromJson() {
        String json = "{\"main\":{\"temp\":28.5,\"humidity\":60}}";
        assertEquals(28.5, new TemperatureExtractor().extract(json), 0.001);
    }

    @Test
    void temperatureExtractorReturnsDefaultWhenJsonIsNull() {
        assertEquals(20.0, new TemperatureExtractor().extract(null), 0.001);
    }

    @Test
    void temperatureExtractorPopulatesBuilder() {
        String json = "{\"main\":{\"temp\":33.0}}";
        WeatherReportBuilder builder = new WeatherReportBuilder();
        new TemperatureExtractor().populateReport(builder, json);
        assertEquals(33.0, builder.build().getTemperature(), 0.001);
    }

    // ── HumidityExtractor ──────────────────────────────────────────────────────

    @Test
    void humidityExtractorParsesValueFromJson() {
        String json = "{\"main\":{\"temp\":20.0,\"humidity\":72}}";
        assertEquals(72, new HumidityExtractor().extract(json));
    }

    @Test
    void humidityExtractorReturnsDefaultWhenJsonIsNull() {
        assertEquals(50, new HumidityExtractor().extract(null));
    }

    @Test
    void humidityExtractorPopulatesBuilder() {
        String json = "{\"main\":{\"humidity\":85}}";
        WeatherReportBuilder builder = new WeatherReportBuilder();
        new HumidityExtractor().populateReport(builder, json);
        assertEquals(85, builder.build().getHumidity());
    }

    // ── WindSpeedExtractor ─────────────────────────────────────────────────────

    @Test
    void windSpeedExtractorParsesValueFromJson() {
        String json = "{\"wind\":{\"speed\":9.2}}";
        assertEquals(9.2, new WindSpeedExtractor().extract(json), 0.001);
    }

    @Test
    void windSpeedExtractorReturnsDefaultWhenJsonIsNull() {
        assertEquals(5.0, new WindSpeedExtractor().extract(null), 0.001);
    }

    @Test
    void windSpeedExtractorPopulatesBuilder() {
        String json = "{\"wind\":{\"speed\":11.0}}";
        WeatherReportBuilder builder = new WeatherReportBuilder();
        new WindSpeedExtractor().populateReport(builder, json);
        assertEquals(11.0, builder.build().getWindSpeed(), 0.001);
    }

    // ── WeatherConditionExtractor ──────────────────────────────────────────────

    @Test
    void conditionExtractorParsesValueFromJson() {
        String json = "{\"weather\":[{\"id\":800,\"main\":\"Rain\",\"description\":\"light rain\"}]}";
        assertEquals("Rain", new WeatherConditionExtractor().extract(json));
    }

    @Test
    void conditionExtractorReturnsDefaultWhenJsonIsNull() {
        assertEquals("Clear", new WeatherConditionExtractor().extract(null));
    }

    @Test
    void conditionExtractorPopulatesBuilder() {
        String json = "{\"weather\":[{\"main\":\"Snow\"}]}";
        WeatherReportBuilder builder = new WeatherReportBuilder();
        new WeatherConditionExtractor().populateReport(builder, json);
        assertEquals("Snow", builder.build().getCondition());
    }

    // ── Full pipeline ──────────────────────────────────────────────────────────

    @Test
    void allExtractorsTogetherBuildCorrectReport() {
        String json = "{\"main\":{\"temp\":33.0,\"humidity\":80},"
                    + "\"wind\":{\"speed\":10.5},"
                    + "\"weather\":[{\"main\":\"Thunderstorm\"}]}";
        WeatherReportBuilder builder = new WeatherReportBuilder();
        new TemperatureExtractor().populateReport(builder, json);
        new HumidityExtractor().populateReport(builder, json);
        new WindSpeedExtractor().populateReport(builder, json);
        new WeatherConditionExtractor().populateReport(builder, json);
        WeatherReport report = builder.build();

        assertEquals(33.0,           report.getTemperature(), 0.001);
        assertEquals(80,             report.getHumidity());
        assertEquals(10.5,           report.getWindSpeed(),   0.001);
        assertEquals("Thunderstorm", report.getCondition());
    }
}
