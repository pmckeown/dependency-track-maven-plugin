package io.github.pmckeown.dependencytrack.project;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import javax.inject.Inject;

import static java.lang.String.format;

/**
 * Provides the capability to delete a project on the remote Dependency Track Server.
 *
 * @author Paul McKeown
 */
@Mojo(name = "delete-project")
public class DeleteProjectMojo extends AbstractDependencyTrackMojo {

    private DeleteProjectAction deleteProjectAction;

    @Inject
    public DeleteProjectMojo(DeleteProjectAction deleteProjectAction, CommonConfig commonConfig, Logger logger) {
        super(commonConfig, logger);
        this.deleteProjectAction = deleteProjectAction;
    }

    @Override
    protected void performAction() throws MojoExecutionException, MojoFailureException {
        try {
            boolean success = deleteProjectAction.deleteProject();

            if (!success) {
                handleFailure(format("Failed to delete project: %s-%s", commonConfig.getProjectName(),
                        commonConfig.getProjectVersion()));
            }
        } catch (DependencyTrackException ex) {
            handleFailure(format("Exception occurred while trying to delete project: %s-%s",
                    commonConfig.getProjectName(), commonConfig.getProjectVersion()), ex);
        }
    }
}
