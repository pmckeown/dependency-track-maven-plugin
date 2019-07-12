package io.github.pmckeown.dependencytrack.upload;


import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

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

    @Parameter(required = true, defaultValue = "target/bom.xml")
    private String bomLocation;

    private UploadBomAction uploadBomAction;

    public UploadBomMojo() {
        this.uploadBomAction = new UploadBomAction();
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if (!uploadBomAction.upload(new UploadBomConfig(commonConfig(), bomLocation), log)) {
                handleFailure("Bom upload failed");
            }
        } catch (DependencyTrackException ex) {
            handleFailure("Error occurred during upload", ex);
        }
    }

//    private void uploadBom(Bom bom) throws MojoFailureException {
//        try {
//            Response response = dependencyTrackClient().uploadBom(bom);
//            log.debug(response.toString());
//
//            if (response.isSuccess()) {
//                log.debug("Bom uploaded to Dependency Track server");
//            } else {
//                handleFailure(format("Failure integrating with Dependency Track: %d %s", response.getStatus(),
//                        response.getStatusText()));
//            }
//
//        } catch (UnirestException ex) {
//            log.debug(ex.getMessage());
//            handleFailure("Bom upload failed");
//        }
//    }
//
    /*
     * Setters for dependency injection in tests
     */
    void setBomLocation(String bomLocation) {
        this.bomLocation = bomLocation;
    }

    void setUploadBomAction(UploadBomAction uploadBomAction) {
        this.uploadBomAction = uploadBomAction;
    }
}
