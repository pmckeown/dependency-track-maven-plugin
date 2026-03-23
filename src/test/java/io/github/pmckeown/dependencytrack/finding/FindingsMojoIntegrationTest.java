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
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.Test;

class FindingsMojoIntegrationTest extends AbstractDependencyTrackMojoTest {

    @Test
    @InjectMojo(goal = "findings")
    void thatFindingMojoCanRetrieveFindingsAndPrintThem(FindingsMojo mojo) throws Exception {
        configureMojo(mojo);
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

        mojo.execute();

        verify(exactly(1), getRequestedFor(urlPathEqualTo(V1_PROJECT_LOOKUP)));
        verify(exactly(1), getRequestedFor(urlPathMatching(V1_FINDING_PROJECT_UUID)));
    }

    @Test
    @InjectMojo(goal = "findings")
    void thatWhenNoFindingsAreFoundTheMojoDoesNotFail(FindingsMojo mojo) {
        configureMojo(mojo);
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID)).willReturn(ok()));

        assertDoesNotThrow(
                () -> {
                    mojo.execute();
                    verify(exactly(1), getRequestedFor(urlPathEqualTo(V1_PROJECT_LOOKUP)));
                    verify(exactly(1), getRequestedFor(urlPathMatching(V1_FINDING_PROJECT_UUID)));
                },
                "No exception expected");
    }

    @Test
    @InjectMojo(goal = "findings")
    void thatWhenExceptionOccursWhileGettingFindingsAndFailOnErrorIsTrueTheMojoErrors(FindingsMojo mojo) {
        configureMojo(mojo);
        assertThrows(MojoExecutionException.class, () -> {
            stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                    .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
            stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID))
                    .willReturn(aResponse().withFault(RANDOM_DATA_THEN_CLOSE)));

            mojo.setFailOnError(true);

            mojo.execute();
            fail("Exception expected");
        });
    }

    @Test
    @InjectMojo(goal = "findings")
    void thatBuildFailsWhenFindingsNumberBreachesDefinedThresholds(FindingsMojo mojo) {
        configureMojo(mojo);
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

            mojo.setFindingThresholds(new FindingThresholds(0, 0, 0, 0, 0));

            mojo.execute();
            fail("Exception expected");
        });
    }

    @Test
    @InjectMojo(goal = "findings")
    void thatBuildDoesNotFailWhenOnlyUnassignedFindingExists(FindingsMojo mojo) throws Exception {
        configureMojo(mojo);
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

        mojo.setFindingThresholds(new FindingThresholds());

        assertDoesNotThrow(
                () -> {
                    mojo.execute();
                },
                "Exception not expected");
    }

    @Test
    @InjectMojo(goal = "findings")
    void thatFindingsIsSkippedWhenSkipIsTrue(FindingsMojo mojo) throws Exception {
        configureMojo(mojo);
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

        mojo.setSkip("true");

        mojo.execute();

        verify(exactly(0), getRequestedFor(urlPathEqualTo(V1_PROJECT_LOOKUP)));
        verify(exactly(0), getRequestedFor(urlPathMatching(V1_FINDING_PROJECT_UUID)));
    }
}
