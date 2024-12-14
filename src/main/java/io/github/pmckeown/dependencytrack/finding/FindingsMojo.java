package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.finding.report.FindingsReportGenerator;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.List;

import static org.apache.maven.plugins.annotations.LifecyclePhase.VERIFY;

/**
 * Print the findings retrieved from the Dependency Track Server after a BOM upload.  This is calculated immediately
 * by the server and as such can be used in situations where you want to know if a change to your application pom.xml
 * has had an impact on the vulnerabilities present in your application.
 * <p>
 * You can optionally define thresholds for failing the build where the number of issues in a particular category
 * is greater than the threshold you define for that category.
 * <p>
 * For example the following configuration with fail the build if there are any Critical or High issues found in the
 * scan, more than 10 medium issues or more than 20 low issues.
 * <pre>
 * &lt;findingThresholds&gt;
 *     &lt;critical&gt;0&lt;/critical&gt;
 *     &lt;high&gt;0&lt;/high&gt;
 *     &lt;medium&gt;10&lt;/medium&gt;
 *     &lt;low&gt;20&lt;/low&gt;
 *     &lt;unassigned&gt;30&lt;/unassigned&gt;
 * &lt;/findingThresholds&gt;
 * </pre>
 * This allows you to tune build failures to your risk appetite.
 * <p>
 * Specific configuration options are:
 * <ol>
 *     <li>findingThresholds</li>
 *     <li>
 *          <ol>
 *              <li>critical</li>
 *              <li>high</li>
 *              <li>medium</li>
 *              <li>low</li>
 *              <li>unassigned</li>
 *          </ol>
 *     </li>
 * </ol>
 *
 * @author Paul McKeown
 */
@Mojo(name = "findings", defaultPhase = VERIFY)
@Singleton
public class FindingsMojo extends AbstractDependencyTrackMojo {

    @Parameter(name = "findingThresholds")
    private FindingThresholds findingThresholds;

    @Parameter(property = "findingThresholds.critical")
    private Integer thresholdCritical;

    @Parameter(property = "findingThresholds.high")
    private Integer thresholdHigh;

    @Parameter(property = "findingThresholds.medium")
    private Integer thresholdMedium;

    @Parameter(property = "findingThresholds.low")
    private Integer thresholdLow;

    @Parameter(property = "findingThresholds.unassigned")
    private Integer thresholdUnassigned;

    @Parameter(defaultValue = "${project}", readonly = true, required = false)
    private MavenProject mavenProject;

    private ProjectAction projectAction;
    private FindingsAction findingsAction;
    private FindingsPrinter findingsPrinter;
    private FindingsAnalyser findingsAnalyser;
    private FindingsReportGenerator findingsReportGenerator;

    @Inject
    public FindingsMojo(ProjectAction projectAction, FindingsAction findingsAction, FindingsPrinter findingsPrinter,
                        FindingsAnalyser findingsAnalyser, FindingsReportGenerator findingsReportGenerator,
                        CommonConfig commonConfig, Logger logger) {
        super(commonConfig, logger);
        this.projectAction = projectAction;
        this.findingsAction = findingsAction;
        this.findingsPrinter = findingsPrinter;
        this.findingsAnalyser = findingsAnalyser;
        this.findingsReportGenerator = findingsReportGenerator;
    }

    @Override
    protected void performAction() throws MojoExecutionException, MojoFailureException {
        List<Finding> findings;
        try {
            Project project = projectAction.getProject(commonConfig);
            findings = findingsAction.getFindings(project);
            findingsPrinter.printFindings(project, findings);
            populateThresholdFromCliOptions();

            boolean policyBreached = findingsAnalyser.doNumberOfFindingsBreachPolicy(findings, this.findingThresholds);
            findingsReportGenerator.generate(getOutputDirectory(), findings, this.findingThresholds, policyBreached);

            if (policyBreached) {
                throw new MojoFailureException("Number of findings exceeded defined thresholds");
            }
        } catch (DependencyTrackException ex) {
            handleFailure("Error occurred when getting findings", ex);
        }
    }

    /**
     * If Maven options are provided on the command line and the {@link FindingThresholds} is not already populated
     * from XML configuration, populate the {@link FindingThresholds} from those options.
     */
    void populateThresholdFromCliOptions() {
        if (this.findingThresholds == null && (this.thresholdCritical != null || this.thresholdHigh != null ||
                this.thresholdMedium != null || this.thresholdLow != null || this.thresholdUnassigned != null)) {
            this.findingThresholds = new FindingThresholds(
                    this.thresholdCritical, this.thresholdHigh, this.thresholdMedium,
                    this.thresholdLow, this.thresholdUnassigned);
        }
    }

    /*
     * For testing
     */
    void setFindingThresholds(FindingThresholds findingThresholds) {
        this.findingThresholds = findingThresholds;
    }

    FindingThresholds getFindingThresholds() {
        return this.findingThresholds;
    }

    void setThresholdCritical(Integer thresholdCritical) {
        this.thresholdCritical = thresholdCritical;
    }

    void setThresholdHigh(Integer thresholdHigh) {
        this.thresholdHigh = thresholdHigh;
    }

    void setThresholdMedium(Integer thresholdMedium) {
        this.thresholdMedium = thresholdMedium;
    }

    void setThresholdLow(Integer thresholdLow) {
        this.thresholdLow = thresholdLow;
    }

    void setThresholdUnassigned(Integer thresholdUnassigned) {
        this.thresholdUnassigned = thresholdUnassigned;
    }

    private File getOutputDirectory() {
        if (mavenProject == null) {
            return null;
        } else {
            return new File(mavenProject.getBuild().getDirectory());
        }
    }
}
