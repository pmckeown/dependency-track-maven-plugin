package io.github.pmckeown.dependencytrack.project;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;
import org.cyclonedx.BomParserFactory;
import org.cyclonedx.exception.ParseException;
import org.cyclonedx.model.Bom;
import org.cyclonedx.model.Component;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Singleton
public class ProjectAction {

    private ProjectClient projectClient;
    private Logger logger;

    @Inject
    public ProjectAction(ProjectClient projectClient, Logger logger) {
        this.projectClient = projectClient;
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
    
    public boolean updateProjectInfo(Project project, String bomLocation) throws DependencyTrackException {
        Optional<ProjectInfo> info = createProjectInfo(new File(bomLocation));
        if (info.isPresent()) {
            try {
                Response<?> response = projectClient.patchProject(project.getUuid(), info.get());
                return response.isSuccess();
            } catch (UnirestException ex) {
                logger.error("Failed to update project info", ex);
                throw new DependencyTrackException("Failed to update project info");
            }
        } else {
            logger.warn("Could not create ProjectInfo from bom at location: %s", bomLocation);
        }

        return false;
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
        return projects.stream()
                .filter(project -> projectName.equals(project.getName()) && projectVersion.equals(project.getVersion()))
                .findFirst();
    }

     Optional<ProjectInfo> createProjectInfo(File bomFile) {
        if (!bomFile.canRead()) {
            return Optional.empty();
        }
        Bom bom;
        try {
            bom = BomParserFactory.createParser(bomFile).parse(bomFile);
        }
        catch (ParseException e) {
            logger.warn("Failed to update project info. Failure processing bom.", e);
            return Optional.empty();
        }
        if (bom.getMetadata() == null || bom.getMetadata().getComponent() == null) {
            return Optional.empty();
        }

        Component component =  bom.getMetadata().getComponent();
        ProjectInfo info = new ProjectInfo();
        if (component.getType() != null) {
            info.setClassifier(component.getType().name());
        }
        info.setAuthor(component.getAuthor());
        info.setPublisher(component.getPublisher());
        info.setDescription(component.getDescription());
        info.setGroup(component.getGroup());
        info.setPurl(component.getPurl());
        info.setCpe(component.getCpe());
        if (component.getSwid() != null) {
            info.setSwidTagId(component.getSwid().getTagId());
        }
        return Optional.of(info);
    }
}
