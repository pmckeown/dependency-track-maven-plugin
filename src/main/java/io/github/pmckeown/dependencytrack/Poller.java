package io.github.pmckeown.dependencytrack;

import com.evanlennick.retry4j.CallExecutorBuilder;
import com.evanlennick.retry4j.Status;
import com.evanlennick.retry4j.config.RetryConfig;
import com.evanlennick.retry4j.config.RetryConfigBuilder;
import com.evanlennick.retry4j.exception.UnexpectedException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Class for polling a remote server.
 *
 * Polling will continue until the provide callable returns a populated {@link Optional<T>} instance.
 *
 * {@link Optional#empty()} will continue the loop until the configured retry limit is me when a
 * {@link com.evanlennick.retry4j.exception.RetriesExhaustedException} will be thrown.
 *
 * Any {@link Exception} encountered will stop the loop and be thrown as an
 * {@link com.evanlennick.retry4j.exception.UnexpectedException}
 *
 * @param <T> The expected return type.  This will be wrapped in an {@link Optional}
 */
@Singleton
public class Poller<T> {

    private PollingConfig pollingConfig;

    @Inject
    public Poller(PollingConfig pollingConfig) {
        this.pollingConfig = pollingConfig;
    }

    /**
     * Execute the supplied {@link Callable} until a non-empty Optional value is returned from the Callable.
     *
     * @param callable The {@link Callable} function to execute one or more times
     * @return An {@link Optional} wrapping the result of the callable
     * @throws UnexpectedException if an Exception occurs while polling
     */
    @SuppressWarnings("unchecked")
    public Optional<T> poll(Callable<Optional<T>> callable) throws UnexpectedException {
        Status<Optional<T>> status = new CallExecutorBuilder<Optional<T>>()
                .config(getRetryConfig())
                .build()
                .execute(callable);

        return status.getResult();
    }

    private RetryConfig getRetryConfig() {
        return new RetryConfigBuilder()
                .withMaxNumberOfTries(pollingConfig.isEnabled() ? pollingConfig.getAttempts() : 1)
                .withDelayBetweenTries(pollingConfig.getPause(), pollingConfig.getChronoUnit())
                .retryOnReturnValue(Optional.empty())
                .withFixedBackoff()
                .failOnAnyException()
                .build();
    }
}
