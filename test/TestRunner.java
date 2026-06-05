import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.platform.engine.discovery.DiscoverySelectors;

import java.io.PrintWriter;

/**
 * Standalone test runner — compiles and runs without Maven or Gradle.
 */
public class TestRunner {
    public static void main(String[] args) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                    DiscoverySelectors.selectPackage("req1"),
                    DiscoverySelectors.selectPackage("req2"),
                    DiscoverySelectors.selectPackage("req3"),
                    DiscoverySelectors.selectPackage("req5")
                )
                .build();

        SummaryGeneratingListener listener = new SummaryGeneratingListener();

        Launcher launcher = LauncherFactory.create();
        launcher.discover(request);
        launcher.execute(request, listener);

        TestExecutionSummary summary = listener.getSummary();
        summary.printFailuresTo(new PrintWriter(System.out, true));

        long passed  = summary.getTestsSucceededCount();
        long failed  = summary.getTestsFailedCount();
        long skipped = summary.getTestsSkippedCount();
        long total   = summary.getTestsStartedCount();

        System.out.println("\n══════════════════════════════");
        System.out.printf("  Tests run : %d%n", total);
        System.out.printf("  Passed    : %d%n", passed);
        System.out.printf("  Failed    : %d%n", failed);
        System.out.printf("  Skipped   : %d%n", skipped);
        System.out.println("══════════════════════════════");

        if (failed > 0) {
            System.out.println("\nFAILURES:");
            for (TestExecutionSummary.Failure f : summary.getFailures()) {
                System.out.println("  ✗ " + f.getTestIdentifier().getDisplayName());
                System.out.println("    " + f.getException().getMessage());
            }
            System.exit(1);
        } else {
            System.out.println("\nAll tests passed!");
        }
    }
}
