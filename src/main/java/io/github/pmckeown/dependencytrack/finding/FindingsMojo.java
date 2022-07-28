package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.finding.report.FindingsReportGenerator;
import io.github.pmckeown.dependencytrack.policy.PolicyAction;
import io.github.pmckeown.dependencytrack.policy.PolicyViolation;
import io.github.pmckeown.dependencytrack.policy.PolicyViolationsPrinter;
import io.github.pmckeown.dependencytrack.policy.PolicyConfig;
import io.github.pmckeown.dependencytrack.policy.PolicyAnalyser;
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
 *
 * You can optionally define thresholds for failing the build where the number of issues in a particular category
 * is greater than the threshold you define for that category.
 *
 * For example the following configuration with fail the build if there are any Critical or High issues found in the
 * scan, more than 10 medium issues or more than 20 low issues.
 *
 * &lt;configuration&gt;
 *     &lt;findingThresholds&gt;
 *         &lt;critical&gt;0&lt;/critical&gt;
 *         &lt;high&gt;0&lt;/high&gt;
 *         &lt;medium&gt;10&lt;/medium&gt;
 *         &lt;low&gt;20&lt;/low&gt;
 *         &lt;unassigned&gt;30&lt;/unassigned&gt;
 *     &lt;/findingThresholds&gt;
 * &lt;/configuration&gt;
 *
 * This allows you to tune build failures to your risk appetite.
 *
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

    @Parameter(name = "policyConfig")
    private PolicyConfig policyConfig;

    @Parameter(defaultValue = "${project}", readonly = true, required = false)
    private MavenProject mavenProject;

    private ProjectAction projectAction;
    private FindingsAction findingsAction;
    private FindingsPrinter findingsPrinter;
    private FindingsAnalyser findingsAnalyser;
    private FindingsReportGenerator findingsReportGenerator;
    private PolicyAction policyAction;
    private PolicyViolationsPrinter policyViolationsPrinter;
    private PolicyAnalyser policyAnalyser;

    @Inject
    public FindingsMojo(ProjectAction projectAction, FindingsAction findingsAction, FindingsPrinter findingsPrinter,
                        FindingsAnalyser findingsAnalyser, FindingsReportGenerator findingsReportGenerator,
                        CommonConfig commonConfig, Logger logger, PolicyAction policyAction,
                        PolicyViolationsPrinter policyViolationsPrinter, PolicyAnalyser policyAnalyser) {
        super(commonConfig, logger);
        this.projectAction = projectAction;
        this.findingsAction = findingsAction;
        this.findingsPrinter = findingsPrinter;
        this.findingsAnalyser = findingsAnalyser;
        this.findingsReportGenerator = findingsReportGenerator;
        this.policyAction = policyAction;
        this.policyViolationsPrinter = policyViolationsPrinter;
        this.policyAnalyser = policyAnalyser;
    }

    @Override
    protected void performAction() throws MojoExecutionException, MojoFailureException {
        List<Finding> findings;
        List<PolicyViolation> policyViolations;
        try {
            Project project = projectAction.getProject(commonConfig.getProjectName(), commonConfig.getProjectVersion());
            findings = findingsAction.getFindings(project);
            policyViolations = policyAction.getPolicyViolations(project);
            findingsPrinter.printFindings(project, findings);
            policyViolationsPrinter.printPolicyViolations(project, policyViolations);
            boolean policyBreached = findingsAnalyser.doNumberOfFindingsBreachPolicy(findings, findingThresholds);
            boolean policyViolationBreached = policyAnalyser.isAnyPolicyViolationBreached(policyViolations, policyConfig);
            findingsReportGenerator.generate(getOutputDirectory(), findings, findingThresholds, policyBreached, policyViolations);

            if (policyBreached) {
                throw new MojoFailureException("Number of findings exceeded defined thresholds");
            }
            if(policyViolationBreached) {
                throw new MojoFailureException("Policy violations breached");
            }
        } catch (DependencyTrackException ex) {
            handleFailure("Error occurred when getting findings", ex);
        }
    }

    /*
     * For testing
     */
    void setFindingThresholds(FindingThresholds findingThresholds) {
        this.findingThresholds = findingThresholds;
    }

    private File getOutputDirectory() {
        if (mavenProject == null) {
            return null;
        }
        else {
            return new File(mavenProject.getBuild().getDirectory());
        }
    }
}
