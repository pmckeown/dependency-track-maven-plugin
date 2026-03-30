package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.DependencyTrackMojoException;

public class FindingsPolicyBreachedException extends DependencyTrackMojoException {

    public FindingsPolicyBreachedException(String shortMessage, String longMessage) {
        super(null, shortMessage, longMessage);
    }

}
