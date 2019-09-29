package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojoTest;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.http.Fault.RANDOM_DATA_THEN_CLOSE;
import static io.github.pmckeown.TestMojoLoader.loadFindingsMojo;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_FINDING_PROJECT_UUID;
import static io.github.pmckeown.dependencytrack.TestUtils.asJson;
import static io.github.pmckeown.dependencytrack.finding.AnalysisBuilder.anAnalysis;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.finding.FindingBuilder.aFinding;
import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static io.github.pmckeown.dependencytrack.finding.Severity.LOW;
import static io.github.pmckeown.dependencytrack.finding.Severity.UNASSIGNED;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.aVulnerability;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class FindingsMojoIntegrationTest extends AbstractDependencyTrackMojoTest {

    @Test
    public void thatFindingMojoCanRetrieveFindingsAndPrintThem() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID)).willReturn(
                aResponse().withBody(asJson(
                        aListOfFindings()
                                .withFinding(
                                        aFinding()
                                                .withComponent(aComponent().withName("dodgy"))
                                                .withVulnerability(aVulnerability().withSeverity(LOW))
                                                .withAnalysis(anAnalysis())).build()))));

        FindingsMojo findingsMojo = loadFindingsMojo(mojoRule);
        findingsMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        findingsMojo.setApiKey("abc123");
        findingsMojo.setProjectName("testName");
        findingsMojo.setProjectVersion("99.99");

        findingsMojo.execute();

        verify(exactly(1), getRequestedFor(urlEqualTo(V1_PROJECT)));
        verify(exactly(1), getRequestedFor(urlPathMatching(V1_FINDING_PROJECT_UUID)));
    }

    @Test
    public void thatWhenNoFindingsAreFoundTheMojoDoesNotFail() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID)).willReturn(ok()));

        FindingsMojo findingsMojo = loadFindingsMojo(mojoRule);
        findingsMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        findingsMojo.setApiKey("abc123");
        findingsMojo.setProjectName("testName");
        findingsMojo.setProjectVersion("99.99");

        try {
            findingsMojo.execute();
            verify(exactly(1), getRequestedFor(urlEqualTo(V1_PROJECT)));
            verify(exactly(1), getRequestedFor(urlPathMatching(V1_FINDING_PROJECT_UUID)));
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test
    public void thatWhenExceptionOccursWhileGettingFindingsAndFailOnErrorIsTrueTheMojoErrors() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID)).willReturn(aResponse().withFault(RANDOM_DATA_THEN_CLOSE)));

        FindingsMojo findingsMojo = loadFindingsMojo(mojoRule);
        findingsMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        findingsMojo.setApiKey("abc123");
        findingsMojo.setProjectName("testName");
        findingsMojo.setProjectVersion("99.99");
        findingsMojo.setFailOnError(true);

        try {
            findingsMojo.execute();
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoExecutionException.class)));
        }
    }

    @Test
    public void thatBuildFailsWhenFindingsNumberBreachesDefinedThresholds() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID)).willReturn(
                aResponse().withBody(asJson(
                        aListOfFindings()
                                .withFinding(
                                        aFinding()
                                                .withComponent(aComponent().withName("dodgy"))
                                                .withVulnerability(aVulnerability().withSeverity(LOW))
                                                .withAnalysis(anAnalysis())).build()))));

        FindingsMojo findingsMojo = loadFindingsMojo(mojoRule);
        findingsMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        findingsMojo.setApiKey("abc123");
        findingsMojo.setProjectName("testName");
        findingsMojo.setProjectVersion("99.99");
        findingsMojo.setFindingThresholds(new FindingThresholds(0, 0, 0, 0));

        try {
            findingsMojo.execute();
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoFailureException.class)));
        }
    }

    @Test
    public void thatBuildDoesNotFailWhenOnlyUnassignedFindingExists() throws Exception {
        stubFor(get(urlPathMatching(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID)).willReturn(
                aResponse().withBody(asJson(
                        aListOfFindings()
                                .withFinding(
                                        aFinding()
                                                .withComponent(aComponent().withName("dodgy"))
                                                .withVulnerability(aVulnerability().withSeverity(UNASSIGNED))
                                                .withAnalysis(anAnalysis())).build()))));

        FindingsMojo findingsMojo = loadFindingsMojo(mojoRule);
        findingsMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        findingsMojo.setApiKey("abc123");
        findingsMojo.setProjectName("testName");
        findingsMojo.setProjectVersion("99.99");
        findingsMojo.setFindingThresholds(new FindingThresholds());

        try {
            findingsMojo.execute();
        } catch (Exception ex) {
            fail("Exception not expected");
        }
    }
}
