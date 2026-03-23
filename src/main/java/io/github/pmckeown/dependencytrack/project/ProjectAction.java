package io.github.pmckeown.dependencytrack.project;

import static java.lang.String.format;

import com.networknt.schema.utils.StringUtils;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Item;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.bom.BomParser;
import java.io.File;
import java.util.*;
import kong.unirest.UnirestException;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.di.Named;
import org.apache.maven.api.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@Singleton
public class ProjectAction {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectAction.class);

    private ProjectClient projectClient;
    private BomParser bomParser;

    @Inject
    public ProjectAction(ProjectClient projectClient, BomParser bomParser) {
        this.projectClient = projectClient;
        this.bomParser = bomParser;
    }

    public Project getProject(ModuleConfig moduleConfig) throws DependencyTrackException {
        return getProject(
                moduleConfig.getProjectUuid(), moduleConfig.getProjectName(), moduleConfig.getProjectVersion());
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
                        throw new DependencyTrackException(format("Requested project not found by UUUID: %s", uuid));
                    } else {
                        throw new DependencyTrackException(
                                format("Requested project not found by name/version: %s-%s", name, version));
                    }
                }
            } else {
                LOG.error("Failed to list projects with error from server: {}", response.getStatusText());
                throw new DependencyTrackException("Failed to list projects");
            }
        } catch (UnirestException ex) {
            throw new DependencyTrackException(ex.getMessage(), ex);
        }
    }

    public boolean updateProject(Project project, UpdateRequest updateReq) throws DependencyTrackException {
        return updateProject(project, updateReq, Collections.emptySet());
    }

    public boolean updateProject(Project project, UpdateRequest updateReq, Set<String> projectTags)
            throws DependencyTrackException {
        ProjectInfo info = null;
        if (updateReq.hasBomLocation()) {
            LOG.info("Project info will be updated");
            Optional<ProjectInfo> optInfo = bomParser.getProjectInfo(new File(updateReq.getBomLocation()));
            if (optInfo.isPresent()) {
                info = optInfo.get();
                info.setIsLatest(project.isLatest());
            } else {
                LOG.warn("Could not create ProjectInfo from bom at location: {}", updateReq.getBomLocation());
                return false;
            }
        }
        if (projectTags != null && !projectTags.isEmpty()) {
            if (info == null) {
                info = new ProjectInfo();
            }
            if (project.getTags() != null && !project.getTags().isEmpty()) {
                LOG.info("Merging Project Tags");
                info.setTags(mergeTags(project.getTags(), projectTags));
            } else {
                info.setTags(projectTags.stream().map(ProjectTag::new).toList());
            }
        }

        if (updateReq.hasParent()) {
            LOG.info("Project parent will be updated");
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
                LOG.debug("Project UUID: {}", project.getUuid());
                LOG.debug("Patch request: {}", info);
                Response<Void> response = projectClient.patchProject(project.getUuid(), info);
                LOG.debug("Patch completed without error");
                LOG.debug("Response code: {}", response.getStatus());
                LOG.debug("Success? {}", response.isSuccess());
                return response.isSuccess();
            } catch (UnirestException ex) {
                LOG.error("Failed to update project info", ex);
                throw new DependencyTrackException("Failed to update project", ex);
            }
        }
    }

    public boolean updateRequired(UpdateRequest updateReq) {
        return updateReq.hasBomLocation() || updateReq.hasParent();
    }

    boolean deleteProject(Project project) throws DependencyTrackException {
        try {
            LOG.debug("Deleting project {}-{}", project.getName(), project.getVersion());

            Response<?> response = projectClient.deleteProject(project);
            return response.isSuccess();
        } catch (UnirestException ex) {
            LOG.error("Failed to delete project", ex);
            throw new DependencyTrackException("Failed to delete project", ex);
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
