package io.github.pmckeown.dependencytrack.project;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Singleton
public class ProjectAction {

    private ProjectClient projectClient;
    private Logger logger;

    @Inject
    public ProjectAction(ProjectClient projectClient, Logger logger) {
        this.projectClient = projectClient;
        this.logger = logger;
    }

    public Project getProject(String projectName, String projectVersion) throws DependencyTrackException {
        try {
            Response<List<Project>> response = projectClient.getProjects();

            if (response.isSuccess()) {
                Optional<List<Project>> body = response.getBody();
                if (body.isPresent()) {
                    Optional<Project> project = findProject(body.get(), projectName, projectVersion);

                    if (project.isPresent()) {
                        return project.get();
                    } else {
                        throw new DependencyTrackException(
                                format("Requested project not found: %s-%s", projectName, projectVersion));
                    }
                } else {
                    throw new DependencyTrackException("No projects found on server.");
                }
            } else {
                logger.error("Failed to list projects with error from server: " + response.getStatusText());
                throw new DependencyTrackException("Failed to list projects");
            }
        } catch (UnirestException ex) {
            throw new DependencyTrackException(ex.getMessage(), ex);
        }
    }

    private Optional<Project> findProject(List<Project> projects, String projectName, String projectVersion) {
        return projects.stream()
                .filter(project -> projectName.equals(project.getName()) && projectVersion.equals(project.getVersion()))
                .findFirst();
    }
}
