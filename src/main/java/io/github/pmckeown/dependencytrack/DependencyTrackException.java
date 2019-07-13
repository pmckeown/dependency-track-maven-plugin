package io.github.pmckeown.dependencytrack;

/**
 * Exception class for wrapping exceptions when integrating with Dependency Track
 */
public class DependencyTrackException extends Exception {

    public DependencyTrackException(String message) {
        super(message);
    }

    public DependencyTrackException(String message, Throwable cause) {
        super(message, cause);
    }

}
