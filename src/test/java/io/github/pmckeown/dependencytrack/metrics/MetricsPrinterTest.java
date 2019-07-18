package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.util.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static io.github.pmckeown.dependencytrack.builders.MetricsBuilder.aMetrics;

@RunWith(MockitoJUnitRunner.class)
public class MetricsPrinterTest {

    @InjectMocks
    private MetricsPrinter metricsPrinter;

    @Mock
    private Logger logger;

    @Test
    public void thatMetricsAreAllPrinted() {
        Metrics metrics = aMetrics()
                .withInheritedRiskScore(0)
                .withCritical(100)
                .withHigh(200)
                .withMedium(300)
                .withLow(400)
                .withUnassigned(500)
                .withVulnerabilities(600)
                .withComponents(700)
                .withFindingsTotal(800)
                .withFindingsAudited(900)
                .withFirstOccurrence(1562223415567L)
                .withLastOccurrence(1563445047035L)
                .build();
        metricsPrinter.print(metrics);

//        verify(logger, Mockito.same()).info("| Inherited Risk Score | 100 |");
    }
}
