package io.github.pmckeown.dependencytrack.policyviolation;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.http.Fault.RANDOM_DATA_THEN_CLOSE;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT_LOOKUP;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_POLICY_VIOLATION_PROJECT_UUID;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojoTest;
import org.apache.maven.api.plugin.testing.Basedir;
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoParameter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PolicyViolationsMojoIntegrationTest extends AbstractDependencyTrackMojoTest {

    PolicyViolationsMojo policyMojo;

    @BeforeEach
    @Basedir(TEST_PROJECT)
    @InjectMojo(goal = "policy-violations")
    @MojoParameter(name = "projectName", value = "testName")
    @MojoParameter(name = "projectVersion", value = "99.99")
    void setUp(PolicyViolationsMojo mojo) {
        policyMojo = mojo;
        configureMojo(policyMojo);
    }

    @Test
    void thatPolicyMojoCanRetrievePolicyViolationWarningsAndNotFailIfFailOnWarnFalse() throws Exception {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(get(urlMatching(V1_POLICY_VIOLATION_PROJECT_UUID))
                .willReturn(aResponse().withBodyFile("api/v1/violation/project/policy-violation-warnings.json")));
        policyMojo.setFailOnWarn(false);
        policyMojo.execute();

        verify(exactly(1), getRequestedFor(urlPathMatching(V1_POLICY_VIOLATION_PROJECT_UUID)));
    }

    @Test
    void thatPolicyMojoCanRetrievePolicyViolationWarningsAndFailIfFailOnWarnTrue() {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(get(urlMatching(V1_POLICY_VIOLATION_PROJECT_UUID))
                .willReturn(aResponse().withBodyFile("api/v1/violation/project/policy-violation-warnings.json")));
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
    void thatPolicyMojoCanRetrievePolicyViolationFailuresAndFailIfFailOnWarnFalse() {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(get(urlMatching(V1_POLICY_VIOLATION_PROJECT_UUID))
                .willReturn(aResponse().withBodyFile("api/v1/violation/project/policy-violation-failures.json")));
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
    void thatPolicyMojoCanRetrievePolicyViolationFailuresAndFailIfFailOnWarnTrue() {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(get(urlMatching(V1_POLICY_VIOLATION_PROJECT_UUID))
                .willReturn(aResponse().withBodyFile("api/v1/violation/project/policy-violation-failures.json")));
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
    void thatWhenExceptionOccursWhileGettingFindingsAndFailOnErrorIsTrueTheMojoErrors() throws Exception {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
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
    void thatWhenExceptionOccursWhileGettingFindingsAndFailOnErrorIsFalseTheMojoSucceeds() {
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withBodyFile("api/v1/project/testName-project.json")));
        stubFor(get(urlPathMatching(V1_POLICY_VIOLATION_PROJECT_UUID))
                .willReturn(aResponse().withFault(RANDOM_DATA_THEN_CLOSE)));

        policyMojo.setFailOnError(false);

        assertDoesNotThrow(
                () -> {
                    policyMojo.execute();
                },
                "Exception not expected");
    }

    @Test
    void thatPolicyViolationsIsSkippedWhenSkipIsTrue() throws Exception {
        policyMojo.setSkip("true");

        policyMojo.execute();

        verify(exactly(0), getRequestedFor(urlPathEqualTo(V1_PROJECT_LOOKUP)));
        verify(exactly(0), getRequestedFor(urlPathMatching(V1_POLICY_VIOLATION_PROJECT_UUID)));
    }
}
