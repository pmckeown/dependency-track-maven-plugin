package io.github.pmckeown.dependencytrack.suppressions;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.util.Logger;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Provides the capability to post vulnerability suppressions to your Dependency Track server.
 *
 * @author Thomas Hucke
 */
@Mojo(name = "suppressions", defaultPhase = LifecyclePhase.VERIFY)
public class SuppressionsMojo extends AbstractDependencyTrackMojo {

    @Parameter(required = true)
    private Suppressions suppressions = new Suppressions(Collections.emptyList());

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

    @SuppressWarnings("unused")
    void setSuppressions(Suppressions suppressions) {
        this.suppressions = suppressions;
    }

    @Override
    public void performAction() throws MojoExecutionException, MojoFailureException {
        logger.info("Performing suppression action");
        if (suppressions.getVulnerabilitySuppressions().stream().anyMatch(vulnerabilitySuppressionValidator::isInValidVulnerabilitySuppression)) {
            handleFailure("Maven vulnerability suppression configuration is invalid");
        }
        this.moduleConfig.setVulnerabilitySuppressions(suppressions.getVulnerabilitySuppressions());

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

            if (!suppressionsAction.setProjectSuppressions(analysisList)) {
                handleFailure("Setting suppressions failed");
            }
        } catch (DependencyTrackException ex) {
            handleFailure("Error occurred in configuring suppressions", ex);
        }
    }

    public Suppressions getSuppressions() {
        return suppressions;
    }
}
