package io.github.pmckeown.dependencytrack.finding;

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
public class FindingsAction {
    private static final Logger LOG = LoggerFactory.getLogger(FindingsAction.class);

    private FindingsClient findingClient;

    @Inject
    public FindingsAction(FindingsClient findingClient) {
        this.findingClient = findingClient;
    }

    List<Finding> getFindings(Project project) throws DependencyTrackException {
        LOG.info("Getting findings for project {}-{}", project.getName(), project.getVersion());

        try {
            Response<List<Finding>> response = findingClient.getFindingsForProject(project);
            Optional<List<Finding>> body = response.getBody();
            if (response.isSuccess()) {
                if (body.isPresent()) {
                    return body.get();
                } else {
                    LOG.info("No findings available for project {}-{}", project.getName(), project.getVersion());
                    return Collections.emptyList();
                }
            } else {
                throw new DependencyTrackException("Error received from server");
            }
        } catch (UnirestException e) {
            LOG.error("Unirest failure. {}", e.getMessage(), e);
            throw new DependencyTrackException(e.getMessage(), e);
        }
    }
}
