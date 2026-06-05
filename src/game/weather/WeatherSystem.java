package game.weather;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Orchestrates the real-world weather pipeline for the game.
 *
 * <p>This is a higher-level class that depends <em>only</em> on the
 * {@link WeatherDataExtractor} and {@link WeatherEffect} abstractions —
 * never on any concrete implementation — satisfying the Dependency Inversion
 * Principle and REQ5 Rule 3.
 *
 * <p><b>Dynamic URL:</b> The OpenWeatherMap query uses real lat/lon coordinates
 * derived from the active player's x-position on the map. The 60-column map is
 * split into three equal zones (0–19, 20–39, 40–59), each mapped to a real-world
 * city. As the player moves across the facility, the queried city changes:
 * <ul>
 *   <li>x ∈ [0, 20) → Melbourne, Australia (−37.81°, 144.96°)</li>
 *   <li>x ∈ [20, 40) → London, United Kingdom (51.51°, −0.13°)</li>
 *   <li>x ∈ [40, 60) → Tokyo, Japan (35.68°, 139.69°)</li>
 * </ul>
 *
 * <p><b>API key security:</b> The key is never hard-coded. It is read first from
 * the {@code OPENWEATHER_API_KEY} environment variable, then from a {@code .env}
 * file in the working directory. If neither is present, the system falls back to
 * default weather values with a console warning.
 */
public class WeatherSystem {

    private static final String API_BASE =
            "https://api.openweathermap.org/data/2.5/weather";

    /** Width of each map zone (map is 60 columns wide, three equal zones). */
    private static final int ZONE_WIDTH = 20;

    /**
     * Real-world city coordinates for each map zone.
     * Index 0 → Melbourne, 1 → London, 2 → Tokyo.
     */
    private static final double[][] CITY_COORDS = {
        {-37.81, 144.96},   // Melbourne, Australia
        { 51.51,  -0.13},   // London, United Kingdom
        { 35.68, 139.69}    // Tokyo, Japan
    };

    private static final String[] CITY_NAMES = {"Melbourne", "London", "Tokyo"};

    private final List<WeatherDataExtractor<?>> extractors;
    private final List<WeatherEffect> effects;
    private final String apiKey;

    /**
     * Constructs a WeatherSystem wired to the given extractors and effects.
     *
     * @param extractors list of {@link WeatherDataExtractor} implementations to
     *                   parse each field from the JSON response
     * @param effects    list of {@link WeatherEffect} implementations to apply
     *                   when their thresholds are met
     */
    public WeatherSystem(List<WeatherDataExtractor<?>> extractors,
                         List<WeatherEffect> effects) {
        this.extractors = extractors;
        this.effects = effects;
        this.apiKey = loadApiKey();
    }

    // ── API key loading ────────────────────────────────────────────────────────

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String loadApiKey() {
        // Priority 1: environment variable
        String key = System.getenv("OPENWEATHER_API_KEY");
        if (!isBlank(key)) {
            return key;
        }

        // Priority 2: .env file in working directory
        try (BufferedReader reader = new BufferedReader(new FileReader(".env"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("OPENWEATHER_API_KEY=")) {
                    String value = line.substring("OPENWEATHER_API_KEY=".length()).trim();
                    if (!isBlank(value)) return value;
                }
            }
        } catch (IOException ignored) {
            // .env file absent — not an error
        }

        return null;
    }

    // ── URL construction (game-state driven) ───────────────────────────────────

    private int zoneOf(int playerX) {
        return Math.min(playerX / ZONE_WIDTH, CITY_COORDS.length - 1);
    }

    /**
     * Builds the dynamic API URL based on the player's x-coordinate.
     * The x-coordinate selects a real-world city, so the URL changes as the
     * player moves across different zones of the facility map.
     *
     * <p>Example for playerX = 35 (London zone):
     * {@code https://api.openweathermap.org/data/2.5/weather?lat=51.51&lon=-0.13&units=metric&appid=***}
     *
     * @param playerX the player's x-coordinate on the current map
     * @return the full API URL string
     */
    private String buildUrl(int playerX) {
        int zone = zoneOf(playerX);
        double lat = CITY_COORDS[zone][0];
        double lon = CITY_COORDS[zone][1];
        return String.format("%s?lat=%.2f&lon=%.2f&units=metric&appid=%s",
                API_BASE, lat, lon, apiKey);
    }

    // ── HTTP fetch ─────────────────────────────────────────────────────────────

    private String fetchJson(int playerX) {
        if (isBlank(apiKey)) {
            System.out.println(
                "[WeatherSystem] No API key found — using default weather values. "
                + "Set OPENWEATHER_API_KEY env var or add it to .env");
            return null;
        }

        String url = buildUrl(playerX);
        String maskedUrl = url.replace(apiKey, "***");
        System.out.println("[WeatherSystem] Querying weather for "
                + CITY_NAMES[zoneOf(playerX)]
                + " (player x=" + playerX + ") → " + maskedUrl);

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int status = conn.getResponseCode();
            if (status == 200) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                }
                return sb.toString();
            }
            System.out.println("[WeatherSystem] API returned HTTP "
                    + status + " — using defaults.");
        } catch (Exception e) {
            System.out.println("[WeatherSystem] API fetch failed: "
                    + e.getMessage() + " — using defaults.");
        } finally {
            if (conn != null) conn.disconnect();
        }
        return null;
    }

    // ── Report building ────────────────────────────────────────────────────────

    /**
     * Runs all registered extractors over the JSON string and assembles a
     * {@link WeatherReport}. If {@code json} is null, every extractor returns
     * its built-in default value, producing a valid "calm conditions" report.
     */
    private WeatherReport buildReport(String json) {
        double temperature = 20.0;
        int    humidity    = 50;
        double windSpeed   = 5.0;
        String condition   = "Clear";

        for (WeatherDataExtractor<?> extractor : extractors) {
            Object value = extractor.extract(json);
            String field = extractor.getFieldName();

            if ("temperature".equals(field) && value instanceof Double) {
                temperature = (Double) value;
            } else if ("humidity".equals(field) && value instanceof Integer) {
                humidity = (Integer) value;
            } else if ("windSpeed".equals(field) && value instanceof Double) {
                windSpeed = (Double) value;
            } else if ("condition".equals(field) && value instanceof String) {
                condition = (String) value;
            }
        }
        return new WeatherReport(temperature, humidity, windSpeed, condition);
    }

    // ── Public API ─────────────────────────────────────────────────────────────

    /**
     * Fetches current weather data for the zone matching {@code playerLocation.x()},
     * then evaluates every registered {@link WeatherEffect} and applies those
     * whose thresholds are met.
     *
     * <p>This method depends solely on the {@link WeatherDataExtractor} and
     * {@link WeatherEffect} abstractions — demonstrating the Dependency
     * Inversion Principle at the point of use.
     *
     * @param map            the game map to modify
     * @param playerLocation the current location of the active player, used to
     *                       (a) derive the dynamic API query parameter and
     *                       (b) centre any area-of-effect changes
     * @return a human-readable log of what happened this weather cycle
     */
    public String fetchAndApply(GameMap map, Location playerLocation) {
        String json = fetchJson(playerLocation.x());
        WeatherReport report = buildReport(json);

        System.out.println("[WeatherSystem] Conditions: " + report);

        StringBuilder log = new StringBuilder();
        for (WeatherEffect effect : effects) {
            if (effect.shouldActivate(report)) {
                effect.apply(map, playerLocation);
                log.append(effect.getEffectName()).append(". ");
            }
        }

        return log.length() == 0
                ? "The facility weather sensors report calm conditions."
                : "Weather alert: " + log;
    }
}
