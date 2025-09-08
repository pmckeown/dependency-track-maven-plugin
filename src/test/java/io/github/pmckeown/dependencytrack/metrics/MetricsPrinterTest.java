package io.github.pmckeown.dependencytrack.metrics;

import static io.github.pmckeown.dependencytrack.Constants.COMPONENTS;
import static io.github.pmckeown.dependencytrack.Constants.CRITICAL;
import static io.github.pmckeown.dependencytrack.Constants.FINDINGS_AUDITED;
import static io.github.pmckeown.dependencytrack.Constants.FINDINGS_TOTAL;
import static io.github.pmckeown.dependencytrack.Constants.FINDINGS_UNAUDITED;
import static io.github.pmckeown.dependencytrack.Constants.FIRST_OCCURRENCE;
import static io.github.pmckeown.dependencytrack.Constants.HIGH;
import static io.github.pmckeown.dependencytrack.Constants.INHERITED_RISK_SCORE;
import static io.github.pmckeown.dependencytrack.Constants.LAST_OCCURRENCE;
import static io.github.pmckeown.dependencytrack.Constants.LOW;
import static io.github.pmckeown.dependencytrack.Constants.MEDIUM;
import static io.github.pmckeown.dependencytrack.Constants.SUPPRESSED;
import static io.github.pmckeown.dependencytrack.Constants.UNASSIGNED;
import static io.github.pmckeown.dependencytrack.Constants.VULNERABILITIES;
import static io.github.pmckeown.dependencytrack.Constants.VULNERABLE_COMPONENTS;
import static io.github.pmckeown.dependencytrack.metrics.MetricsBuilder.aMetrics;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import io.github.pmckeown.util.Logger;
import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
public class MetricsPrinterTest {

    static final String ISO_OFFSET_DATE_TIME_PATTERN =
            "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}([+|-][0-9]{2}:[0-9]{2}|Z)";

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {INHERITED_RISK_SCORE, "1"},
            {CRITICAL, "100"},
            {HIGH, "200"},
            {MEDIUM, "300"},
            {LOW, "400"},
            {UNASSIGNED, "500"},
            {VULNERABILITIES, "600"},
            {VULNERABLE_COMPONENTS, "700"},
            {COMPONENTS, "800"},
            {SUPPRESSED, "900"},
            {FINDINGS_TOTAL, "1000"},
            {FINDINGS_AUDITED, "1100"},
            {FINDINGS_UNAUDITED, "1200"},
            {FIRST_OCCURRENCE, ISO_OFFSET_DATE_TIME_PATTERN},
            {LAST_OCCURRENCE, ISO_OFFSET_DATE_TIME_PATTERN}
        });
    }

    @InjectMocks
    private MetricsPrinter metricsPrinter;

    @Mock
    private Logger logger;

    public String key;
    public String value;

    @MethodSource("data")
    @ParameterizedTest
    public void thatEachMetricIsPrintedCorrectly(String key, String value) {
        initMetricsPrinterTest(key, value);
        metricsPrinter.print(metrics());

        verify(logger, atLeastOnce()).info(matches("\\s*" + key + " \\| " + value));
    }

    private Metrics metrics() {
        return aMetrics()
                .withInheritedRiskScore(1)
                .withCritical(100)
                .withHigh(200)
                .withMedium(300)
                .withLow(400)
                .withUnassigned(500)
                .withVulnerabilities(600)
                .withVulnerableComponents(700)
                .withComponents(800)
                .withSuppressed(900)
                .withFindingsTotal(1000)
                .withFindingsAudited(1100)
                .withFindingsUnaudited(1200)
                .withFirstOccurrence(1562223415567L)
                .withLastOccurrence(1563445047035L)
                .build();
    }

    public void initMetricsPrinterTest(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
