package io.github.pmckeown.util;

import org.apache.maven.plugin.logging.Log;

import javax.inject.Singleton;

/**
 * Wrapper service around the Maven {@link Log} implementation.  Provides convenient shortcuts to common log methods.
 *
 * @author Paul McKeown
 */
@Singleton
public class Logger {

    private Log log;

    public Logger() {
        // For dependency injection
    }

    public Logger (Log log) {
        // For testing
        this.log = log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    private void assertLogSupplied() {
        if (log == null) {
            throw new IllegalStateException("No Log instance supplied");
        }
    }

    public void info(String template, Object... params) {
        assertLogSupplied();
        if(log.isInfoEnabled()) {
            log.info(String.format(template, params));
        }
    }

    public void warn(String template, Object... params) {
        assertLogSupplied();
        if(log.isWarnEnabled()) {
            log.warn(String.format(template, params));
        }
    }

    public void debug(String template, Object... params) {
        assertLogSupplied();
        if(log.isDebugEnabled()) {
            log.debug(String.format(template, params));
        }
    }

    public void error(String template, Object... params) {
        assertLogSupplied();
        if(log.isErrorEnabled()) {
            log.error(String.format(template, params));
        }
    }
}
