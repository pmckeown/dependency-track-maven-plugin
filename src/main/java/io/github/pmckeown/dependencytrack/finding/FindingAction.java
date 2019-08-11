package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class FindingAction {

    private FindingClient findingClient;

    private Logger logger;

    @Inject
    public FindingAction(FindingClient findingClient, Logger logger) {
        this.findingClient = findingClient;
        this.logger = logger;
    }

    List<Finding> getFindings(Project project) throws DependencyTrackException {
        logger.info("Getting findings for project %s-%s", project.getName(), project.getVersion());

        try {
            Response<List<Finding>> response = findingClient.getFindingsForProject(project);
            Optional<List<Finding>> body = response.getBody();
            if (body.isPresent()) {
                return body.get();
            } else {
                logger.info("No findings available for project %s-%s", project.getName(), project.getVersion());
                throw new DependencyTrackException("No findings for project");
            }
        } catch (UnirestException ex) {
            logger.error(ex.getMessage());
            throw new DependencyTrackException(ex.getMessage());
        }
    }
}
