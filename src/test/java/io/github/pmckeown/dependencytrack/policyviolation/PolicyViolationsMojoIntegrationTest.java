package io.github.pmckeown.dependencytrack.policyviolation;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojoTest;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.http.Fault.RANDOM_DATA_THEN_CLOSE;
import static io.github.pmckeown.TestMojoLoader.loadPolicyMojo;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT_LOOKUP;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_POLICY_VIOLATION_PROJECT_UUID;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class PolicyViolationsMojoIntegrationTest extends AbstractDependencyTrackMojoTest {

    private PolicyViolationsMojo policyMojo;

    @Before
    public void setup() throws Exception {
        policyMojo = loadPolicyMojo(mojoRule);
        policyMojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
        policyMojo.setApiKey("abc123");
        policyMojo.setProjectName("testName");
        policyMojo.setProjectVersion("99.99");
    }

    @Test
    public void thatPolicyMojoCanRetrievePolicyViolationWarningsAndNotFailIfFailOnWarnFalse() throws Exception {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP)).willReturn(
                aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(get(urlMatching(V1_POLICY_VIOLATION_PROJECT_UUID)).willReturn(
                aResponse().withBodyFile("api/v1/violation/project/policy-violation-warnings.json")));
        policyMojo.setFailOnWarn(false);
        policyMojo.execute();

        verify(exactly(1), getRequestedFor(urlPathMatching(V1_POLICY_VIOLATION_PROJECT_UUID)));
    }

    @Test
    public void thatPolicyMojoCanRetrievePolicyViolationWarningsAndFailIfFailOnWarnTrue() {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP)).willReturn(
                aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(get(urlMatching(V1_POLICY_VIOLATION_PROJECT_UUID)).willReturn(
                aResponse().withBodyFile("api/v1/violation/project/policy-violation-warnings.json")));
        policyMojo.setFailOnWarn(true);

        try {
            policyMojo.execute();
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoFailureException.class)));
        }

        verify(exactly(1), getRequestedFor(urlPathMatching(V1_POLICY_VIOLATION_PROJECT_UUID)));
    }

    @Test
    public void thatPolicyMojoCanRetrievePolicyViolationFailuresAndFailIfFailOnWarnFalse() {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP)).willReturn(
                aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(get(urlMatching(V1_POLICY_VIOLATION_PROJECT_UUID)).willReturn(
                aResponse().withBodyFile("api/v1/violation/project/policy-violation-failures.json")));
        policyMojo.setFailOnWarn(false);

        try {
            policyMojo.execute();
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoFailureException.class)));
        }

        verify(exactly(1), getRequestedFor(urlPathMatching(V1_POLICY_VIOLATION_PROJECT_UUID)));
    }

    @Test
    public void thatPolicyMojoCanRetrievePolicyViolationFailuresAndFailIfFailOnWarnTrue() {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP)).willReturn(
                aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(get(urlMatching(V1_POLICY_VIOLATION_PROJECT_UUID)).willReturn(
                aResponse().withBodyFile("api/v1/violation/project/policy-violation-failures.json")));
        policyMojo.setFailOnWarn(true);

        try {
            policyMojo.execute();
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoFailureException.class)));
        }

        verify(exactly(1), getRequestedFor(urlPathMatching(V1_POLICY_VIOLATION_PROJECT_UUID)));
    }

    @Test
    public void thatWhenExceptionOccursWhileGettingFindingsAndFailOnErrorIsTrueTheMojoErrors() throws Exception {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP)).willReturn(
                aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(get(urlPathMatching(V1_POLICY_VIOLATION_PROJECT_UUID))
                .willReturn(aResponse().withFault(RANDOM_DATA_THEN_CLOSE)));

        policyMojo.setFailOnError(true);

        try {
            policyMojo.execute();
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoExecutionException.class)));
        }
    }

    @Test
    public void thatWhenExceptionOccursWhileGettingFindingsAndFailOnErrorIsFalseTheMojoSucceeds() throws Exception {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP)).willReturn(
                aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(get(urlPathMatching(V1_POLICY_VIOLATION_PROJECT_UUID))
                .willReturn(aResponse().withFault(RANDOM_DATA_THEN_CLOSE)));

        policyMojo.setFailOnError(false);

        try {
            policyMojo.execute();
        } catch (Exception ex) {
            fail("Exception not expected");
        }
    }

    @Test
    public void thatPolicyViolationsIsSkippedWhenSkipIsTrue() throws Exception {
        policyMojo.setSkip("true");

        policyMojo.execute();

        verify(exactly(0), getRequestedFor(urlPathEqualTo(V1_PROJECT_LOOKUP)));
        verify(exactly(0), getRequestedFor(urlPathMatching(V1_POLICY_VIOLATION_PROJECT_UUID)));
    }
}
