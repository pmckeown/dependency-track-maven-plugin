package io.github.pmckeown.dependencytrack.project;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.DependencyTrackMojoException;

import org.apache.maven.api.di.Inject;
import org.apache.maven.api.plugin.annotations.Mojo;

/**
 * Provides the capability to delete a project on the remote Dependency Track Server.
 *
 * @author Paul McKeown
 */
@Mojo(name = "delete-project")
public class DeleteProjectMojo extends AbstractDependencyTrackMojo {

    @Inject
    private ProjectAction projectAction;

    @Override
    protected void performAction() throws DependencyTrackMojoException {
        try {
            Project project = projectAction.getProject(moduleConfig);

            boolean success = projectAction.deleteProject(project);

            if (!success) {
                handleFailure("Failed to delete project: %s-%s"
                        .formatted(moduleConfig.getProjectName(), moduleConfig.getProjectVersion()));
            }
        } catch (DependencyTrackException ex) {
            handleFailure("Exception occurred while trying to delete project: %s-%s"
                    .formatted(moduleConfig.getProjectName(), moduleConfig.getProjectVersion()),
                    ex);
        }
    }
}
