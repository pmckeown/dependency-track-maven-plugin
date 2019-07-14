package io.github.pmckeown.util;

import org.apache.maven.plugin.logging.Log;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Wrapper service around the Maven {@link Log} implementation.  Provides convenient shortcuts to common log methods.
 *
 * @author Paul McKeown
 */
@Named
@Singleton
public class Logger {

    private Log log;

    public Logger(Log log) {
        this.log = log;
    }

    public void info(String template, Object... params) {
        if(log.isInfoEnabled()) {
            log.info(String.format(template, params));
        }
    }

    public void warn(String template, Object... params) {
        if(log.isWarnEnabled()) {
            log.warn(String.format(template, params));
        }
    }

    public void debug(String template, Object... params) {
        if(log.isDebugEnabled()) {
            log.debug(String.format(template, params));
        }
    }

    public void error(String template, Object... params) {
        if(log.isErrorEnabled()) {
            log.error(String.format(template, params));
        }
    }
}
