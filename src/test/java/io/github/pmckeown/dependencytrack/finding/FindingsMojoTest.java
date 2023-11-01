package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.finding.report.FindingsReportGenerator;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@SuppressWarnings("unused")
@RunWith(MockitoJUnitRunner.class)
public class FindingsMojoTest {

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
    private FindingsReportGenerator findingsReportGenerator;

    @Mock
    private Logger logger;

    @Test
    public void thatReportIsAlwaysGeneratedEvenWhenNoFindingsArePresent() throws Exception {
        findingsMojo.performAction();

        verify(findingsReportGenerator, times(1)).generate(
                null, new ArrayList<>(), null, false);
    }

    @Test
    public void thatReportIsNotGeneratedWhenSkipIsTrue() throws Exception {
        findingsMojo.setSkip("true");

        findingsMojo.execute();

        verifyNoInteractions(findingsReportGenerator);
    }

    @Test
    public void thatThresholdLowOptionCanBeSetDirectly() throws Exception {
        doReturn(true).when(findingsAnalyser).doNumberOfFindingsBreachPolicy(
                any(List.class), any(FindingThresholds.class));

        findingsMojo.setThresholdLow(1);

        try {
            findingsMojo.execute();
            fail("Exception expected here");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoFailureException.class)));
        }
    }

    @Test
    public void thatThresholdCriticalOptionIsAddedToFindingThresholds() {
        findingsMojo.setThresholdCritical(1);
        findingsMojo.populateThresholdFromCliOptions();
        assertThat(findingsMojo.getFindingThresholds().getCritical(), is(equalTo(1)));
    }

    @Test
    public void thatThresholdHighOptionIsAddedToFindingThresholds() {
        findingsMojo.setThresholdHigh(1);
        findingsMojo.populateThresholdFromCliOptions();
        assertThat(findingsMojo.getFindingThresholds().getHigh(), is(equalTo(1)));
    }

    @Test
    public void thatThresholdMediumOptionIsAddedToFindingThresholds() {
        findingsMojo.setThresholdMedium(1);
        findingsMojo.populateThresholdFromCliOptions();
        assertThat(findingsMojo.getFindingThresholds().getMedium(), is(equalTo(1)));
    }

    @Test
    public void thatThresholdLowOptionIsAddedToFindingThresholds() {
        findingsMojo.setThresholdLow(1);
        findingsMojo.populateThresholdFromCliOptions();
        assertThat(findingsMojo.getFindingThresholds().getLow(), is(equalTo(1)));
    }

    @Test
    public void thatThresholdUnassignedOptionIsAddedToFindingThresholds() {
        findingsMojo.setThresholdUnassigned(1);
        findingsMojo.populateThresholdFromCliOptions();
        assertThat(findingsMojo.getFindingThresholds().getUnassigned(), is(equalTo(1)));
    }

}