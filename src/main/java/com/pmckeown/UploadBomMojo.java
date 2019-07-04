package com.pmckeown;


import com.pmckeown.rest.model.Bom;
import com.pmckeown.rest.model.Response;
import com.pmckeown.util.BomUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Optional;

@Mojo(name = "upload-bom", defaultPhase = LifecyclePhase.VERIFY)
public class UploadBomMojo extends AbstractDependencyTrackMojo {

    @Parameter(required = true)
    private String bomLocation;

    @Parameter(required = true, defaultValue = "${project.artifactId}")
    private String projectName;

    @Parameter(required = true, defaultValue = "${project.version}")
    private String projectVersion;

    public void execute() throws MojoExecutionException {
        info("upload-bom goal started");
        debug("Current working directory: %s", System.getProperty("user.dir"));
        debug("looking for bom.xml at %s", bomLocation);

        Optional<String> base64EncodedBomOptional = BomUtils.getBase64EncodedBom(bomLocation, getLog());

        if (base64EncodedBomOptional.isPresent()) {
            info("Project Name: %s", projectName);
            info("Project Version: %s", projectVersion);

            debug("Base64 Encoded BOM: ", base64EncodedBomOptional.get());

            Bom bom = new Bom(projectName, projectVersion, true, base64EncodedBomOptional.get());
            Response response = dependencyTrackClient().uploadBom(bom);

            if (response.isSuccess()) {
                info("Bom uploaded to Dependency Track server");

            } else {
                error("Failure integrating with Dependency Track.");
                error("Status: %d", response.getStatus());
                error("Status Text: %s", response.getStatusText());
            }

        } else {
            error("No bom.xml could be located at: %s", bomLocation);
        }
    }

    void setBomLocation(String bomLocation) {
        this.bomLocation = bomLocation;
    }

    void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }
}
