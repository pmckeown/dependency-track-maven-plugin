package io.github.pmckeown.dependencytrack.policyviolation;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.policyviolation.report.PolicyViolationsReportGenerator;
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
 * Print the policy violations retrieved from the Dependency Track Server after a BOM upload.  This is calculated
 * immediately by the server and as such can be used in situations where you want to know if a change to your
 * application pom.xml has breached a Policy defined on the Dependency Track server.
 *
 * The build will fail if any Policies are breached that are configured with a violation state of FAIL.
 *
 * The build will pass if any Policies are breached that are configured with a violation state of INFO.
 *
 * The build will pass if any Policies are breached that are configured with a violation state of WARN unless the
 * `failOnWarn` option is supplied.
 *
 * This allows you to tune build failures to your risk appetite.
 *
 * @author Sahiba Mittal
 */
@Mojo(name = "policy-violations", defaultPhase = VERIFY)
@Singleton
public class PolicyViolationsMojo extends AbstractDependencyTrackMojo {

    @Parameter(name = "failOnWarn")
    private boolean failOnWarn;

    @Parameter(defaultValue = "${project}", readonly = true, required = false)
    private MavenProject mavenProject;

    private ProjectAction projectAction;
    private PolicyViolationsReportGenerator policyViolationReportGenerator;
    private PolicyAction policyAction;
    private PolicyViolationsPrinter policyViolationsPrinter;
    private PolicyViolationsAnalyser policyAnalyser;

    @Inject
    public PolicyViolationsMojo(ProjectAction projectAction, PolicyViolationsReportGenerator policyViolationReportGenerator,
            CommonConfig commonConfig, Logger logger, PolicyAction policyAction,
            PolicyViolationsPrinter policyViolationsPrinter, PolicyViolationsAnalyser policyAnalyser) {
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
