package game.weather;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.weather.extractors.HumidityExtractor;
import game.weather.extractors.TemperatureExtractor;
import game.weather.extractors.WeatherConditionExtractor;
import game.weather.extractors.WindSpeedExtractor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Orchestrates the real-world weather pipeline for the game.
 *
 * <p>This is a higher-level class that depends <em>only</em> on the
 * {@link WeatherZone} and {@link WeatherEffect} abstractions —
 * never on any concrete implementation — satisfying the Dependency Inversion
 * Principle.
 *
 * <p><b>Two-phase weather cycle (every {@value #FETCH_INTERVAL} turns):</b>
 * <ol>
 *   <li><b>Passive zone effect</b> — the active {@link WeatherZone} (determined by the
 *       player's x-coordinate) applies its unique, always-on terrain transformation.</li>
 *   <li><b>Threshold effects</b> — live weather data is fetched from OpenWeatherMap,
 *       parsed into a {@link WeatherReport}, and each registered {@link WeatherEffect}
 *       is evaluated; those whose thresholds are met are applied.</li>
 * </ol>
 *
 * <p><b>Dynamic URL:</b> The active {@link WeatherZone} constructs the API query URL
 * from its own city coordinates. As the player moves across the facility, the active
 * zone changes, changing the URL — the query is never static.
 *
 * <p><b>API key security:</b> The key is read first from the
 * {@code OPENWEATHER_API_KEY} environment variable, then from a {@code .env} file in
 * the working directory. If neither is present, the system falls back to safe defaults.
 */
public class WeatherSystem {

    /** JSON parsing helpers — internal implementation detail, not injected. */
    private static final List<WeatherDataExtractor<?>> EXTRACTORS =
            Arrays.<WeatherDataExtractor<?>>asList(
                    new TemperatureExtractor(),
                    new HumidityExtractor(),
                    new WindSpeedExtractor(),
                    new WeatherConditionExtractor()
            );

    private final List<WeatherZone> zones;
    private final List<WeatherEffect> effects;
    private final String apiKey;

    /**
     * Constructs a WeatherSystem wired to the given zones and effects.
     *
     * <p>Both parameters are typed as {@code List} of the abstract types —
     * no concrete zone or effect class is mentioned here.
     *
     * @param zones   list of {@link WeatherZone} implementations that define
     *                the geographic regions and their passive terrain effects
     * @param effects list of {@link WeatherEffect} implementations that define
     *                the threshold-triggered reactions to live weather data
     */
    public WeatherSystem(List<WeatherZone> zones, List<WeatherEffect> effects) {
        this.zones   = zones;
        this.effects = effects;
        this.apiKey  = loadApiKey();
    }

    // ── API key loading ────────────────────────────────────────────────────────

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String loadApiKey() {
        String key = System.getenv("OPENWEATHER_API_KEY");
        if (!isBlank(key)) return key;

        try (BufferedReader reader = new BufferedReader(new FileReader(".env"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("OPENWEATHER_API_KEY=")) {
                    String value = line.substring("OPENWEATHER_API_KEY=".length()).trim();
                    if (!isBlank(value)) return value;
                }
            }
        } catch (IOException ignored) {}

        return null;
    }

    // ── Active zone resolution ─────────────────────────────────────────────────

    /**
     * Finds the {@link WeatherZone} whose column range contains the player's
     * x-coordinate. Falls back to the first zone if none matches.
     */
    private WeatherZone findActiveZone(int playerX) {
        for (WeatherZone zone : zones) {
            if (zone.containsPlayerX(playerX)) {
                return zone;
            }
        }
        return zones.get(0);
    }

    // ── HTTP fetch ─────────────────────────────────────────────────────────────

    private String fetchJson(WeatherZone activeZone) {
        if (isBlank(apiKey)) {
            System.out.println(
                "[WeatherSystem] No API key — using defaults. "
                + "Set OPENWEATHER_API_KEY env var or add it to .env");
            return null;
        }

        String url       = activeZone.buildApiQuery(apiKey);
        String maskedUrl = url.replace(apiKey, "***");
        System.out.println("[WeatherSystem] Zone: " + activeZone.getZoneName()
                + " → " + maskedUrl);

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
                    while ((line = br.readLine()) != null) sb.append(line);
                }
                return sb.toString();
            }
            System.out.println("[WeatherSystem] API returned HTTP "
                    + status + " — using defaults.");
        } catch (Exception e) {
            System.out.println("[WeatherSystem] Fetch failed: "
                    + e.getMessage() + " — using defaults.");
        } finally {
            if (conn != null) conn.disconnect();
        }
        return null;
    }

    // ── Report building ────────────────────────────────────────────────────────

    private WeatherReport buildReport(String json) {
        WeatherReportBuilder builder = new WeatherReportBuilder();
        for (WeatherDataExtractor<?> extractor : EXTRACTORS) {
            extractor.populateReport(builder, json);
        }
        return builder.build();
    }

    // ── Public API ─────────────────────────────────────────────────────────────

    /**
     * Runs one full weather cycle:
     * <ol>
     *   <li>Resolves the active {@link WeatherZone} from the player's x-coordinate.</li>
     *   <li>Calls {@link WeatherZone#applyPassiveEffect} — the always-on climate effect.</li>
     *   <li>Fetches live JSON and builds a {@link WeatherReport}.</li>
     *   <li>Applies every {@link WeatherEffect} whose threshold is met.</li>
     * </ol>
     *
     * <p>Depends only on {@link WeatherZone} and {@link WeatherEffect} — demonstrating
     * the Dependency Inversion Principle at the point of use.
     *
     * @param map            the game map to modify
     * @param playerLocation the active player's location
     * @return a human-readable log of what happened this cycle
     */
    public String fetchAndApply(GameMap map, Location playerLocation) {
        WeatherZone activeZone = findActiveZone(playerLocation.x());

        // Phase 1 — passive zone effect (always runs)
        System.out.println("[WeatherSystem] Applying passive effect for " + activeZone.getZoneName());
        activeZone.applyPassiveEffect(map, playerLocation);

        // Phase 2 — threshold-triggered effects (driven by live data)
        String json         = fetchJson(activeZone);
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
                ? "The facility weather sensors report calm conditions in the " + activeZone.getZoneName() + "."
                : "Weather alert [" + activeZone.getZoneName() + "]: " + log;
    }
}
