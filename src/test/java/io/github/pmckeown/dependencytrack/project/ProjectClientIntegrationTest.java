package io.github.pmckeown.dependencytrack.project;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojoTest;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.Response;
import kong.unirest.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.patchRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_PROJECT_UUID;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_PROJECT_WITH_ONE_MILLION_LIMIT;
import static io.github.pmckeown.dependencytrack.project.ProjectInfoBuilder.aProjectInfo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ProjectClientIntegrationTest extends AbstractDependencyTrackMojoTest {

	private static final int COUNT_ALL_PROJECTS = 9;

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

	@Test
	public void thatProjectInfoUpdateReturnsSuccessWhenServerReturnsSuccess() {
		stubFor(patch(urlPathMatching(V1_PROJECT_UUID)).willReturn(
				aResponse().withStatus(HttpStatus.OK)));

		Response<Void> response = projectClient.patchProject("1234", aProjectInfo().build());
		assertThat(response.isSuccess(), is(equalTo(true)));
		verify(exactly(1), patchRequestedFor(urlPathMatching(V1_PROJECT_UUID)));
	}

	@Test
	public void thatProjectInfoUpdateReturnsSuccessWhenServerReturnsNotModified() {
		stubFor(patch(urlPathMatching(V1_PROJECT_UUID)).willReturn(
				aResponse().withStatus(HttpStatus.NOT_MODIFIED)));

		Response<Void> response = projectClient.patchProject("1234", aProjectInfo().build());
		assertThat(response.isSuccess(), is(equalTo(true)));
		verify(exactly(1), patchRequestedFor(urlPathMatching(V1_PROJECT_UUID)));
	}

	@Test
	public void thatProjectInfoUpdateReturnsFailedWhenServerReturnsTeapot() {
		stubFor(patch(urlPathMatching(V1_PROJECT_UUID)).willReturn(
				aResponse().withStatus(HttpStatus.IM_A_TEAPOT)));

		Response<Void> response = projectClient.patchProject("1234", aProjectInfo().build());
		assertThat(response.isSuccess(), is(equalTo(false)));
		verify(exactly(1), patchRequestedFor(urlPathMatching(V1_PROJECT_UUID)));
	}
}
