package io.github.pmckeown.dependencytrack.upload;

import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * Class for wrapping calls to the {@link TimeUnit} sleep method to allow for testability
 */
@Singleton
class Sleeper {

    void sleep(int seconds) throws InterruptedException {
        TimeUnit.SECONDS.sleep(seconds);
    }
}
