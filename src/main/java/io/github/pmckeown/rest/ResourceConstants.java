package io.github.pmckeown.rest;

/**
 * Resource address constants.
 *
 * @author Paul McKeown
 */
public class ResourceConstants {

    public static final String V1_BOM = "/api/v1/bom";
    public static final String V1_PROJECT = "/api/v1/project";
    public static final String V1_CURRENT_PROJECT_METRICS = "/api/v1/metrics/project/{uuid}/current";

    private ResourceConstants() {
        // Constants file
    }
}
