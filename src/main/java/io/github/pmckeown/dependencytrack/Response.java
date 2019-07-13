package io.github.pmckeown.dependencytrack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Wrapper for __files from the Dependency Track server
 *
 * @author Paul McKeown
 */
public class Response {

    private final int status;
    private final String statusText;
    private boolean success;

    public Response(int status, String statusText, boolean success) {
        this.status = status;
        this.statusText = statusText;
        this.success = success;
    }

    public int getStatus() {
        return this.status;
    }

    public String getStatusText() {
        return this.statusText;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
