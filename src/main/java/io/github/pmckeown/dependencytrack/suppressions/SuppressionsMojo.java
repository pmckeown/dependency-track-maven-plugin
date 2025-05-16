package io.github.pmckeown.dependencytrack.suppressions;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.util.Logger;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Provides the capability to upload a Bill of Material (BOM) to your Dependency Track server.
 * <p>
 * The BOM may any format supported by your Dependency Track server, has only been tested with the output from the
 * <a href="https://github.com/CycloneDX/cyclonedx-maven-plugin">cyclonedx-maven-plugin</a> in the
 * <a href="https://cyclonedx.org/">CycloneDX</a> format
 * <p>
 * Specific configuration options are:
 * <ol>
 *     <li>bomLocation</li>
 * </ol>
 *
 * @author Paul McKeown
 */
@Mojo(name = "suppressions", defaultPhase = LifecyclePhase.VERIFY)
public class SuppressionsMojo extends AbstractDependencyTrackMojo {

    @Parameter(property = "dependency-track.suppressions.vulnerabilities")
    private Set<VulnerabilitySuppression> vulnerabilitySuppressions;

    private final FindingsProcessor findingsProcessor;

    private final SuppressionsAction suppressionsAction;

    private final ProjectAction projectAction;

    private final VulnerabilitySuppressionValidator vulnerabilitySuppressionValidator;

    @Inject
    public SuppressionsMojo(SuppressionsAction suppressionsAction, CommonConfig commonConfig, ModuleConfig moduleConfig,
        Logger logger, FindingsProcessor findingsProcessor, ProjectAction projectAction,
        VulnerabilitySuppressionValidator vulnerabilitySuppressionValidator) {
        super(commonConfig, moduleConfig, logger);
        this.suppressionsAction = suppressionsAction;
        this.findingsProcessor = findingsProcessor;
        this.projectAction = projectAction;
        this.vulnerabilitySuppressionValidator = vulnerabilitySuppressionValidator;
    }

    @Override
    public void performAction() throws MojoExecutionException, MojoFailureException {

        if (vulnerabilitySuppressions.stream().anyMatch(vulnerabilitySuppressionValidator::isInValidVulnerabilitySuppression)) {
            handleFailure("Maven vulnerability suppression configuration is invalid");
        }
        this.moduleConfig.setVulnerabilitySuppressions(vulnerabilitySuppressions);

        /*
        Concept
            1. get all findings for the project incl. suppressed ones
            2. iterate through the findings and
                2.1 check if the finding is NOT suppressed and found in the list of suppressions ==> add suppression
                2.2 check if the finding is suppressed and NOT found in the list of suppressions ==> remove suppression
            A vulnerability of a finding is classified by the attribute vulnerability and its attributes source and vulnId.
            If a vulnerability is suppressed, it is classified by the attribute "analysis" and its attribute "isSuppressed"
                and the classifier attribute "state".
            A vulnerabilitySuppression is identified by its source and vulnId.
        Change suppression
            A suppression is done via an API call to the Dependency Track server api/v1/analysis endpoint.
            Needed parameters are:
                - "project": project uuid
                - "component": component uuid (taken from finding.getComponent().getUuid())
                - "vulnerability": vulnerability uuid (taken from finding.getVulnerability().getUuid())
                - Adding a suppression, following attributes are needed additionally:
                    "analysisDetails": "Automatically ignore violation in branch",
                    "analysisState": Configured value "NOT_AFFECTED","FALSE_POSITIVE","IN_TRIAGE","EXPLOITABLE" (Default: "IN_TRIAGE")
                    "analysisJustification": "PROTECTED_BY_MITIGATING_CONTROL",
                    "analysisResponse": "NOT_SET",
                    "isSuppressed": true,
                    "suppressed": true
                - Remove a suppression, following attributes are needed additionally:
                    "analysisDetails": "Automatically re-activate violation alert in branch",
                    "analysisState": "NOT_SET",
                    "analysisJustification": "PROTECTED_BY_MITIGATING_CONTROL",
                    "analysisResponse": "NOT_SET",
                    "isSuppressed": true,
                    "suppressed": true

         */
        try {
            Project project = projectAction.getProject(moduleConfig);

            // fetch all findings for the project incl. suppressed ones
            List<Analysis> analysisList = findingsProcessor.process(project, moduleConfig);

            if (!suppressionsAction.setProjectSuppressions(project, analysisList)) {
                handleFailure("Setting suppressions failed");
            }
        } catch (DependencyTrackException ex) {
            handleFailure("Error occurred in configuring suppressions", ex);
        }
    }

    /*
     * Setters for dependency injection in tests
     */
    void setvulnerabilitySuppressions(Set<VulnerabilitySuppression> vulnerabilitySuppressions) {
        moduleConfig.setVulnerabilitySuppressions(vulnerabilitySuppressions);
        this.vulnerabilitySuppressions = vulnerabilitySuppressions;
    }
}
