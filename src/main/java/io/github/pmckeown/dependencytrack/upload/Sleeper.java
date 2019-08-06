package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.dependencytrack.PollingConfig;

import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * Class for wrapping calls to the {@link TimeUnit} sleep method to allow for testability
 */
@Singleton
@Deprecated
class Sleeper {

    void sleep(int timeout, PollingConfig.TimeUnit timeUnit) throws InterruptedException {
        if (timeUnit == PollingConfig.TimeUnit.MILLIS) {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } else {
            TimeUnit.SECONDS.sleep(timeout);
        }
    }
}
