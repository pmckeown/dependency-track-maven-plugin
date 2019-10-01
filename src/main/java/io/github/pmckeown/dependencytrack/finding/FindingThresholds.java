package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.Thresholds;

public class FindingThresholds extends Thresholds {

    public FindingThresholds() {
        super();
    }

    public FindingThresholds(Integer critical, Integer high, Integer medium, Integer low, Integer unassigned) {
        super(critical, high, medium, low, unassigned);
    }
}
