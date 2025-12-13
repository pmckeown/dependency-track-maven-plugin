package io.github.pmckeown.dependencytrack.finding;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.http.Fault.RANDOM_DATA_THEN_CLOSE;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT_LOOKUP;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_FINDING_PROJECT_UUID;
import static io.github.pmckeown.dependencytrack.TestUtils.asJson;
import static io.github.pmckeown.dependencytrack.finding.AnalysisBuilder.anAnalysis;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.finding.FindingBuilder.aFinding;
import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static io.github.pmckeown.dependencytrack.finding.Severity.LOW;
import static io.github.pmckeown.dependencytrack.finding.Severity.UNASSIGNED;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.aVulnerability;
import static org.junit.jupiter.api.Assertions.*;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojoTest;
import org.apache.maven.api.plugin.testing.Basedir;
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoParameter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FindingsMojoIntegrationTest extends AbstractDependencyTrackMojoTest {

    FindingsMojo findingsMojo;

    @BeforeEach
    @Basedir(TEST_PROJECT)
    @InjectMojo(goal = "findings")
    @MojoParameter(name = "projectName", value = "testName")
    @MojoParameter(name = "projectVersion", value = "99.99")
    void setUp(FindingsMojo mojo) {
        findingsMojo = mojo;
        configureMojo(findingsMojo);
    }

    @Test
    void thatFindingMojoCanRetrieveFindingsAndPrintThem() throws Exception {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID))
                .willReturn(aResponse()
                        .withBody(asJson(aListOfFindings()
                                .withFinding(aFinding()
                                        .withComponent(aComponent().withName("dodgy"))
                                        .withVulnerability(aVulnerability().withSeverity(LOW))
                                        .withAnalysis(anAnalysis()))
                                .build()))));

        findingsMojo.execute();

        verify(exactly(1), getRequestedFor(urlPathEqualTo(V1_PROJECT_LOOKUP)));
        verify(exactly(1), getRequestedFor(urlPathMatching(V1_FINDING_PROJECT_UUID)));
    }

    @Test
    void thatWhenNoFindingsAreFoundTheMojoDoesNotFail() {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID)).willReturn(ok()));

        assertDoesNotThrow(
                () -> {
                    findingsMojo.execute();
                    verify(exactly(1), getRequestedFor(urlPathEqualTo(V1_PROJECT_LOOKUP)));
                    verify(exactly(1), getRequestedFor(urlPathMatching(V1_FINDING_PROJECT_UUID)));
                },
                "No exception expected");
    }

    @Test
    void thatWhenExceptionOccursWhileGettingFindingsAndFailOnErrorIsTrueTheMojoErrors() {
        assertThrows(MojoExecutionException.class, () -> {
            stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                    .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
            stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID))
                    .willReturn(aResponse().withFault(RANDOM_DATA_THEN_CLOSE)));

            findingsMojo.setFailOnError(true);

            findingsMojo.execute();
            fail("Exception expected");
        });
    }

    @Test
    void thatBuildFailsWhenFindingsNumberBreachesDefinedThresholds() {
        assertThrows(MojoFailureException.class, () -> {
            stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                    .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
            stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID))
                    .willReturn(aResponse()
                            .withBody(asJson(aListOfFindings()
                                    .withFinding(aFinding()
                                            .withComponent(aComponent().withName("dodgy"))
                                            .withVulnerability(aVulnerability().withSeverity(LOW))
                                            .withAnalysis(anAnalysis()))
                                    .build()))));

            findingsMojo.setFindingThresholds(new FindingThresholds(0, 0, 0, 0, 0));

            findingsMojo.execute();
            fail("Exception expected");
        });
    }

    @Test
    void thatBuildDoesNotFailWhenOnlyUnassignedFindingExists() throws Exception {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID))
                .willReturn(aResponse()
                        .withBody(asJson(aListOfFindings()
                                .withFinding(aFinding()
                                        .withComponent(aComponent().withName("dodgy"))
                                        .withVulnerability(aVulnerability().withSeverity(UNASSIGNED))
                                        .withAnalysis(anAnalysis()))
                                .build()))));

        findingsMojo.setFindingThresholds(new FindingThresholds());

        assertDoesNotThrow(
                () -> {
                    findingsMojo.execute();
                },
                "Exception not expected");
    }

    @Test
    void thatFindingsIsSkippedWhenSkipIsTrue() throws Exception {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID))
                .willReturn(aResponse()
                        .withBody(asJson(aListOfFindings()
                                .withFinding(aFinding()
                                        .withComponent(aComponent().withName("dodgy"))
                                        .withVulnerability(aVulnerability().withSeverity(LOW))
                                        .withAnalysis(anAnalysis()))
                                .build()))));

        findingsMojo.setSkip("true");

        findingsMojo.execute();

        verify(exactly(0), getRequestedFor(urlPathEqualTo(V1_PROJECT_LOOKUP)));
        verify(exactly(0), getRequestedFor(urlPathMatching(V1_FINDING_PROJECT_UUID)));
    }
}
