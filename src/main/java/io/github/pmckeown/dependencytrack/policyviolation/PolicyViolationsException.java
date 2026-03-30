package io.github.pmckeown.dependencytrack.policyviolation;

import io.github.pmckeown.dependencytrack.DependencyTrackMojoException;

public class PolicyViolationsException extends DependencyTrackMojoException {
    public PolicyViolationsException(String shortMessage, String longMessage) {
        super(null, shortMessage, longMessage);
    }
}
