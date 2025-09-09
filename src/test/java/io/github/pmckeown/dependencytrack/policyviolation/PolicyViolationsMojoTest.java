package io.github.pmckeown.dependencytrack.policyviolation;

import static org.mockito.Mockito.*;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.policyviolation.report.PolicyViolationsReportGenerator;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.util.Logger;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.WARN)
@SuppressWarnings("unused")
@ExtendWith(MockitoExtension.class)
class PolicyViolationsMojoTest {

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
    void thatReportIsAlwaysGeneratedEvenWhenNoFindingsArePresent() throws Exception {
        policyMojo.performAction();

        verify(policyViolationReportGenerator, times(1)).generate(null, new ArrayList<>());
    }

    @Test
    void thatReportIsNotGeneratedWhenSkipIsTrue() throws Exception {
        policyMojo.setSkip("true");

        policyMojo.execute();

        verifyNoInteractions(policyViolationReportGenerator);
    }
}
