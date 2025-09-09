package io.github.pmckeown.dependencytrack.finding;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_FINDING_PROJECT_UUID;
import static io.github.pmckeown.dependencytrack.TestUtils.asJson;
import static io.github.pmckeown.dependencytrack.finding.AnalysisBuilder.anAnalysis;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.finding.FindingBuilder.aFinding;
import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static io.github.pmckeown.dependencytrack.finding.Severity.LOW;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.aVulnerability;
import static io.github.pmckeown.dependencytrack.project.ProjectBuilder.aProject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import io.github.pmckeown.dependencytrack.AbstractDependencyTrackIntegrationTest;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.util.Logger;
import java.util.List;
import java.util.Optional;
import kong.unirest.UnirestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
class FindingsClientTest extends AbstractDependencyTrackIntegrationTest {

    @InjectMocks
    private FindingsClient findingClient;

    @Mock
    private Logger logger;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmri) {
        findingClient = new FindingsClient(getCommonConfig(wmri), logger);
    }

    @Test
    void thatFindingsCanBeRetrieved() throws Exception {
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID))
                .willReturn(aResponse()
                        .withBody(asJson(aListOfFindings()
                                .withFinding(aFinding()
                                        .withComponent(aComponent().withName("dodgy"))
                                        .withVulnerability(aVulnerability().withSeverity(LOW))
                                        .withAnalysis(anAnalysis()))
                                .build()))));

        Response<List<Finding>> response =
                findingClient.getFindingsForProject(aProject().build());

        verify(1, getRequestedFor(urlPathMatching(V1_FINDING_PROJECT_UUID)));
        assertThat(response.isSuccess(), is(equalTo(true)));
        Optional<List<Finding>> body = response.getBody();
        if (body.isPresent()) {
            List<Finding> findingList = body.get();
            assertThat(findingList.get(0), is(not(nullValue())));
            assertThat(findingList.get(0).getComponent().getName(), is(equalTo("dodgy")));
            assertThat(findingList.get(0).getVulnerability().getSeverity(), is(equalTo(LOW)));
            assertThat(findingList.get(0).getAnalysis().isSuppressed(), is(equalTo(false)));
        } else {
            fail("Body missing");
        }
    }

    @Test
    void thatFailureToGetFindingsReturnsAnErrorResponse() {
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID)).willReturn(badRequest()));

        Response<List<Finding>> response =
                findingClient.getFindingsForProject(aProject().build());

        verify(1, getRequestedFor(urlPathMatching(V1_FINDING_PROJECT_UUID)));
        assertThat(response.isSuccess(), is(equalTo(false)));
        assertThat(response.getStatus(), is(equalTo(400)));
    }

    @Test
    void thatAnErrorToGetFindingsThrowsException() {
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID))
                .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        try {
            findingClient.getFindingsForProject(aProject().build());
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(UnirestException.class)));
        }
    }
}
