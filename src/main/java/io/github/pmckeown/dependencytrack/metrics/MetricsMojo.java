package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.GetProjectAction;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import javax.inject.Inject;

/**
 * Provides the capability to print the full set of metrics about a project as determined by the Dependency Track Server
 *
 * @author Paul McKeown
 */
@Mojo(name = "metrics", defaultPhase = LifecyclePhase.VERIFY)
public class MetricsMojo extends AbstractDependencyTrackMojo {

    private MetricsAction metricsAction;
    private GetProjectAction getProjectAction;
    private MetricsPrinter metricsPrinter;

    @Inject
    public MetricsMojo(MetricsAction metricsAction, GetProjectAction getProjectAction, MetricsPrinter metricsPrinter,
                       CommonConfig commonConfig, Logger logger) {
        super(commonConfig, logger);
        this.metricsAction = metricsAction;
        this.getProjectAction = getProjectAction;
        this.metricsPrinter = metricsPrinter;
    }

    @Override
    public void performAction() throws MojoExecutionException, MojoFailureException {
        try {
            Project project = getProjectAction.getProject(commonConfig.getProjectName(), commonConfig.getProjectVersion());
            logger.debug("Project Details: %s", project.toString());

            final Metrics projectMetrics = project.getMetrics();
            if (projectMetrics != null) {
                metricsPrinter.print(projectMetrics);
            } else {
                metricsPrinter.print(metricsAction.getMetrics(project));
            }
        } catch (DependencyTrackException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
