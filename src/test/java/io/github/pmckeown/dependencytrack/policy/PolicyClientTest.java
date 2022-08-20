package io.github.pmckeown.dependencytrack.policy;

import com.github.tomakehurst.wiremock.http.Fault;
import io.github.pmckeown.dependencytrack.AbstractDependencyTrackIntegrationTest;
import io.github.pmckeown.dependencytrack.Response;
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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_POLICY_VIOLATION_PROJECT_UUID;
import static io.github.pmckeown.dependencytrack.TestUtils.asJson;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.policy.PolicyConditionBuilder.aPolicyCondition;
import static io.github.pmckeown.dependencytrack.policy.PolicyViolationBuilder.aPolicyViolation;
import static io.github.pmckeown.dependencytrack.policy.PolicyViolationListBuilder.aListOfPolicyViolations;
import static io.github.pmckeown.dependencytrack.project.ProjectBuilder.aProject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class PolicyClientTest extends AbstractDependencyTrackIntegrationTest {

    @InjectMocks
    private PolicyClient policyClient;

    @Mock
    private Logger logger;

    @Before
    public void setup() {
        policyClient = new PolicyClient(getCommonConfig(), logger);
    }

    @Test
    public void thatPolicyViolationsCanBeRetrieved() throws Exception {
        stubFor(get(urlPathMatching(V1_POLICY_VIOLATION_PROJECT_UUID)).willReturn(
                aResponse().withBody(asJson(
                        aListOfPolicyViolations()
                                .withPolicyViolation(
                                        aPolicyViolation()
                                                .withType("SEVERITY")
                                                .withPolicyCondition(aPolicyCondition()
                                                        .withPolicy(new Policy("testPolicy", ViolationState.INFO)))
                                                .withComponent(aComponent())).build()))));

        Response<List<PolicyViolation>> response = policyClient.getPolicyViolationsForProject(aProject().build());

        verify(1, getRequestedFor(urlPathMatching(V1_POLICY_VIOLATION_PROJECT_UUID)));
        assertThat(response.isSuccess(), is(equalTo(true)));
        Optional<List<PolicyViolation>> body = response.getBody();
        if (body.isPresent()) {
            List<PolicyViolation> policyViolations = body.get();
            assertThat(policyViolations.get(0), is(not(nullValue())));
            assertThat(policyViolations.get(0).getComponent().getName(), is(equalTo("password-printer")));
            assertThat(policyViolations.get(0).getType(), is(equalTo("SEVERITY")));
            assertThat(policyViolations.get(0).getPolicyCondition().getPolicy().getName(), is(equalTo("testPolicy")));
        } else {
            fail("Body missing");
        }
    }

    @Test
    public void thatFailureToGetViolationsReturnsAnErrorResponse() {
        stubFor(get(urlPathMatching(V1_POLICY_VIOLATION_PROJECT_UUID)).willReturn(badRequest()));

        Response<List<PolicyViolation>> response = policyClient.getPolicyViolationsForProject(aProject().build());

        verify(1, getRequestedFor(urlPathMatching(V1_POLICY_VIOLATION_PROJECT_UUID)));
        assertThat(response.isSuccess(), is(equalTo(false)));
        assertThat(response.getStatus(), is(equalTo(400)));
    }

    @Test
    public void thatAnErrorToGetFindingsThrowsException() {
        stubFor(get(urlPathMatching(V1_POLICY_VIOLATION_PROJECT_UUID)).willReturn(
                aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        try {
            policyClient.getPolicyViolationsForProject(aProject().build());
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(UnirestException.class)));
        }
    }

}
