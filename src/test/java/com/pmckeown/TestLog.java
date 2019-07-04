package com.pmckeown;

import org.apache.maven.plugin.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic {@link Log} Test Double to provide as a dependency where needed.
 *
 * Prints and captures log contents for debugging and verification.
 *
 * TODO - Delete this and refactor the BomUtils to make it testable
 */
public class TestLog implements Log {

    private List<String> logs = new ArrayList<>();

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void debug(CharSequence content) {
        System.out.println(content.toString());
        logs.add(content.toString());
    }

    @Override
    public void debug(CharSequence content, Throwable error) {
        System.out.println(content);
        error.printStackTrace();
        logs.add(content.toString());
    }

    @Override
    public void debug(Throwable error) {
        error.printStackTrace();
        logs.add(error.getMessage());
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(CharSequence content) {
        System.out.println(content);
        logs.add(content.toString());
    }

    @Override
    public void info(CharSequence content, Throwable error) {
        System.out.println(content);
        error.printStackTrace();
        logs.add(content.toString());
    }

    @Override
    public void info(Throwable error) {
        error.printStackTrace();
        logs.add(error.getMessage());
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(CharSequence content) {
        System.out.println(content);
        logs.add(content.toString());
    }

    @Override
    public void warn(CharSequence content, Throwable error) {
        System.out.println(content);
        error.printStackTrace();
        logs.add(content.toString());
    }

    @Override
    public void warn(Throwable error) {
        error.printStackTrace();
        logs.add(error.getMessage());
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(CharSequence content) {
        System.out.println(content);
        logs.add(content.toString());
    }

    @Override
    public void error(CharSequence content, Throwable error) {
        System.out.println(content);
        error.printStackTrace();
        logs.add(content.toString());
    }

    @Override
    public void error(Throwable error) {
        error.printStackTrace();
        logs.add(error.getMessage());
    }

    public List<String> getLogs() {
        return logs;
    }
}
