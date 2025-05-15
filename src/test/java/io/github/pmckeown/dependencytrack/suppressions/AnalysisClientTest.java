package io.github.pmckeown.dependencytrack.suppressions;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_ANALYSIS_PROJECT_UUID;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_FINDING_PROJECT_UUID;
import static io.github.pmckeown.dependencytrack.TestUtils.asJson;
import static io.github.pmckeown.dependencytrack.finding.AnalysisBuilder.anAnalysis;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.finding.FindingBuilder.aFinding;
import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static io.github.pmckeown.dependencytrack.finding.Severity.CRITICAL;
import static io.github.pmckeown.dependencytrack.finding.Severity.LOW;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.aVulnerability;
import static io.github.pmckeown.dependencytrack.project.ProjectBuilder.aProject;
import static io.github.pmckeown.dependencytrack.suppressions.AnalysisBuilder.fixType1Analysis;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import com.github.tomakehurst.wiremock.http.Fault;
import io.github.pmckeown.dependencytrack.AbstractDependencyTrackIntegrationTest;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.finding.Finding;
import io.github.pmckeown.dependencytrack.finding.FindingsClient;
import io.github.pmckeown.util.Logger;
import java.util.List;
import java.util.Optional;
import kong.unirest.UnirestException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AnalysisClientTest extends AbstractDependencyTrackIntegrationTest {

    @InjectMocks
    private AnalysisClient analysisClient;

    @Mock
    private Logger logger;

    @Before
    public void setup() {
        analysisClient = new AnalysisClient(getCommonConfig(), logger);
    }

    @Test
    public void thatAnalysisCouldBeUploaded() throws Exception {
        stubFor(get(urlPathMatching(V1_ANALYSIS_PROJECT_UUID)).willReturn(
                aResponse().withBody(asJson(fixType1Analysis().build()))));

        Response<List<Finding>> response = analysisClient.uploadAnalysis(fixType1Analysis().build());

        verify(1, getRequestedFor(urlPathMatching(V1_FINDING_PROJECT_UUID)));
        assertThat(response.isSuccess(), is(equalTo(true)));
        Optional<List<Finding>> body = response.getBody();
        if (body.isPresent()) {
            List<Finding> findingList = body.get();
            assertThat(findingList.get(0), is(not(nullValue())));
            assertThat(findingList.get(0).getComponent().getName(), is(equalTo("dodgy")));
            assertThat(findingList.get(0).getVulnerability().getSeverity(), is(equalTo(LOW)));
            assertThat(findingList.get(0).getAnalysis().getIsSuppressed(), is(equalTo(false)));
        } else {
            fail("Body missing");
        }
    }

    @Test
    public void thatSuppressedFindingsCanBeRetrieved() throws Exception {
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID)).willReturn(
            aResponse().withBody(asJson(
                aListOfFindings()
                    .withFinding(
                        aFinding()
                            .withComponent(aComponent().withName("dodgy"))
                            .withVulnerability(aVulnerability().withSeverity(LOW))
                            .withAnalysis(anAnalysis()))
                    .withFinding(
                        aFinding()
                            .withComponent(aComponent().withName("ghosty"))
                            .withVulnerability(aVulnerability().withSeverity(CRITICAL))
                            .withAnalysis(anAnalysis().withSuppressed(true)))
                    .build()))));

        Response<List<Finding>> response = analysisClient.getFindingsForProject(aProject().build(), true);

        verify(1, getRequestedFor(urlPathMatching(V1_FINDING_PROJECT_UUID)));
        assertThat(response.isSuccess(), is(equalTo(true)));
        Optional<List<Finding>> body = response.getBody();
        if (body.isPresent()) {
            List<Finding> findingList = body.get();
            assertThat(findingList.size(), is(equalTo(2)));
            assertThat(findingList.get(0), is(not(nullValue())));
            assertThat(findingList.get(0).getComponent().getName(), is(equalTo("dodgy")));
            assertThat(findingList.get(0).getVulnerability().getSeverity(), is(equalTo(LOW)));
            assertThat(findingList.get(0).getAnalysis().getIsSuppressed(), is(equalTo(false)));
            assertThat(findingList.get(1), is(not(nullValue())));
            assertThat(findingList.get(1).getComponent().getName(), is(equalTo("ghosty")));
            assertThat(findingList.get(1).getVulnerability().getSeverity(), is(equalTo(CRITICAL)));
            assertThat(findingList.get(1).getAnalysis().getIsSuppressed(), is(equalTo(true)));
        } else {
            fail("Body missing");
        }
    }

    @Test
    public void thatFailureToGetFindingsReturnsAnErrorResponse() {
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID)).willReturn(badRequest()));

        Response<List<Finding>> response = analysisClient.getFindingsForProject(aProject().build());

        verify(1, getRequestedFor(urlPathMatching(V1_FINDING_PROJECT_UUID)));
        assertThat(response.isSuccess(), is(equalTo(false)));
        assertThat(response.getStatus(), is(equalTo(400)));
    }

    @Test
    public void thatAnErrorToGetFindingsThrowsException() {
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID)).willReturn(
                aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        try {
            analysisClient.getFindingsForProject(aProject().build());
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(UnirestException.class)));
        }
    }

}
