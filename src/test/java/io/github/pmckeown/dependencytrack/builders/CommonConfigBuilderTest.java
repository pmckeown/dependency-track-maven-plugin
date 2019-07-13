package io.github.pmckeown.dependencytrack.builders;

import io.github.pmckeown.dependencytrack.CommonConfig;
import org.junit.Test;

import static io.github.pmckeown.dependencytrack.builders.CommonConfigBuilder.config;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CommonConfigBuilderTest {

    private static final String PROJECT_NAME = "name";
    private static final String PROJECT_VERSION = "1.2.3";
    private static final String BASE_URL = "url";
    private static final String BASE_URL_WITH_TRAILING_SLASH = "url/";
    private static final String API_KEY = "key";
    private static final boolean FAIL_ON_ERROR = true;

    @Test
    public void thatProjectNameIsAssignedProperly() {
        CommonConfig config = config().withProjectName(PROJECT_NAME).build();

        assertThat(config.getProjectName(), is(equalTo(PROJECT_NAME)));
    }

    @Test
    public void thatProjectVersionIsAssignedProperly() {
        CommonConfig config = config().withProjectVersion(PROJECT_VERSION).build();

        assertThat(config.getProjectVersion(), is(equalTo(PROJECT_VERSION)));
    }

    @Test
    public void thatDependencyTrackBaseUrlIsAssignedProperly() {
        CommonConfig config = config().withDependencyTrackBaseUrl(BASE_URL).build();

        assertThat(config.getDependencyTrackBaseUrl(), is(equalTo(BASE_URL)));
    }

    @Test
    public void thatDependencyTrackBaseUrlIsTrimmedOfTrailingSlash() {
        CommonConfig config = config().withDependencyTrackBaseUrl(BASE_URL_WITH_TRAILING_SLASH).build();

        assertThat(config.getDependencyTrackBaseUrl(), is(equalTo(BASE_URL)));
    }

    @Test
    public void thatApiKeyIsAssignedProperly() {
        CommonConfig config = config().withApiKey(API_KEY).build();

        assertThat(config.getApiKey(), is(equalTo(API_KEY)));
    }

    @Test
    public void thatFailOnErrorIsAssignedProperly() {
        CommonConfig config = config().shouldFailOnError(FAIL_ON_ERROR).build();

        assertThat(config.isFailOnError(), is(equalTo(FAIL_ON_ERROR)));
    }

}
