package io.github.pmckeown.dependencytrack.policyviolation;

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
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyConditionBuilder.aPolicyCondition;
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyViolationBuilder.aPolicyViolation;
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyViolationListBuilder.aListOfPolicyViolations;
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

@ExtendWith(MockitoExtension.class)
class PolicyViolationsClientTest extends AbstractDependencyTrackIntegrationTest {

    @InjectMocks
    private PolicyViolationsClient policyClient;

    @Mock
    private Logger logger;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmri) {
        policyClient = new PolicyViolationsClient(getCommonConfig(wmri), logger);
    }

    @Test
    void thatPolicyViolationsCanBeRetrieved() throws Exception {
        stubFor(get(urlPathMatching(V1_POLICY_VIOLATION_PROJECT_UUID))
                .willReturn(aResponse()
                        .withBody(asJson(aListOfPolicyViolations()
                                .withPolicyViolation(aPolicyViolation()
                                        .withType("SEVERITY")
                                        .withPolicyCondition(aPolicyCondition()
                                                .withPolicy(new Policy("testPolicy", ViolationState.INFO)))
                                        .withComponent(aComponent()))
                                .build()))));

        Response<List<PolicyViolation>> response =
                policyClient.getPolicyViolationsForProject(aProject().build());

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
    void thatFailureToGetViolationsReturnsAnErrorResponse() {
        stubFor(get(urlPathMatching(V1_POLICY_VIOLATION_PROJECT_UUID)).willReturn(badRequest()));

        Response<List<PolicyViolation>> response =
                policyClient.getPolicyViolationsForProject(aProject().build());

        verify(1, getRequestedFor(urlPathMatching(V1_POLICY_VIOLATION_PROJECT_UUID)));
        assertThat(response.isSuccess(), is(equalTo(false)));
        assertThat(response.getStatus(), is(equalTo(400)));
    }

    @Test
    void thatAnErrorToGetFindingsThrowsException() {
        stubFor(get(urlPathMatching(V1_POLICY_VIOLATION_PROJECT_UUID))
                .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        try {
            policyClient.getPolicyViolationsForProject(aProject().build());
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(UnirestException.class)));
        }
    }
}
