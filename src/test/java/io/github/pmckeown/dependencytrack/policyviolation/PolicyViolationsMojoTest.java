package io.github.pmckeown.dependencytrack.policyviolation;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.policyviolation.report.PolicyViolationsReportGenerator;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.util.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

@SuppressWarnings("unused")
@RunWith(MockitoJUnitRunner.class)
public class PolicyViolationsMojoTest {

    @InjectMocks
    private PolicyViolationsMojo policyMojo;

    @Mock
    private ProjectAction projectAction;

    @Mock
    private PolicyViolationsAction policyAction;

    @Mock
    private PolicyViolationsPrinter policyViolationsPrinter;

    @Mock
    private PolicyViolationsAnalyser policyAnalyser;

    @Mock
    private CommonConfig commonConfig;

    @Mock
    private ModuleConfig moduleConfig;

    @Mock
    private PolicyViolationsReportGenerator policyViolationReportGenerator;

    @Mock
    private Logger logger;

    @Test
    public void thatReportIsAlwaysGeneratedEvenWhenNoFindingsArePresent() throws Exception {
        policyMojo.performAction();

        verify(policyViolationReportGenerator, times(1)).generate(
                null, new ArrayList<>());
    }

    @Test
    public void thatReportIsNotGeneratedWhenSkipIsTrue() throws Exception {
        policyMojo.setSkip("true");

        policyMojo.execute();

        verifyNoInteractions(policyViolationReportGenerator);
    }

}