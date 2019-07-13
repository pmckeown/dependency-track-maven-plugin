package io.github.pmckeown.dependencytrack.builders;

import io.github.pmckeown.dependencytrack.CommonConfig;
import org.apache.commons.lang3.StringUtils;

public class CommonConfigBuilder {

    private String projectName;
    private String projectVersion;
    private String dependencyTrackBaseUrl;
    private String apiKey;
    private boolean failOnError;

    private CommonConfigBuilder() {
        // Use builder methods
    }

    public static CommonConfigBuilder config() {
        return new CommonConfigBuilder();
    }

    public CommonConfig build() {
        return new CommonConfig(projectName, projectVersion, dependencyTrackBaseUrl, apiKey, failOnError);
    }

    public CommonConfigBuilder withProjectName(String name) {
        this.projectName = name;
        return this;
    }

    public CommonConfigBuilder withProjectVersion(String version) {
        this.projectVersion = version;
        return this;
    }

    public CommonConfigBuilder withDependencyTrackBaseUrl(String baseUrl) {
        this.dependencyTrackBaseUrl = normaliseUrl(baseUrl);
        return this;
    }

    public CommonConfigBuilder withApiKey(String key) {
        this.apiKey = key;
        return this;
    }

    public CommonConfigBuilder shouldFailOnError(boolean shouldFailOnError) {
        this.failOnError = shouldFailOnError;
        return this;
    }

    private String normaliseUrl(String host) {
        if (StringUtils.endsWith(host,"/")) {
            return StringUtils.stripEnd(host,"/");
        }
        return host;
    }
}
