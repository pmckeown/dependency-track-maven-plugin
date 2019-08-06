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

    public CommonConfig() {
        // For dependency injection
    }

    public CommonConfig(String projectName, String projectVersion, String dependencyTrackBaseUrl, String apiKey) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.dependencyTrackBaseUrl = dependencyTrackBaseUrl;
        this.apiKey = apiKey;
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

}
