package io.github.pmckeown.dependencytrack;

public class DependencyTrackException extends Exception {

    public DependencyTrackException(String message) {
        super(message);
    }

    public DependencyTrackException(String message, Throwable cause) {
        super(message, cause);
    }

    public DependencyTrackException(Throwable cause) {
        super(cause);
    }

    public DependencyTrackException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
