package io.github.pmckeown.mojo;

import io.github.pmckeown.rest.client.DependencyTrackClient;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Base class for Mojos in this project.
 *
 * Provides common configuration options:
 * <ol>
 *     <li>projectName</li>
 *     <li>projectVersion</li>
 *     <li>dependencyTrackBaseUrl</li>
 *     <li>apiKey</li>
 *     <li>failOnError</li>
 * </ol>
 *
 * @author Paul McKeown
 */
public abstract class AbstractDependencyTrackMojo extends AbstractMojo {

    @Parameter(required = true, defaultValue = "${project.artifactId}", property = "dependency-track.projectName")
    protected String projectName;

    @Parameter(required = true, defaultValue = "${project.version}", property = "dependency-track.projectVersion")
    protected String projectVersion;

    @Parameter(required = true)
    private String dependencyTrackBaseUrl;

    @Parameter(required = true)
    private String apiKey;

    @Parameter(defaultValue = "false")
    private boolean failOnError;

    protected DependencyTrackClient dependencyTrackClient() {
        info("Connecting to Dependency Track instance: %s", dependencyTrackBaseUrl);
        return new DependencyTrackClient(dependencyTrackBaseUrl, apiKey);
    }

    protected void info(String message, Object... params) {
        if(getLog().isInfoEnabled()) {
            getLog().info(String.format(message, params));
        }
    }

    protected void warning(String message, Object... params) {
        if(getLog().isWarnEnabled()) {
            getLog().warn(String.format(message, params));
        }
    }

    protected void debug(String message, Object... params) {
        if(getLog().isDebugEnabled()) {
            getLog().debug(String.format(message, params));
        }
    }

    protected void error(String message, Object... params) {
        if(getLog().isErrorEnabled()) {
            getLog().error(String.format(message, params));
        }
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }

    public void setDependencyTrackBaseUrl(String url) {
        this.dependencyTrackBaseUrl = url;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setFailOnError(boolean fail) {
        this.failOnError = fail;
    }

    protected boolean shouldFailOnError() {
        return failOnError;
    }
}
