package io.github.pmckeown.dependencytrack;

import javax.inject.Singleton;
import java.time.temporal.ChronoUnit;

@Singleton
public class PollingConfig {

    /*
     * Polling defaults
     */
    private static final boolean POLLING_ENABLED = true;
    private static final int MAX_ATTEMPTS = 20;
    private static final int PAUSE_TIME = 1;
    private static final TimeUnit TIME_UNIT_SECONDS = TimeUnit.SECONDS;

    private boolean enabled;
    private int pause;
    private int attempts;
    private TimeUnit timeUnit;

    public PollingConfig() {
        // Required for Plexus to create and populate a Polling instance
    }

    public PollingConfig(boolean enabled, int pause, int attempts) {
        this(enabled, pause, attempts, TimeUnit.SECONDS);
    }

    public PollingConfig(boolean enabled, int pause, int attempts, TimeUnit timeUnit) {
        this.enabled = enabled;
        this.pause = pause;
        this.attempts = attempts;
        this.timeUnit = timeUnit;
    }

    public static PollingConfig disabled() {
        return new PollingConfig(false, 0, 0, TIME_UNIT_SECONDS);
    }

    public static PollingConfig defaults() {
        return new PollingConfig(POLLING_ENABLED, PAUSE_TIME, MAX_ATTEMPTS, TIME_UNIT_SECONDS);
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

    public ChronoUnit getChronoUnit() {
        if (timeUnit == TimeUnit.MILLIS) {
            return ChronoUnit.MILLIS;
        } else {
            return ChronoUnit.SECONDS;
        }
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public enum TimeUnit {SECONDS, MILLIS}
}
