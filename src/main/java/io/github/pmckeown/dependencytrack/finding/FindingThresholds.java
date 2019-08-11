package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.Thresholds;

public class FindingThresholds extends Thresholds {

    public FindingThresholds() {
        super();
    }

    public FindingThresholds(int critical, int high, int medium, int low) {
        super(critical, high, medium, low);
    }
}
