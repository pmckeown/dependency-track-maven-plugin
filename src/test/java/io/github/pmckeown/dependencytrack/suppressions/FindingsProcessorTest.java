package io.github.pmckeown.dependencytrack.suppressions;

import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.finding.Finding;
import io.github.pmckeown.dependencytrack.finding.FindingBuilder;
import io.github.pmckeown.dependencytrack.finding.FindingsAction;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static io.github.pmckeown.dependencytrack.project.ProjectBuilder.aProject;
import static io.github.pmckeown.dependencytrack.suppressions.VulnerabilitySuppressionBuilder.aDefaultVulnerabilitySuppression;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class FindingsProcessorTest {

    @InjectMocks
    private FindingsProcessor findingsProcessor;

    @Mock
    private FindingsAction findingsAction;

    @Mock
    private Logger logger;

    @Mock
    private ModuleConfig moduleConfig;

    private List<Finding> findings;

    private Project project;

    private final List<VulnerabilitySuppression> vulnerabilitySuppressions = new ArrayList<>();

    @Before
    public void setUp() {

        project = aProject().build();

        // index 0: "any" finding
        findings = aListOfFindings().withFinding(FindingBuilder.aDefaultFinding()).build();

        // index 1: suppressed finding
        findings.add(FindingBuilder.suppressedType1Finding().build());

        // index 2: NOT suppressed finding
        findings.add(FindingBuilder.notSuppressedType2Finding().build());

        // only type2 finding should be suppressed
        vulnerabilitySuppressions.add(VulnerabilitySuppressionBuilder.fixType2VulnerabilitySuppression().build());
    }

    @Test
    public void thatQueryingSuppressionsForFindingsIsCorrect() {
        assertThat(findingsProcessor.getVulnerabilitySuppression(findings.get(1), vulnerabilitySuppressions).isPresent(), is(false));
        assertThat(findingsProcessor.getVulnerabilitySuppression(findings.get(2), vulnerabilitySuppressions).isPresent(), is(true));
    }

    @Test
    public void thatStrictModeThrowsExceptionWhenSuppressionsDoNotFiTToFindings() throws DependencyTrackException {
        vulnerabilitySuppressions.add(aDefaultVulnerabilitySuppression().build());
        doReturn(findings).when(findingsAction).getFindings(project, true);
        doReturn(vulnerabilitySuppressions).when(moduleConfig).getVulnerabilitySuppressions();

        try {
            findingsProcessor.process(project, moduleConfig, true);
            fail("DependencyTrackException expected");
        } catch (Exception ex) {
            assertThat(ex, instanceOf(DependencyTrackException.class));
        }
    }

    @Test
    public void thatStrictModeThrowsNoExceptionWhenAllSuppressionsFiTToFindings() throws DependencyTrackException {
        doReturn(findings).when(findingsAction).getFindings(project, true);
        doReturn(vulnerabilitySuppressions).when(moduleConfig).getVulnerabilitySuppressions();

        try {
            findingsProcessor.process(project, moduleConfig, true);
        } catch (Exception ex) {
            fail("Exception not expected");
        }
    }

    @Test
    public void thatReturnedAnalysisListIsCorrect() throws Exception {
        doReturn(findings).when(findingsAction).getFindings(project, true);
        doReturn(vulnerabilitySuppressions).when(moduleConfig).getVulnerabilitySuppressions();

        List<Analysis> analysisList = findingsProcessor.process(project, moduleConfig, true);


        // should contain 2 entries
        assertThat(analysisList.size(), is(equalTo(2)));

        // suppression of first entry (matches to suppressedType1Finding) should be removed
        assertThat(analysisList.get(0).matchesFinding(findings.get(1)), is(equalTo(true)));
        assertThat(analysisList.get(0).getProjectUuid(), is(equalTo(project.getUuid())));
        assertThat(analysisList.get(0).getSuppressed(), is(equalTo(false)));

        // suppression of second entry (matches to suppressedType2Finding) should be added
        assertThat(analysisList.get(1).matchesFinding(findings.get(2)), is(equalTo(true)));
        assertThat(analysisList.get(1).getProjectUuid(), is(equalTo(project.getUuid())));
        assertThat(analysisList.get(1).getSuppressed(), is(equalTo(true)));
    }
}
