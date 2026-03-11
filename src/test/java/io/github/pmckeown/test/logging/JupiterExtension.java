package io.github.pmckeown.test.logging;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Hook into JUnit Jupiter to reset you spy loggers around every test.
 */
public class JupiterExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        SpyLoggerRegistry.INSTANCE.resetLoggers();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        SpyLoggerRegistry.INSTANCE.resetLoggers();
    }
}
