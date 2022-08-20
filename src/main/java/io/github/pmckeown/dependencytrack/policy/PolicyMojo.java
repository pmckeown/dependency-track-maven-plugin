package io.github.pmckeown.dependencytrack.policy;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.policy.report.PolicyViolationsReportGenerator;
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
 * Print the policy violations retrieved from the Dependency Track Server after a BOM upload.  This is calculated immediately
 * by the server and as such can be used in situations where you want to know if a change to your application pom.xml
 * has had an impact on the vulnerabilities present in your application.
 *
 * You can optionally define thresholds for failing the build where the number of violations is greater than the threshold
 * you define for that category. Similarly, you can also optionally define policy name, state or its risk type.
 *
 * For example the following configuration with fail the build if there is any Policy violation named "test" found in the
 * scan.
 *
 * &lt;configuration&gt;
 *     &lt;policyConfig&gt;
 *         &lt;polictName&gt;test&lt;/policyName&gt;
 *     &lt;/policyConfig&gt;
 * &lt;/configuration&gt;
 *
 * This allows you to tune build failures to your risk appetite.
 *
 * Specific configuration options are:
 * <ol>
 *     <li>policyConfig</li>
 *     <li>
 *          <ol>
 *              <li>policyName</li>
 *              <li>violationState</li>
 *              <li>riskType</li>
 *              <li>threshold</li>
 *          </ol>
 *     </li>
 * </ol>
 *
 * @author Sahiba Mittal
 */
@Mojo(name = "policy-violations", defaultPhase = VERIFY)
@Singleton
public class PolicyMojo extends AbstractDependencyTrackMojo {

    @Parameter(name = "failOnWarn")
    private boolean failOnWarn;

    @Parameter(defaultValue = "${project}", readonly = true, required = false)
    private MavenProject mavenProject;

    private ProjectAction projectAction;
    private PolicyViolationsReportGenerator policyViolationReportGenerator;
    private PolicyAction policyAction;
    private PolicyViolationsPrinter policyViolationsPrinter;
    private PolicyAnalyser policyAnalyser;

    @Inject
    public PolicyMojo(ProjectAction projectAction, PolicyViolationsReportGenerator policyViolationReportGenerator,
            CommonConfig commonConfig, Logger logger, PolicyAction policyAction,
            PolicyViolationsPrinter policyViolationsPrinter, PolicyAnalyser policyAnalyser) {
        super(commonConfig, logger);
        this.projectAction = projectAction;
        this.policyViolationReportGenerator = policyViolationReportGenerator;
        this.policyAction = policyAction;
        this.policyViolationsPrinter = policyViolationsPrinter;
        this.policyAnalyser = policyAnalyser;
    }

    @Override
    protected void performAction() throws MojoExecutionException, MojoFailureException {
        List<PolicyViolation> policyViolations;
        try {
            Project project = projectAction.getProject(commonConfig.getProjectName(), commonConfig.getProjectVersion());
            policyViolations = policyAction.getPolicyViolations(project);
            policyViolationsPrinter.printPolicyViolations(project, policyViolations);
            boolean policyViolationsBreached = policyAnalyser.isAnyPolicyViolationBreached(policyViolations,
                    failOnWarn);
            policyViolationReportGenerator.generate(getOutputDirectory(), policyViolations);

            if (policyViolationsBreached) {
                throw new MojoFailureException("Policy violations breached");
            }
        } catch (DependencyTrackException ex) {
            handleFailure("Error occurred when getting policy violations", ex);
        }
    }

    private File getOutputDirectory() {
        if (mavenProject == null) {
            return null;
        }
        else {
            return new File(mavenProject.getBuild().getDirectory());
        }
    }

    public void setFailOnWarn(boolean failOnWarn) {
        this.failOnWarn = failOnWarn;
    }
}
