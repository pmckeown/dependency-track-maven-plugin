package io.github.pmckeown.dependencytrack.finding;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.finding.report.FindingsReportGenerator;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.util.Logger;
import java.util.ArrayList;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindingsMojoTest {

    @InjectMocks
    private FindingsMojo findingsMojo;

    @Mock
    private ProjectAction projectAction;

    @Mock
    private FindingsAction findingsAction;

    @Mock
    private FindingsPrinter findingsPrinter;

    @Mock
    private FindingsAnalyser findingsAnalyser;

    @Mock
    private CommonConfig commonConfig;

    @Mock
    private ModuleConfig moduleConfig;

    @Mock
    private FindingsReportGenerator findingsReportGenerator;

    @Mock
    private Logger logger;

    @Test
    void thatReportIsAlwaysGeneratedEvenWhenNoFindingsArePresent() throws Exception {
        findingsMojo.performAction();

        verify(findingsReportGenerator, times(1)).generate(null, new ArrayList<>(), null, false);
    }

    @Test
    void thatReportIsNotGeneratedWhenSkipIsTrue() throws Exception {
        findingsMojo.setSkip("true");

        findingsMojo.execute();

        verifyNoInteractions(findingsReportGenerator);
    }

    @Test
    void thatThresholdLowOptionCanBeSetDirectly() throws Exception {
        doReturn(true).when(findingsAnalyser).doNumberOfFindingsBreachPolicy(any(), any());

        findingsMojo.setThresholdLow(1);

        try {
            findingsMojo.execute();
            fail("Exception expected here");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoFailureException.class)));
        }
    }

    @Test
    void thatThresholdCriticalOptionIsAddedToFindingThresholds() {
        findingsMojo.setThresholdCritical(1);
        findingsMojo.populateThresholdFromCliOptions();
        assertThat(findingsMojo.getFindingThresholds().getCritical(), is(equalTo(1)));
    }

    @Test
    void thatThresholdHighOptionIsAddedToFindingThresholds() {
        findingsMojo.setThresholdHigh(1);
        findingsMojo.populateThresholdFromCliOptions();
        assertThat(findingsMojo.getFindingThresholds().getHigh(), is(equalTo(1)));
    }

    @Test
    void thatThresholdMediumOptionIsAddedToFindingThresholds() {
        findingsMojo.setThresholdMedium(1);
        findingsMojo.populateThresholdFromCliOptions();
        assertThat(findingsMojo.getFindingThresholds().getMedium(), is(equalTo(1)));
    }

    @Test
    void thatThresholdLowOptionIsAddedToFindingThresholds() {
        findingsMojo.setThresholdLow(1);
        findingsMojo.populateThresholdFromCliOptions();
        assertThat(findingsMojo.getFindingThresholds().getLow(), is(equalTo(1)));
    }

    @Test
    void thatThresholdUnassignedOptionIsAddedToFindingThresholds() {
        findingsMojo.setThresholdUnassigned(1);
        findingsMojo.populateThresholdFromCliOptions();
        assertThat(findingsMojo.getFindingThresholds().getUnassigned(), is(equalTo(1)));
    }
}
