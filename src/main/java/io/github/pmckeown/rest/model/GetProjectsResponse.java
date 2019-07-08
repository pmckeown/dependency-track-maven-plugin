package io.github.pmckeown.rest.model;

import java.util.List;

/**
 * Wrapper for the Get Projects API response from the Dependency Track server
 *
 * @author Paul McKeown
 */
public class GetProjectsResponse extends Response {

    private final List<Project> projects;

    public GetProjectsResponse(int status, String statusText, boolean success, List<Project> projects) {
        super(status, statusText, success);
        this.projects = projects;
    }

    public List<Project> getBody() {
        return projects;
    }
}
