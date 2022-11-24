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
    private boolean verifySsl;
    private PollingConfig pollingConfig;

    public CommonConfig() {
        // For dependency injection
    }

    public CommonConfig(String projectName, String projectVersion, String dependencyTrackBaseUrl, String apiKey, boolean verifySsl,
            PollingConfig pollingConfig) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.dependencyTrackBaseUrl = dependencyTrackBaseUrl;
        this.apiKey = apiKey;
        this.verifySsl = verifySsl;
        this.pollingConfig = pollingConfig;
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

    public PollingConfig getPollingConfig() {
        return pollingConfig;
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

    public void setPollingConfig(PollingConfig pollingConfig) {
        this.pollingConfig = pollingConfig;
    }

    public boolean isVerifySsl() {
        return verifySsl;
    }

    public void setVerifySsl(boolean verifySsl) {
        this.verifySsl = verifySsl;
    }

    
    
    
}
