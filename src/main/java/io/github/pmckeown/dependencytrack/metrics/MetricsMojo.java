package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.DependencyTrackMojo;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import javax.inject.Inject;

@Mojo(name = "metrics", defaultPhase = LifecyclePhase.VERIFY)
public class MetricsMojo extends DependencyTrackMojo {

    private MetricsAction metricsAction;

    @Inject
    public MetricsMojo(MetricsAction metricsAction, CommonConfig commonConfig, Logger logger) {
        super(commonConfig, logger);
        this.metricsAction = metricsAction;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();
        try {
            Metrics metrics = metricsAction.getMetrics(new Project("4b37f262-9f0f-4986-9293-f36c55e3f708",
                    "hardcoded-test", "0.0.1", null));
            logger.info(metrics.toString());
        } catch (DependencyTrackException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
