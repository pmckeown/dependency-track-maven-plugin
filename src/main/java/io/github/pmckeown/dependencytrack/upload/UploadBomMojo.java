package io.github.pmckeown.dependencytrack.upload;


import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.util.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

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

    @Parameter(required = false)
    private String bomLocation;

    @Parameter(property = "project", readonly = true, required = true)
    private MavenProject project;

    private UploadBomAction uploadBomAction;

    @Inject
    public UploadBomMojo(UploadBomAction uploadBomAction, CommonConfig commonConfig, Logger logger) {
        super(commonConfig, logger);
        this.uploadBomAction = uploadBomAction;
    }

    @Override
    public void performAction() throws MojoExecutionException, MojoFailureException {
        try {
            if (!uploadBomAction.upload(getBomLocation())) {
                handleFailure("Bom upload failed");
            }
        } catch (DependencyTrackException ex) {
            handleFailure("Error occurred during upload", ex);
        }
    }

    private String getBomLocation() {
        if (StringUtils.isNotBlank(bomLocation)) {
            return bomLocation;
        } else {
            String defaultLocation = project.getBasedir() + "/target/bom.xml";
            logger.debug("bomLocation not supplied so using: %s", defaultLocation);
            return defaultLocation;
        }
    }

    /*
     * Setters for dependency injection in tests
     */
    void setBomLocation(String bomLocation) {
        this.bomLocation = bomLocation;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }
}
