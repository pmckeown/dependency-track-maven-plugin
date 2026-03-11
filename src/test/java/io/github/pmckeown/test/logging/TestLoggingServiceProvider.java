package io.github.pmckeown.test.logging;

import org.apache.maven.slf4j.MavenServiceProvider;
import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

/**
 * SLF4JServiceProvider which mostly wraps Maven's provider, we just register a logger factory which creates spies for all of Maven's standard loggers.
 */
public class TestLoggingServiceProvider implements SLF4JServiceProvider {

    private final MavenServiceProvider mavenServiceProvider;
    private final TestLoggerFactory loggerFactory;

    public TestLoggingServiceProvider() {
        mavenServiceProvider = new MavenServiceProvider();
        loggerFactory =
                new TestLoggerFactory(SpyLoggerRegistry.INSTANCE, mavenServiceProvider.getLoggerFactory()::getLogger);
    }

    @Override
    public ILoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    @Override
    public IMarkerFactory getMarkerFactory() {
        return mavenServiceProvider.getMarkerFactory();
    }

    @Override
    public MDCAdapter getMDCAdapter() {
        return mavenServiceProvider.getMDCAdapter();
    }

    @Override
    public String getRequestedApiVersion() {
        return mavenServiceProvider.getRequestedApiVersion();
    }

    @Override
    public void initialize() {
        mavenServiceProvider.initialize();
    }
}
