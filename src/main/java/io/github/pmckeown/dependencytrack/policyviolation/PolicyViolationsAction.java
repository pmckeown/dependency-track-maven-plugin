package io.github.pmckeown.dependencytrack.policyviolation;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import kong.unirest.UnirestException;

@Singleton
public class PolicyViolationsAction {

    private PolicyViolationsClient policyClient;

    private Logger logger;

    @Inject
    public PolicyViolationsAction(PolicyViolationsClient policyClient, Logger logger) {
        this.policyClient = policyClient;
        this.logger = logger;
    }

    public List<PolicyViolation> getPolicyViolations(Project project) throws DependencyTrackException {
        logger.info("Getting policy violations for project %s-%s", project.getName(), project.getVersion());

        try {
            Response<List<PolicyViolation>> response = policyClient.getPolicyViolationsForProject(project);
            Optional<List<PolicyViolation>> body = response.getBody();
            if (response.isSuccess()) {
                if (body.isPresent()) {
                    return body.get();
                } else {
                    logger.info(
                            "No policy violations available for project %s-%s",
                            project.getName(), project.getVersion());
                    return Collections.emptyList();
                }
            } else {
                throw new DependencyTrackException("Error received from server");
            }
        } catch (UnirestException ex) {
            logger.error(ex.getMessage());
            throw new DependencyTrackException(ex.getMessage());
        }
    }
}
