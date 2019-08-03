package io.github.pmckeown.dependencytrack.metrics;

public class MetricsThresholds {

    private int critical;
    private int high;
    private int medium;
    private int low;

    public MetricsThresholds() {
        // Initialises metric levels to all zero
    }

    public MetricsThresholds(int critical, int high, int medium, int low) {
        this.critical = critical;
        this.high = high;
        this.medium = medium;
        this.low = low;
    }

    public int getCritical() {
        return critical;
    }

    public int getHigh() {
        return high;
    }

    public int getMedium() {
        return medium;
    }

    public int getLow() {
        return low;
    }
}
