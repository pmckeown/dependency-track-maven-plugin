package io.github.pmckeown.dependencytrack.project;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.pmckeown.dependencytrack.ResourceConstants.V1_PROJECT_LOOKUP;
import static io.github.pmckeown.dependencytrack.TestResourceConstants.V1_PROJECT_UUID;
import static io.github.pmckeown.dependencytrack.project.ProjectInfoBuilder.aProjectInfo;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.Response;
import kong.unirest.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@WireMockTest
class ProjectClientIntegrationTest {

    @InjectMocks
    private ProjectClient projectClient;

    @Mock
    private CommonConfig commonConfig;

    @Mock
    private ModuleConfig moduleConfig;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmri) {
        doReturn("http://localhost:" + wmri.getHttpPort()).when(commonConfig).getDependencyTrackBaseUrl();
        projectClient = new ProjectClient(commonConfig);
    }

    @Test
    void thatProjectInfoUpdateReturnsSuccessWhenServerReturnsSuccess() {
        stubFor(patch(urlPathMatching(V1_PROJECT_UUID)).willReturn(aResponse().withStatus(HttpStatus.OK)));

        Response<Void> response = projectClient.patchProject(
                "3b2fa278-6380-4430-b646-a353107e9fbe", aProjectInfo().build());
        assertThat(response.isSuccess(), is(equalTo(true)));
        verify(exactly(1), patchRequestedFor(urlPathMatching(V1_PROJECT_UUID)));
    }

    @Test
    void thatProjectParsingWorks() {
        doReturn("doesn't matter").when(moduleConfig).getProjectName();
        doReturn("doesn't matter").when(moduleConfig).getProjectVersion();
        stubFor(get(urlPathEqualTo(V1_PROJECT_LOOKUP))
                .willReturn(aResponse().withStatus(HttpStatus.OK).withBodyFile("api/v1/project/tags-project.json")));

        Response<Project> response = projectClient.getProject(
                moduleConfig.getProjectUuid(), moduleConfig.getProjectName(), moduleConfig.getProjectVersion());
        assertThat(response.isSuccess(), is(equalTo(true)));
        assertThat(response.getBody().isPresent(), is(equalTo(true)));
        Project project = response.getBody().get();
        assertThat(project.getTags(), hasItems(new ProjectTag("backend"), new ProjectTag("Team-1")));
        assertThat(project.getName(), is("tags-project"));
        assertThat(project.getVersion(), is("4.6.10"));
        assertThat(project.getUuid(), is("8c8fcbd1-b569-4e98-849f-884afef20a2a"));

        verify(exactly(1), getRequestedFor(urlPathMatching(V1_PROJECT_LOOKUP)));
    }

    @Test
    void thatProjectInfoUpdateReturnsSuccessWhenServerReturnsNotModified() {
        stubFor(patch(urlPathMatching(V1_PROJECT_UUID)).willReturn(aResponse().withStatus(HttpStatus.NOT_MODIFIED)));

        Response<Void> response = projectClient.patchProject(
                "3b2fa278-6380-4430-b646-a353107e9fbe", aProjectInfo().build());
        assertThat(response.isSuccess(), is(equalTo(true)));
        verify(exactly(1), patchRequestedFor(urlPathMatching(V1_PROJECT_UUID)));
    }

    @Test
    void thatProjectInfoUpdateReturnsFailedWhenServerReturnsTeapot() {
        stubFor(patch(urlPathMatching(V1_PROJECT_UUID)).willReturn(aResponse().withStatus(HttpStatus.IM_A_TEAPOT)));

        Response<Void> response = projectClient.patchProject(
                "3b2fa278-6380-4430-b646-a353107e9fbe", aProjectInfo().build());
        assertThat(response.isSuccess(), is(equalTo(false)));
        verify(exactly(1), patchRequestedFor(urlPathMatching(V1_PROJECT_UUID)));
    }
}
