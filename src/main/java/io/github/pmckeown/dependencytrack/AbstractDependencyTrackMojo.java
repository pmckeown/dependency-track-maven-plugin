package io.github.pmckeown.dependencytrack;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import static io.github.pmckeown.dependencytrack.builders.CommonConfigBuilder.config;

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

    @Parameter(required = true, property = "dependency-track.dependencyTrackBaseUrl")
    private String dependencyTrackBaseUrl;

    @Parameter(required = true, property = "dependency-track.apiKey")
    private String apiKey;

    @Parameter(defaultValue = "false", property = "dependency-track.failOnError")
    private boolean failOnError;

    protected CommonConfig commonConfig() {
        return config()
                .withProjectName(projectName)
                .withProjectVersion(projectVersion)
                .withDependencyTrackBaseUrl(dependencyTrackBaseUrl)
                .withApiKey(apiKey)
                .shouldFailOnError(failOnError)
                .build();
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

    protected void handleFailure(String message) throws MojoFailureException {
        getLog().error(message);
        if (failOnError) {
            throw new MojoFailureException(message);
        }
    }

    protected void handleFailure(String message, Throwable ex) throws MojoExecutionException {
        getLog().error(message, ex);
        if (failOnError) {
            throw new MojoExecutionException(message);
        }
    }
}
