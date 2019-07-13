package io.github.pmckeown.dependencytrack;

/**
 * Holder for common configuration supplied on Mojo execution
 *
 * @author Paul McKeown
 */
public class CommonConfig {

    private String projectName;
    private String projectVersion;
    private String dependencyTrackBaseUrl;
    private String apiKey;
    private boolean failOnError;

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
}
