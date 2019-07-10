package io.github.pmckeown.mojo.score;

import io.github.pmckeown.mojo.AbstractDependencyTrackMojo;
import io.github.pmckeown.rest.model.Metrics;
import io.github.pmckeown.rest.model.Project;
import io.github.pmckeown.rest.model.ResponseWithOptionalBody;
import kong.unirest.UnirestException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Provides the capability to find the current Inherited Risk Score as determined by the Dependency Track Server.
 *
 * Specific configuration options are:
 * <ol>
 *     <li>inheritedRiskScoreThreshold</li>
 * </ol>
 *
 * @author Paul McKeown
 */
@Mojo(name = "score", defaultPhase = LifecyclePhase.VERIFY)
public class ScoreMojo extends AbstractDependencyTrackMojo {

    private static final String DELIMITER = "========================================================================";

    @Parameter
    private Integer inheritedRiskScoreThreshold;

    @Override
    public void execute() throws MojoFailureException, MojoExecutionException {
        try {
            ResponseWithOptionalBody<List<Project>> response = dependencyTrackClient().getProjects();

            if (response.isSuccess() && response.getBody().isPresent()) {
                parseAndHandleResponse(response.getBody().get());
            } else {
                handleFailure(format("Failed to get projects from Dependency Track with error: %d %s",
                        response.getStatus(), response.getStatusText()));
            }
        } catch (UnirestException ex) {
            error(ex.getMessage());
            handleFailure("Get score failed");
        }
    }

    private void parseAndHandleResponse(List<Project> projects) throws MojoExecutionException, MojoFailureException {
        debug(projects.toString());

        info("Found %s projects", projects.size());
        Optional<Project> projectOptional = findCurrentProject(projects, projectName, projectVersion);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();

            Metrics metrics = getProjectMetrics(project);

            printInheritedRiskScore(project, metrics.getInheritedRiskScore());

            failBuildIfThresholdIsBreached(metrics.getInheritedRiskScore());

        } else {
            handleFailure(format("Failed to find project on server: Project: %s, Version: %s", projectName,
                    projectVersion));
        }
    }

    private void printInheritedRiskScore(Project project, int inheritedRiskScore) {
        info(DELIMITER);
        info("Project: %s, Version: %s", project.getName(), project.getVersion());
        StringBuilder scoreMessage = new StringBuilder(format("Inherited Risk Score: %d", inheritedRiskScore));

        if (inheritedRiskScoreThreshold != null) {
            scoreMessage.append(format(" - Maximum allowed Inherited Risk Score: %d", inheritedRiskScoreThreshold));
        }

        if (inheritedRiskScore > 0) {
            warning(scoreMessage.toString());
        } else {
            info(scoreMessage.toString());
        }
        info(DELIMITER);
    }

    private Metrics getProjectMetrics(Project project) throws MojoExecutionException {
        Metrics metrics = project.getMetrics();
        if (metrics == null) {
            info("Metrics not present, checking the server for more info");
            ResponseWithOptionalBody<Metrics> response = dependencyTrackClient().getMetrics(project.getUuid());

            if (response.getBody().isPresent()) {
                debug("Metrics found for project: %s", project.getUuid());
                debug(response.getBody().get().toString());
                return response.getBody().get();
            } else {
                throw new MojoExecutionException("No metrics have yet been calculated. Request a metrics analysis " +
                        "in the Dependency Track UI.");
            }
        }
        return metrics;
    }

    private void failBuildIfThresholdIsBreached(int inheritedRiskScore) throws MojoFailureException {
        debug("Inherited Risk Score Threshold set to: %s",
                inheritedRiskScoreThreshold == null ? "Not set" : inheritedRiskScoreThreshold);

        if (inheritedRiskScoreThreshold != null && inheritedRiskScore > inheritedRiskScoreThreshold) {

            throw new MojoFailureException(format("Inherited Risk Score [%d] was greater than the " +
                    "configured threshold [%d]", inheritedRiskScore, inheritedRiskScoreThreshold));
        }
    }

    private Optional<Project> findCurrentProject(List<Project> projects, String name, String version) {
        debug("Searching for project using Name: [%s] and Version [%s]", name, version);
        return projects.stream()
                .parallel()
                .peek(project -> debug(project.toString()))
                .filter(project -> project.getName().equals(name))
                .filter(project -> project.getVersion().equals(version))
                .findFirst();
    }

    /*
     * Setters for dependency injection in tests
     */
    void setInheritedRiskScoreThreshold(Integer threshold) {
        this.inheritedRiskScoreThreshold = threshold;
    }
}
