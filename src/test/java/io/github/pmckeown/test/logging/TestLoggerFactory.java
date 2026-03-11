package io.github.pmckeown.test.logging;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class TestLoggerFactory implements ILoggerFactory {

    private final SpyLoggerRegistry registry;
    private final Function<String, Logger> realLoggerFactory;
    private final Map<String, Logger> loggers;

    public TestLoggerFactory(SpyLoggerRegistry registry, Function<String, Logger> realLoggerFactory) {
        this.registry = registry;
        this.realLoggerFactory = realLoggerFactory;
        loggers = new ConcurrentHashMap<>();
    }

    @Override
    public Logger getLogger(String name) {
        return loggers.computeIfAbsent(name, key -> new TestLogger(key, registry, realLoggerFactory));
    }
}
