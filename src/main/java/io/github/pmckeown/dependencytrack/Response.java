package io.github.pmckeown.dependencytrack;

import java.util.Optional;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Wrapper for API responses from the Dependency Track server
 *
 * @param <B> Body type
 * @author Paul McKeown
 */
public class Response<B> {

    private final int status;
    private final String statusText;
    private boolean success;
    private final Optional<B> body;

    public Response(int status, String statusText, boolean success) {
        this.status = status;
        this.statusText = statusText;
        this.success = success;
        this.body = Optional.empty();
    }

    public Response(int status, String statusText, boolean success, Optional<B> body) {
        this.status = status;
        this.statusText = statusText;
        this.success = success;
        this.body = body;
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

    public Optional<B> getBody() {
        return this.body;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
