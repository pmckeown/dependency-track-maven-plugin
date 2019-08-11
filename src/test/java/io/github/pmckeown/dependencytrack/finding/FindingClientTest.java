package io.github.pmckeown.dependencytrack.finding;

import com.github.tomakehurst.wiremock.http.Fault;
import io.github.pmckeown.dependencytrack.AbstractDependencyTrackIntegrationTest;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_FINDING_PROJECT_UUID;
import static io.github.pmckeown.dependencytrack.TestUtils.asJson;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.finding.FindingBuilder.aFinding;
import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.aVulnerability;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class FindingClientTest extends AbstractDependencyTrackIntegrationTest {

    @InjectMocks
    private FindingClient findingClient;

    @Mock
    private Logger logger;

    @Before
    public void setup() {
        findingClient = new FindingClient(getCommonConfig(), logger);
    }

    @Test
    public void thatFindingsCanBeRetrieved() throws Exception {
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID)).willReturn(
                aResponse().withBody(asJson(
                        aListOfFindings()
                                .withFinding(
                                        aFinding()
                                                .withComponent(aComponent().withName("dodgy"))
                                                .withVulnerability(aVulnerability().withSeverity("LOW"))
                                                .withAnalysis(false)).build()))));

        Response<List<Finding>> response = findingClient.getFindingsForProject(aProject());

        verify(1, getRequestedFor(urlPathMatching(V1_FINDING_PROJECT_UUID)));
        assertThat(response.isSuccess(), is(equalTo(true)));
        Optional<List<Finding>> body = response.getBody();
        if (body.isPresent()) {
            List<Finding> findingList = body.get();
            assertThat(findingList.get(0), is(not(nullValue())));
            assertThat(findingList.get(0).getComponent().getName(), is(equalTo("dodgy")));
            assertThat(findingList.get(0).getVulnerability().getSeverity(), is(equalTo("LOW")));
            assertThat(findingList.get(0).getAnalysis().isSuppressed(), is(equalTo(false)));
        } else {
            fail("Body missing");
        }
    }

    @Test
    public void thatFailureToGetFindingsReturnsAnErrorResponse() {
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID)).willReturn(badRequest()));

        Response<List<Finding>> response = findingClient.getFindingsForProject(aProject());

        verify(1, getRequestedFor(urlPathMatching(V1_FINDING_PROJECT_UUID)));
        assertThat(response.isSuccess(), is(equalTo(false)));
        assertThat(response.getStatus(), is(equalTo(400)));
    }

    @Test
    public void thatAnErrorToGetFindingsThrowsException() {
        stubFor(get(urlPathMatching(V1_FINDING_PROJECT_UUID)).willReturn(
                aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        try {
            findingClient.getFindingsForProject(aProject());
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(UnirestException.class)));
        }
    }

    private Project aProject() {
        return new Project(UUID.randomUUID().toString(), PROJECT_NAME, PROJECT_VERSION, null);
    }

}
