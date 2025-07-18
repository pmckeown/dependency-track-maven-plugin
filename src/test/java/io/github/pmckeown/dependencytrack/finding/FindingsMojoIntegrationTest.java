package io.github.pmckeown.dependencytrack.finding;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.http.Fault.RANDOM_DATA_THEN_CLOSE;
import static io.github.pmckeown.TestMojoLoader.loadFindingsMojo;
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
import static org.junit.Assert.fail;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojoTest;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;

public class FindingsMojoIntegrationTest extends AbstractDependencyTrackMojoTest {

    private FindingsMojo findingsMojo;

    @Before
    public void setup() throws Exception {
        findingsMojo = loadFindingsMojo(mojoRule);
        findingsMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        findingsMojo.setApiKey("abc123");
        findingsMojo.setProjectName("testName");
        findingsMojo.setProjectVersion("99.99");
    }

    @Test
    public void thatFindingMojoCanRetrieveFindingsAndPrintThem() throws Exception {
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
    public void thatWhenNoFindingsAreFoundTheMojoDoesNotFail() throws Exception {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID)).willReturn(ok()));

        try {
            findingsMojo.execute();
            verify(exactly(1), getRequestedFor(urlPathEqualTo(V1_PROJECT_LOOKUP)));
            verify(exactly(1), getRequestedFor(urlPathMatching(V1_FINDING_PROJECT_UUID)));
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test(expected = MojoExecutionException.class)
    public void thatWhenExceptionOccursWhileGettingFindingsAndFailOnErrorIsTrueTheMojoErrors() throws Exception {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID))
                .willReturn(aResponse().withFault(RANDOM_DATA_THEN_CLOSE)));

        findingsMojo.setFailOnError(true);

        findingsMojo.execute();
        fail("Exception expected");
    }

    @Test(expected = MojoFailureException.class)
    public void thatBuildFailsWhenFindingsNumberBreachesDefinedThresholds() throws Exception {
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
    }

    @Test
    public void thatBuildDoesNotFailWhenOnlyUnassignedFindingExists() throws Exception {
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

        try {
            findingsMojo.execute();
        } catch (Exception ex) {
            fail("Exception not expected");
        }
    }

    @Test
    public void thatFindingsIsSkippedWhenSkipIsTrue() throws Exception {
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
