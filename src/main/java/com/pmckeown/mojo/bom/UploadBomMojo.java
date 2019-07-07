package com.pmckeown.mojo.bom;


import com.pmckeown.mojo.AbstractDependencyTrackMojo;
import com.pmckeown.util.BomEncoder;
import com.pmckeown.rest.model.Bom;
import com.pmckeown.rest.model.Response;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Optional;

@Mojo(name = "upload-bom", defaultPhase = LifecyclePhase.VERIFY)
public class UploadBomMojo extends AbstractDependencyTrackMojo {

    @Parameter(required = true, defaultValue = "target/bom.xml")
    private String bomLocation;

    @Parameter(required = true, defaultValue = "${project.artifactId}")
    private String projectName;

    @Parameter(required = true, defaultValue = "${project.version}")
    private String projectVersion;

    private BomEncoder bomEncoder = new BomEncoder();

    public void execute() throws MojoExecutionException {
        info("upload-bom goal started");
        debug("Current working directory: %s", System.getProperty("user.dir"));
        debug("looking for bom.xml at %s", bomLocation);

        Optional<String> encodedBomOptional = bomEncoder.encodeBom(bomLocation);

        boolean uploadFailed = false;

        if (encodedBomOptional.isPresent()) {
            info("Project Name: %s", projectName);
            info("Project Version: %s", projectVersion);

            debug("Base64 Encoded BOM: ", encodedBomOptional.get());

            Bom bom = new Bom(projectName, projectVersion, true, encodedBomOptional.get());
            Response response = dependencyTrackClient().uploadBom(bom);

            if (response.isSuccess()) {
                info("Bom uploaded to Dependency Track server");
            } else {
                uploadFailed = true;
                error("Failure integrating with Dependency Track.");
                error("Status: %d", response.getStatus());
                error("Status Text: %s", response.getStatusText());
            }

        } else {
            uploadFailed = true;
            error("No bom.xml could be located at: %s", bomLocation);
        }

        if (shouldFailOnError() && uploadFailed) {
            throw new MojoExecutionException("Bom upload failed");
        }
    }

    /*
     * Setters for dependency injection in tests
     */
    void setBomLocation(String bomLocation) {
        this.bomLocation = bomLocation;
    }

    void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }

    void setBomEncoder(BomEncoder bomEncoder) {
        this.bomEncoder = bomEncoder;
    }
}
