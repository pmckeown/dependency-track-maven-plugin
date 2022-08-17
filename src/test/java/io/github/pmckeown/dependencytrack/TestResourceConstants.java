package io.github.pmckeown.dependencytrack;

public class TestResourceConstants {

    public static final String V1_METRICS_PROJECT_CURRENT = "/api/v1/metrics/project/(.*)/current";
    public static final String V1_METRICS_PROJECT_REFRESH = "/api/v1/metrics/project/(.*)/refresh";
    public static final String V1_PROJECT_UUID = "/api/v1/project/(.*)";
    public static final String V1_PROJECT_WITH_ONE_MILLION_LIMIT = "/api/v1/project?limit=1000000&offset=0";
    public static final String V1_BOM_TOKEN_UUID = "/api/v1/bom/token/(.*)";
    public static final String V1_FINDING_PROJECT_UUID = "/api/v1/finding/project/(.*)";
    public static final String V1_POLICY_VIOLATION_PROJECT_UUID = "/api/v1/violation/project/(.*)";

    private TestResourceConstants() {
        // Utility Class
    }
}