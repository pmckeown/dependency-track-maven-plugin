package io.github.pmckeown.dependencytrack;

import javax.inject.Singleton;

/**
 * Holder for common configuration supplied on Mojo execution
 *
 * @author Paul McKeown
 */
@Singleton
public class CommonConfig {

    private String projectName;
    private String projectVersion;
    private String dependencyTrackBaseUrl;
    private String apiKey;
    private boolean failOnError;

    public CommonConfig() {
        // For dependency injection
    }

    public CommonConfig(String projectName, String projectVersion, String dependencyTrackBaseUrl, String apiKey,
            boolean failOnError) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.dependencyTrackBaseUrl = dependencyTrackBaseUrl;
        this.apiKey = apiKey;
        this.failOnError = failOnError;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public String getDependencyTrackBaseUrl() {
        return dependencyTrackBaseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public boolean isFailOnError() {
        return failOnError;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }

    public void setDependencyTrackBaseUrl(String dependencyTrackBaseUrl) {
        this.dependencyTrackBaseUrl = dependencyTrackBaseUrl;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }
}
