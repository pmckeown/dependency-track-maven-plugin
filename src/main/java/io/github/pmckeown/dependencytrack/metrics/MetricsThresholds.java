package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.dependencytrack.Thresholds;

public class MetricsThresholds extends Thresholds {

    public MetricsThresholds() {
        super();
    }

    public MetricsThresholds(int critical, int high, int medium, int low) {
        super(critical, high, medium, low);
    }
}
