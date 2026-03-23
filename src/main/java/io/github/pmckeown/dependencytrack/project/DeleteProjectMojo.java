package io.github.pmckeown.dependencytrack.project;

import static java.lang.String.format;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.plugin.annotations.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

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
    protected void performAction() throws MojoExecutionException, MojoFailureException {
        try {
            Project project = projectAction.getProject(moduleConfig);

            boolean success = projectAction.deleteProject(project);

            if (!success) {
                handleFailure(format(
                        "Failed to delete project: %s-%s",
                        moduleConfig.getProjectName(), moduleConfig.getProjectVersion()));
            }
        } catch (DependencyTrackException ex) {
            handleFailure(
                    format(
                            "Exception occurred while trying to delete project: %s-%s",
                            moduleConfig.getProjectName(), moduleConfig.getProjectVersion()),
                    ex);
        }
    }
}
