package io.github.pmckeown.dependencytrack;

import org.apache.maven.api.plugin.MojoException;

/**
 * Generic exception thrown by the DependencyTrack Mojo executions.
 */
public class DependencyTrackMojoException extends MojoException {

    public DependencyTrackMojoException(String message) {
        super(message);
    }

    public DependencyTrackMojoException(String message, Throwable cause) {
        super(message, cause);
    }

    public DependencyTrackMojoException(Object source, String shortMessage, String longMessage) {
        super(source, shortMessage, longMessage);
    }
}
