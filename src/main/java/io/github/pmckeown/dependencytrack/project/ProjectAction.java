package io.github.pmckeown.dependencytrack.project;

import com.networknt.schema.utils.StringUtils;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Item;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.bom.BomParser;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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

    public Project getProject(ModuleConfig moduleConfig) throws DependencyTrackException {
        return getProject(
                moduleConfig.getProjectUuid(),
                moduleConfig.getProjectName(),
                moduleConfig.getProjectVersion());
    }

    public Project getProject(String uuid) throws DependencyTrackException {
        return getProject(uuid, "", "");
    }

    public Project getProject(String name, String version) throws DependencyTrackException {
        return getProject("", name, version);
    }

    public Project getProject(String uuid, String name, String version) throws DependencyTrackException {
        try {
            Response<Project> response = projectClient.getProject(uuid, name, version);

            if (response.isSuccess()) {
                Optional<Project> body = response.getBody();
                if (body.isPresent()) {
                    return body.get();
                } else {
                    if (StringUtils.isBlank(uuid)) {
                        throw new DependencyTrackException(
                                format("Requested project not found by UUUID: %s", uuid)
                        );
                    } else {
                        throw new DependencyTrackException(
                                format("Requested project not found by name/version: %s-%s", name, version)
                        );
                    }
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
        return updateProject(project, updateReq, Collections.emptySet());
    }

    public boolean updateProject(Project project, UpdateRequest updateReq, Set<String> projectTags) throws DependencyTrackException {
        ProjectInfo info = null;
        if (updateReq.hasBomLocation()) {
            logger.info("Project info will be updated");
            Optional<ProjectInfo> optInfo = bomParser.getProjectInfo(new File(updateReq.getBomLocation()));
            if (optInfo.isPresent()) {
                info = optInfo.get();
                info.setIsLatest(Boolean.valueOf(project.isLatest()));
            } else {
                logger.warn("Could not create ProjectInfo from bom at location: %s", updateReq.getBomLocation());
                return false;
            }
        }
        if (projectTags != null && !projectTags.isEmpty()) {
            if (info == null) {
                info = new ProjectInfo();
            }
            if (project.getTags() != null && !project.getTags().isEmpty()) {
                logger.info("Merging Project Tags");
                info.setTags(mergeTags(project.getTags(), projectTags));
            } else {
                info.setTags(projectTags.stream().map(ProjectTag::new).collect(Collectors.toList()));
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
                logger.debug("Patch completed without error");
                logger.debug("Response code: %s", response.getStatus());
                logger.debug("Success? %s", response.isSuccess());
                return response.isSuccess();
            } catch (UnirestException ex) {
                logger.error("Failed to update project info", ex);
                throw new DependencyTrackException("Failed to update project", ex);
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
        } catch (UnirestException ex) {
            logger.error("Failed to delete project", ex);
            throw new DependencyTrackException("Failed to delete project");
        }
    }

    private List<ProjectTag> mergeTags(List<ProjectTag> existingTags, Set<String> mavenTags) {
        List<ProjectTag> projectTags = new LinkedList<>(existingTags);
        for (String mavenTag : mavenTags) {
            boolean exists = false;
            for (ProjectTag projectTag : projectTags) {
                if (projectTag.getName().equals(mavenTag)) {
                    exists = true;
                }
            }
            if (!exists) {
                projectTags.add(new ProjectTag(mavenTag));
            }
        }
        return projectTags;
    }
}
