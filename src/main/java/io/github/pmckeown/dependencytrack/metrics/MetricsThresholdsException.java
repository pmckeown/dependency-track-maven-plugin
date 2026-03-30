package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.dependencytrack.DependencyTrackMojoException;

public class MetricsThresholdsException extends DependencyTrackMojoException {
    public MetricsThresholdsException(String shortMessage, String longMessage) {
        super(null, shortMessage, longMessage);
    }
}
