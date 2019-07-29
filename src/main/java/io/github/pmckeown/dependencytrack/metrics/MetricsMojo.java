package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import javax.inject.Inject;

@Mojo(name = "metrics", defaultPhase = LifecyclePhase.VERIFY)
public class MetricsMojo extends AbstractDependencyTrackMojo {

    private MetricsAction metricsAction;
    private ProjectAction projectAction;
    private MetricsPrinter metricsPrinter;

    @Inject
    public MetricsMojo(MetricsAction metricsAction, ProjectAction projectAction, MetricsPrinter metricsPrinter,
            CommonConfig commonConfig, Logger logger) {
        super(commonConfig, logger);
        this.metricsAction = metricsAction;
        this.projectAction = projectAction;
        this.metricsPrinter = metricsPrinter;
    }

    @Override
    public void performAction() throws MojoExecutionException, MojoFailureException {
        try {
            Project project = projectAction.getProject(commonConfig.getProjectName(), commonConfig.getProjectVersion());
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
