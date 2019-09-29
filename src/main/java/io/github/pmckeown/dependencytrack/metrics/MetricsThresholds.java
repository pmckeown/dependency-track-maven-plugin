package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.dependencytrack.Thresholds;

public class MetricsThresholds extends Thresholds {

    public MetricsThresholds() {
        super();
    }

    public MetricsThresholds(Integer critical, Integer high, Integer medium, Integer low) {
        super(critical, high, medium, low);
    }
}
