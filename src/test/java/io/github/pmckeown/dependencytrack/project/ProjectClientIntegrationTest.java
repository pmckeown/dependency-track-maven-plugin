package io.github.pmckeown.dependencytrack.project;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_PROJECT_WITH_ONE_MILLION_LIMIT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojoTest;
import io.github.pmckeown.dependencytrack.CommonConfig;

public class ProjectClientIntegrationTest extends AbstractDependencyTrackMojoTest {

	private static final int COUNT_ALL_PROJECTS = 7;

	private ProjectClient projectClient;

	@Before
	public void setUp() {
		CommonConfig commonConfig = new CommonConfig();
		commonConfig.setDependencyTrackBaseUrl("http://localhost:" + wireMockRule.port());
		projectClient = new ProjectClient(commonConfig);
	}

	@Test
	public void thatCallingDependencyTrackWithAHighResponseLimitReturnsAllProjects() {

		stubFor(get(urlEqualTo(V1_PROJECT_WITH_ONE_MILLION_LIMIT)).willReturn(
				aResponse().withBodyFile("api/v1/project/get-all-projects.json")));

		List<Project> projects = projectClient.getProjects().getBody().orElse(Collections.emptyList());

		verify(exactly(1), getRequestedFor(urlEqualTo(V1_PROJECT_WITH_ONE_MILLION_LIMIT)));
		assertThat(projects.size(), is(COUNT_ALL_PROJECTS));
	}
}
