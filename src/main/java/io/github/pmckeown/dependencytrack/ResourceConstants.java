package io.github.pmckeown.dependencytrack;

/**
 * Resource address constants.
 *
 * @author Paul McKeown
 */
public final class ResourceConstants {

    public static final String V1_BOM = "/api/v1/bom";
    public static final String V1_BOM_TOKEN_UUID = "/api/v1/bom/token/{uuid}";
    public static final String V1_PROJECT = "/api/v1/project?limit=1000000&offset=0";
    public static final String V1_PROJECT_UUID = "/api/v1/project/{uuid}";
    public static final String V1_FINDING_PROJECT_UUID = "/api/v1/finding/project/{uuid}";
    public static final String V1_METRICS_PROJECT_UUID_CURRENT = "/api/v1/metrics/project/{uuid}/current";
    public static final String V1_METRICS_PROJECT_UUID_REFRESH = "/api/v1/metrics/project/{uuid}/refresh";
    public static final String V1_POLICY_VIOLATION_PROJECT_UUID = "/api/v1/violation/project/{uuid}";

    private ResourceConstants() {
        // Constants file
    }
}
