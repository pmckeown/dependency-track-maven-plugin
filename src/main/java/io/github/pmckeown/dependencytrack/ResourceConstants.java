package io.github.pmckeown.dependencytrack;

/**
 * Resource address constants.
 *
 * @author Paul McKeown
 */
public class ResourceConstants {

    public static final String V1_BOM = "/api/v1/bom";
    public static final String V1_BOM_TOKEN_UUID = "/api/v1/bom/token/{uuid}";
    public static final String V1_PROJECT = "/api/v1/project";
    public static final String V1_PROJECT_UUID = "/api/v1/project/{uuid}";
    public static final String V1_FINDING_PROJECT_UUID = "/api/v1/finding/project/{uuid}";
    public static final String V1_METRICS_PROJECT_UUID_CURRENT = "/api/v1/metrics/project/{uuid}/current";
    public static final String V1_METRICS_PROJECT_UUID_REFRESH = "/api/v1/metrics/project/{uuid}/refresh";

    private ResourceConstants() {
        // Constants file
    }
}
