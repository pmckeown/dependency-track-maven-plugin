package io.github.pmckeown.dependencytrack.upload;


import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.metrics.MetricsAction;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.dependencytrack.project.ProjectInfo;
import io.github.pmckeown.util.Logger;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

import javax.inject.Inject;

/**
 * Provides the capability to upload a Bill of Material (BOM) to your Dependency Track server.
 *
 * The BOM may any format supported by your Dependency Track server, has only been tested with the output from the
 * <a href="https://github.com/CycloneDX/cyclonedx-maven-plugin">cyclonedx-maven-plugin</a> in the
 * <a href="https://cyclonedx.org/">CycloneDX</a> format
 *
 * Specific configuration options are:
 * <ol>
 *     <li>bomLocation</li>
 * </ol>
 *
 * @author Paul McKeown
 */
@Mojo(name = "upload-bom", defaultPhase = LifecyclePhase.VERIFY)
public class UploadBomMojo extends AbstractDependencyTrackMojo {

    @Parameter(property = "dependency-track.bomLocation")
    private String bomLocation;

    @Parameter(property = "project", readonly = true, required = true)
    private MavenProject mavenProject;
    
    @Parameter(property = "dependency-track.updateProjectInfo")
    private boolean updateProjectInfo;

    private UploadBomAction uploadBomAction;

    private MetricsAction metricsAction;

    private ProjectAction projectAction;

    @Inject
    public UploadBomMojo(UploadBomAction uploadBomAction, MetricsAction metricsAction, ProjectAction projectAction,
             CommonConfig commonConfig, Logger logger) {
        super(commonConfig, logger);
        this.uploadBomAction = uploadBomAction;
        this.metricsAction = metricsAction;
        this.projectAction = projectAction;
    }

    @Override
    public void performAction() throws MojoExecutionException, MojoFailureException {
        try {
            if (!uploadBomAction.upload(getBomLocation())) {
                handleFailure("Bom upload failed");
            }
            Project project = projectAction.getProject(projectName, projectVersion);
            if (updateProjectInfo) {
                Optional<ProjectInfo> info = createProjectInfo();
                if (info.isPresent()) {
                    logger.info("Updating project info");
                    if (!projectAction.updateProjectInfo(project, info.get())) {
                        logger.info("Failed to update project info");
                    }
                }
            }
            metricsAction.refreshMetrics(project);
        } catch (DependencyTrackException ex) {
            handleFailure("Error occurred during upload", ex);
        }
    }

    private String getBomLocation() {
        if (StringUtils.isNotBlank(bomLocation)) {
            return bomLocation;
        } else {
            String defaultLocation = mavenProject.getBasedir() + "/target/bom.xml";
            logger.debug("bomLocation not supplied so using: %s", defaultLocation);
            return defaultLocation;
        }
    }

    Optional<ProjectInfo> createProjectInfo() {
        Xpp3Dom dom;
        try {
            dom = Xpp3DomBuilder.build(new FileReader(getBomLocation()));
        } catch (XmlPullParserException | IOException e) {
            logger.warn("Failed to update project info. Failure processing bom.", e);
            return Optional.empty();
        }
        Xpp3Dom component = getMetadataComponent(dom, "metadata", "component");
        if (component == null) {
            logger.debug("BOM does not contain a metadata/component element");
            return Optional.empty();
        }
        ProjectInfo info = new ProjectInfo();
        String type = component.getAttribute("type");
        if (type != null) {
            info.setClassifier(type);
        }
        setProjectInfoValue(component, "publisher", info::setPublisher);
        setProjectInfoValue(component, "description", info::setDescription);
        setProjectInfoValue(component, "group", info::setGroup);
        setProjectInfoValue(component, "group", info::setGroup);
        setProjectInfoValue(component, "purl", info::setPurl);
        setProjectInfoValue(component, "cpe", info::setCpe);
        setProjectInfoValue(component, "swid", info::setSwidTagId);
        return Optional.of(info);
    }

    private Xpp3Dom getMetadataComponent(Xpp3Dom dom, String... path) {
        for (String segment : path) {
            if (dom == null) {
                break;
            }
            dom = dom.getChild(segment);
        }
        return dom;
    }

    private void setProjectInfoValue(Xpp3Dom component, String fieldName, Consumer<String> setter) {
        Xpp3Dom field = component.getChild(fieldName);
        if (field != null) {
            setter.accept(field.getValue());
        }
    }

    /*
     * Setters for dependency injection in tests
     */
    void setBomLocation(String bomLocation) {
        this.bomLocation = bomLocation;
    }

    void setMavenProject(MavenProject mp) {
        this.mavenProject = mp;
    }

}
