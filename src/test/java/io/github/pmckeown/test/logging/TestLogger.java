package io.github.pmckeown.test.logging;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.event.SubstituteLoggingEvent;
import org.slf4j.helpers.SubstituteLogger;

public class TestLogger extends SubstituteLogger {
    private static final Queue<SubstituteLoggingEvent> DUMMY_QUEUE = new LinkedBlockingQueue<>();

    private final SpyLoggerRegistry registry;
    private final Function<String, Logger> realLoggerFactory;

    public TestLogger(String name, SpyLoggerRegistry registry, Function<String, Logger> realLoggerFactory) {
        super(name, DUMMY_QUEUE, true);
        this.registry = registry;
        this.realLoggerFactory = realLoggerFactory;
    }

    @Override
    public Logger delegate() {
        return registry.getLogger(getName(), realLoggerFactory);
    }
}
