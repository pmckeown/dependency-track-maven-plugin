package io.github.pmckeown.dependencytrack.project;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.metrics.Metrics;
import io.github.pmckeown.dependencytrack.metrics.MetricsAction;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import javax.inject.Inject;

@Mojo(name = "metrics", defaultPhase = LifecyclePhase.VERIFY)
public class MetricsMojo extends AbstractDependencyTrackMojo {

    private MetricsAction metricsAction;
    private Logger logger;

    @Inject
    public MetricsMojo(MetricsAction metricsAction, Logger logger) {
        this.metricsAction = metricsAction;
        this.logger = logger;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Metrics metrics = metricsAction.getMetrics(commonConfig(), logger, new Project(
                    "4b37f262-9f0f-4986-9293-f36c55e3f708", "hardcoded-test", "0.0.1", null));
            logger.info(metrics.toString());
        } catch (DependencyTrackException e) {
            throw new MojoExecutionException("Boom", e);
        }
    }
}
