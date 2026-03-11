package io.github.pmckeown.test.logging;

import static org.mockito.Mockito.spy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Function;
import org.slf4j.Logger;

/**
 * Registry for all the logger spies. Spies are thread local.
 */
public class SpyLoggerRegistry {

    public static Logger expectLogger(Class<?> clazz) {
        return expectLogger(clazz.getName());
    }

    public static Logger expectLogger(String name) {
        return INSTANCE.getSpy(name)
                .orElseThrow(() -> new AssertionError("No logger with the name '%s' was created.".formatted(name)));
    }

    public static final SpyLoggerRegistry INSTANCE = new SpyLoggerRegistry();

    private Map<Thread, Map<String, Logger>> loggers;

    public SpyLoggerRegistry() {
        loggers = Collections.synchronizedMap(new WeakHashMap<>());
    }

    public void resetLoggers() {
        loggers.remove(Thread.currentThread());
    }

    public Optional<Logger> getSpy(String name) {
        return Optional.ofNullable(
                loggers.getOrDefault(Thread.currentThread(), Map.of()).get(name));
    }

    public Logger getLogger(String name, Function<String, Logger> loggerFactory) {
        return getThreadLoggers().computeIfAbsent(name, key -> spy(loggerFactory.apply(name)));
    }

    private Map<String, Logger> getThreadLoggers() {
        return loggers.computeIfAbsent(Thread.currentThread(), key -> new HashMap<>());
    }
}
