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
import org.apache.maven.project.MavenProject;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Provides the capability to find the current Inherited Risk Score as determined by the Dependency Track Server.
 *
 * @author Paul McKeown
 */
@Mojo(name = "score", defaultPhase = LifecyclePhase.VERIFY)
public class ScoreMojo extends AbstractDependencyTrackMojo {

    private static final String DELIMITER = "========================================================================";

    @Parameter(defaultValue = "${project}")
    private MavenProject mavenProject;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        info("score goal started");

        GetProjectsResponse response = dependencyTrackClient().getProjects();

        if (response.isSuccess()) {
            List<Project> projects = response.getBody();
            info("Found %s projects", projects.size());

            String artifactId = mavenProject.getArtifactId();
            String version = mavenProject.getVersion();

            debug("Searching for project using Artifact ID: [%s] and Version [%s]", artifactId, version);
            Optional<Project> projectOptional = findCurrentProject(projects, artifactId, version);

            if (projectOptional.isPresent()) {
                Project project = projectOptional.get();
                Metrics metrics = project.getMetrics();

                info(DELIMITER);
                info("Project: %s, Version: %s", project.getName(), project.getVersion());

                String message = format("Inherited Risk Score: %d", metrics.getInheritedRiskScore());
                if (metrics.getInheritedRiskScore() > 0) {
                    warning(message);
                } else {
                    info(message);
                }
                info(DELIMITER);

            } else {
                error("Failed to find project on server: Project: %s, Version: %s");
            }
        } else {
            error("Failed to get projects from Dependency Track: " + response.getStatusText());
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
    void setMavenProject(MavenProject mavenProject) {
        this.mavenProject = mavenProject;
    }
}
