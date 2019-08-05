package io.github.pmckeown.dependencytrack;

public class PollingConfig {

    /*
     * Polling defaults
     */
    private static final boolean POLLING_ENABLED = true;
    private static final int MAX_ATTEMPTS = 20;
    private static final int PAUSE_IN_SECONDS = 1;

    private boolean enabled;
    private int pause;
    private int attempts;

    public PollingConfig() {
        // Required for Plexus to create and populate a Polling instance
    }

    public PollingConfig(boolean enabled, int pause, int attempts) {
        this.enabled = enabled;
        this.pause = pause;
        this.attempts = attempts;
    }

    public static PollingConfig defaults() {
        return new PollingConfig(POLLING_ENABLED, PAUSE_IN_SECONDS, MAX_ATTEMPTS);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getPause() {
        return pause;
    }

    public int getAttempts() {
        return attempts;
    }
}
