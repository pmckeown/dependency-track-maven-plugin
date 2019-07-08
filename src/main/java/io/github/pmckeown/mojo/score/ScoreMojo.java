package io.github.pmckeown.mojo.score;

import io.github.pmckeown.mojo.AbstractDependencyTrackMojo;
import io.github.pmckeown.rest.model.GetProjectsResponse;
import io.github.pmckeown.rest.model.Metrics;
import io.github.pmckeown.rest.model.Project;
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
        GetProjectsResponse response = dependencyTrackClient().getProjects();

        if (response.isSuccess()) {
            List<Project> projects = response.getBody();
            info("Found %s projects", projects.size());

            debug("Searching for project using Artifact ID: [%s] and Version [%s]", projectName, projectVersion);
            Optional<Project> projectOptional = findCurrentProject(projects, projectName, projectVersion);

            if (projectOptional.isPresent()) {
                Project project = projectOptional.get();

                Metrics metrics = project.getMetrics();
                if (metrics == null) {
                    throw new MojoExecutionException("No metrics have yet been calculated. Request a metrics analysis" +
                            "in the Dependency Track UI.");
                }

                int inheritedRiskScore = metrics.getInheritedRiskScore();

                info(DELIMITER);
                info("Project: %s, Version: %s", project.getName(), project.getVersion());
                StringBuffer scoreMessage = new StringBuffer(format("Inherited Risk Score: %d", inheritedRiskScore));

                if (inheritedRiskScoreThreshold != null) {
                    scoreMessage.append(format(" - Maximum allowed Inherited Risk Score: %d", inheritedRiskScoreThreshold));
                }

                if (inheritedRiskScore > 0) {
                    warning(scoreMessage.toString());
                } else {
                    info(scoreMessage.toString());
                }
                info(DELIMITER);

                compareScoreToThreshold(inheritedRiskScore);

            } else {
                String message = format("Failed to find project on server: Project: %s, Version: %s",
                        projectName, projectVersion);
                error(message);

                if (shouldFailOnError()) {
                    throw new MojoFailureException(message);
                }
            }
        } else {
            error("Failed to get projects from Dependency Track with error: ", response.getStatusText());
        }
    }

    private void compareScoreToThreshold(int inheritedRiskScore) throws MojoFailureException {
        debug("Inherited Risk Score Threshold set to: ", inheritedRiskScoreThreshold);

        if (inheritedRiskScoreThreshold != null && inheritedRiskScore > inheritedRiskScoreThreshold) {

            throw new MojoFailureException(format("Inherited Risk Score [%d] was greater than the " +
                    "configured threshold [%d]", inheritedRiskScore, inheritedRiskScoreThreshold));
        }
    }

    private Optional<Project> findCurrentProject(List<Project> projects, String artifactId, String version) {
        return projects.stream()
                .parallel()
                .peek(project -> debug(project.toString()))
                .filter(project -> project.getName().equals(artifactId))
                .filter(project -> project.getVersion().equals(version))
                .findFirst();
    }

    /*
     * Setters for dependency injection in tests
     */
    void setInheritedRiskScoreThreshold(int threshold) {
        this.inheritedRiskScoreThreshold = threshold;
    }
}
