package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.List;

import static org.apache.maven.plugins.annotations.LifecyclePhase.VERIFY;

@Mojo(name = "findings", defaultPhase = VERIFY)
@Singleton
public class FindingsMojo extends AbstractDependencyTrackMojo {

    private ProjectAction projectAction;
    private FindingsAction findingsAction;
    private FindingsPrinter findingsPrinter;

    @Inject
    public FindingsMojo(ProjectAction projectAction, FindingsAction findingsAction, FindingsPrinter findingsPrinter,
            CommonConfig commonConfig, Logger logger) {
        super(commonConfig, logger);
        this.projectAction = projectAction;
        this.findingsAction = findingsAction;
        this.findingsPrinter = findingsPrinter;
    }

    @Override
    protected void performAction() throws MojoExecutionException, MojoFailureException {
        try {
            Project project = projectAction.getProject(commonConfig.getProjectName(), commonConfig.getProjectVersion());
            List<Finding> findings = findingsAction.getFindings(project);
            findingsPrinter.printFindings(project, findings);
        } catch (DependencyTrackException ex) {
            logger.error(ex.getMessage());
        }
    }
}
