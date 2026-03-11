package io.github.pmckeown.dependencytrack.policyviolation;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.project.Project;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import kong.unirest.UnirestException;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PolicyViolationsAction {
    private static final Logger LOG = LoggerFactory.getLogger(PolicyViolationsAction.class);

    private PolicyViolationsClient policyClient;

    @Inject
    public PolicyViolationsAction(PolicyViolationsClient policyClient) {
        this.policyClient = policyClient;
    }

    public List<PolicyViolation> getPolicyViolations(Project project) throws DependencyTrackException {
        LOG.info("Getting policy violations for project {}-{}", project.getName(), project.getVersion());

        try {
            Response<List<PolicyViolation>> response = policyClient.getPolicyViolationsForProject(project);
            Optional<List<PolicyViolation>> body = response.getBody();
            if (response.isSuccess()) {
                if (body.isPresent()) {
                    return body.get();
                } else {
                    LOG.info(
                            "No policy violations available for project {}-{}",
                            project.getName(),
                            project.getVersion());
                    return Collections.emptyList();
                }
            } else {
                throw new DependencyTrackException("Error received from server");
            }
        } catch (UnirestException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DependencyTrackException(ex.getMessage(), ex);
        }
    }
}
