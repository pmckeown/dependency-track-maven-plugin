package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.DependencyTrackMojo;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import javax.inject.Inject;

@Mojo(name = "metrics", defaultPhase = LifecyclePhase.VERIFY)
public class MetricsMojo extends DependencyTrackMojo {

    private MetricsAction metricsAction;
    private ProjectAction projectAction;

    @Inject
    public MetricsMojo(MetricsAction metricsAction, ProjectAction projectAction, CommonConfig commonConfig,
           Logger logger) {
        super(commonConfig, logger);
        this.metricsAction = metricsAction;
        this.projectAction = projectAction;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();
        try {
            Project project = projectAction.getProject(commonConfig.getProjectName(), commonConfig.getProjectVersion());
            logger.info(project.toString());

            Metrics metrics = project.getMetrics();
            if (metrics == null) {
                metrics = metricsAction.getMetrics(project);
            }
            logger.info(metrics.toString());
        } catch (DependencyTrackException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
