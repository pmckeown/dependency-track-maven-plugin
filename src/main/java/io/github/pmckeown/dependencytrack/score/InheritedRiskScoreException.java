package io.github.pmckeown.dependencytrack.score;

import io.github.pmckeown.dependencytrack.DependencyTrackMojoException;

public class InheritedRiskScoreException extends DependencyTrackMojoException {
    public InheritedRiskScoreException(String shortMessage, String longMessage) {
        super(null, shortMessage, longMessage);
    }
}
