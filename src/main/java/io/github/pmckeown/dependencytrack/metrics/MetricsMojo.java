package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import org.apache.maven.api.Lifecycle.Phase;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.plugin.annotations.Mojo;
import org.apache.maven.api.plugin.annotations.Parameter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Print the full set of metrics about a project as determined by the Dependency Track Server
 *
 * <p>You can optionally define thresholds for failing the build where the number of issues in a
 * particular category is greater than the threshold you define for that category.
 *
 * <p>For example the following configuration with fail the build if there are any Critical or High
 * issues found in the scan, more than 10 medium issues or more than 20 low issues.
 *
 * <p>&lt;configuration&gt; &lt;metricsThresholds&gt; &lt;critical&gt;0&lt;/critical&gt;
 * &lt;high&gt;0&lt;/high&gt; &lt;medium&gt;10&lt;/medium&gt; &lt;low&gt;20&lt;/low&gt;
 * &lt;unassigned&gt;30&lt;/unassigned&gt; &lt;/metricsThresholds&gt; &lt;/configuration&gt;
 *
 * <p>This allows you to tune build failures to your risk appetite.
 *
 * <p>Specific configuration options are:
 *
 * <ol>
 *   <li>metricsThresholds
 *   <li>
 *       <ol>
 *         <li>critical
 *         <li>high
 *         <li>medium
 *         <li>low
 *         <li>unassigned
 *       </ol>
 * </ol>
 *
 * @author Paul McKeown
 */
@Mojo(name = "metrics", defaultPhase = Phase.VERIFY)
public class MetricsMojo extends AbstractDependencyTrackMojo {
    private static final Logger LOG = LoggerFactory.getLogger(MetricsMojo.class);

    private MetricsAction metricsAction;
    private ProjectAction getProjectAction;
    private MetricsPrinter metricsPrinter;
    private MetricsAnalyser metricsAnalyser;

    @Parameter(name = "metricsThresholds")
    private MetricsThresholds metricsThresholds;

    @Inject
    public MetricsMojo(
            MetricsAction metricsAction,
            ProjectAction getProjectAction,
            MetricsPrinter metricsPrinter,
            MetricsAnalyser metricsAnalyser,
            CommonConfig commonConfig,
            ModuleConfig moduleConfig) {
        super(commonConfig, moduleConfig);
        this.metricsAction = metricsAction;
        this.getProjectAction = getProjectAction;
        this.metricsPrinter = metricsPrinter;
        this.metricsAnalyser = metricsAnalyser;
    }

    @Override
    public void performAction() throws MojoExecutionException, MojoFailureException {
        try {
            Project project = getProjectAction.getProject(moduleConfig);
            LOG.debug("Project Details: {}", project);

            Metrics metrics = getMetrics(project);
            metricsPrinter.print(metrics);

            if (metricsThresholds != null) {
                metricsAnalyser.analyse(metrics, metricsThresholds);
            }
        } catch (DependencyTrackException ex) {
            handleFailure(ex.getMessage(), ex);
        }
    }

    private Metrics getMetrics(Project project) throws DependencyTrackException {
        if (project.getMetrics() != null) {
            return project.getMetrics();
        } else {
            return metricsAction.getMetrics(project);
        }
    }

    void setMetricsThresholds(MetricsThresholds thresholds) {
        this.metricsThresholds = thresholds;
    }
}
