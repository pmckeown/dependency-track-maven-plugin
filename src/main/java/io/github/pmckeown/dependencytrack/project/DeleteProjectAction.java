package io.github.pmckeown.dependencytrack.project;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Deletes a project from the remote Dependency Track server
 *
 * @author Paul McKeown
 */
@Singleton
public class DeleteProjectAction {

    private GetProjectAction getProjectAction;
    private ProjectClient projectClient;
    private CommonConfig commonConfig;
    private Logger logger;

    @Inject
    public DeleteProjectAction(GetProjectAction getProjectAction, ProjectClient projectClient,
           CommonConfig commonConfig, Logger logger) {
        this.getProjectAction = getProjectAction;
        this.projectClient = projectClient;
        this.commonConfig = commonConfig;
        this.logger = logger;
    }

    boolean deleteProject() throws DependencyTrackException {
        try {
            Project project = getProjectAction.getProject(commonConfig.getProjectName(),
                    commonConfig.getProjectVersion());
            logger.debug("Deleting project %s-%s", project.getName(), project.getVersion());

            Response<?> response = projectClient.deleteProject(project);
            return response.isSuccess();
        } catch(UnirestException ex) {
            logger.error("Failed to delete project", ex);
            throw new DependencyTrackException("Failed to delete project");
        }
    }
}
