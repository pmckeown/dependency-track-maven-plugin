package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojoTest;
import io.github.pmckeown.dependencytrack.ResourceConstants;
import io.github.pmckeown.dependencytrack.TestResourceConstants;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static io.github.pmckeown.TestMojoLoader.loadFindingsMojo;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_FINDING_PROJECT_UUID;
import static io.github.pmckeown.dependencytrack.TestUtils.asJson;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.finding.FindingBuilder.aFinding;
import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.aVulnerability;

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
                                                .withVulnerability(aVulnerability().withSeverity("LOW"))
                                                .withAnalysis(false)).build()))));

        FindingsMojo findingsMojo = loadFindingsMojo(mojoRule);
        findingsMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        findingsMojo.setApiKey("abc123");
        findingsMojo.setProjectName("testName");
        findingsMojo.setProjectVersion("99.99");

        findingsMojo.execute();

        verify(exactly(1), getRequestedFor(urlEqualTo(ResourceConstants.V1_PROJECT)));
        verify(exactly(1), getRequestedFor(urlPathMatching(TestResourceConstants.V1_FINDING_PROJECT_UUID)));
    }
}
