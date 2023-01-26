package io.github.pmckeown.dependencytrack.project;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Item;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.bom.BomParser;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Singleton
public class ProjectAction {

    private ProjectClient projectClient;

    private BomParser bomParser;
    private Logger logger;

    @Inject
    public ProjectAction(ProjectClient projectClient, BomParser bomParser, Logger logger) {
        this.projectClient = projectClient;
        this.bomParser = bomParser;
        this.logger = logger;
    }

    public Project getProject(String projectName, String projectVersion) throws DependencyTrackException {
        try {
            Response<List<Project>> response = projectClient.getProjects();

            if (response.isSuccess()) {
                Optional<List<Project>> body = response.getBody();
                if (body.isPresent()) {
                    Optional<Project> project = findProject(body.get(), projectName, projectVersion);

                    if (project.isPresent()) {
                        return project.get();
                    } else {
                        throw new DependencyTrackException(
                                format("Requested project not found: %s-%s", projectName, projectVersion));
                    }
                } else {
                    throw new DependencyTrackException("No projects found on server.");
                }
            } else {
                logger.error("Failed to list projects with error from server: " + response.getStatusText());
                throw new DependencyTrackException("Failed to list projects");
            }
        } catch (UnirestException ex) {
            throw new DependencyTrackException(ex.getMessage(), ex);
        }
    }

    public boolean updateProject(Project project, UpdateRequest updateReq) throws DependencyTrackException {
        ProjectInfo info = null;
        if (updateReq.hasBomLocation()) {
            logger.info("Project info will be updated");
            Optional<ProjectInfo> optInfo = bomParser.getProjectInfo(new File(updateReq.getBomLocation()));
            if (optInfo.isPresent()) {
                info = optInfo.get();
            } else {
                logger.warn("Could not create ProjectInfo from bom at location: %s", updateReq.getBomLocation());
                return false;
            }
        }

        if (updateReq.hasParent()) {
            logger.info("Project parent will be updated");
            if (info == null) {
                info = new ProjectInfo();
            }

            info.setParent(new Item(updateReq.getParent().getUuid()));
        }

        if (info == null) {
            // No-op
            return true;
        } else {
            try {
                logger.debug("Project UUID: %s", project.getUuid());
                logger.debug("Patch request: %s", info);
                Response<Void> response = projectClient.patchProject(project.getUuid(), info);
                return response.isSuccess();
            } catch (UnirestException ex) {
                logger.error("Failed to update project info", ex);
                throw new DependencyTrackException("Failed to update project");
            }
        }
    }

    public boolean updateRequired(UpdateRequest updateReq) {
        return updateReq.hasBomLocation() || updateReq.hasParent();
    }

    boolean deleteProject(Project project) throws DependencyTrackException {
        try {
            logger.debug("Deleting project %s-%s", project.getName(), project.getVersion());

            Response<?> response = projectClient.deleteProject(project);
            return response.isSuccess();
        } catch(UnirestException ex) {
            logger.error("Failed to delete project", ex);
            throw new DependencyTrackException("Failed to delete project");
        }
    }

    private Optional<Project> findProject(List<Project> projects, String projectName, String projectVersion) {
        // The project version may be null from the Dependency-Track server
        return projects.stream()
                .filter(project -> projectName.equals(project.getName()) && StringUtils.equals(projectVersion, project.getVersion()))
                .findFirst();
    }
}
