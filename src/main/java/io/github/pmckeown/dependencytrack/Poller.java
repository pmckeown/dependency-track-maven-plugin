package io.github.pmckeown.dependencytrack;

import com.evanlennick.retry4j.CallExecutorBuilder;
import com.evanlennick.retry4j.Status;
import com.evanlennick.retry4j.config.RetryConfigBuilder;
import com.evanlennick.retry4j.exception.UnexpectedException;

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

    /**
     * Execute the supplied {@link Callable} until a non-empty Optional value is returned from the Callable.
     *
     * @param callable The {@link Callable} function to execute one or more times
     * @return An {@link Optional} wrapping the result of the callable
     * @throws UnexpectedException if an Exception occurs while polling
     */
    @SuppressWarnings("unchecked")
    public Optional<T> poll(PollingConfig pollingConfig, Callable<Optional<T>> callable) {
        Status<Optional<T>> status = new CallExecutorBuilder<Optional<T>>()
                .config(new RetryConfigBuilder()
                        .withMaxNumberOfTries(pollingConfig.isEnabled() ? pollingConfig.getAttempts() : 1)
                        .withDelayBetweenTries(pollingConfig.getPause(), pollingConfig.getChronoUnit())
                        .retryOnReturnValue(Optional.empty())
                        .withFixedBackoff()
                        .failOnAnyException()
                        .build())
                .build()
                .execute(callable);

        return status.getResult();
    }

    /**
     * Execute the supplied {@link Callable} while the retry value is returned from the Callable.
     *
     * @param retryValue Keep retrying if one of these is returned
     * @param callable The {@link Callable} function to execute one or more times
     * @return An {@link Optional} wrapping the result of the callable
     * @throws UnexpectedException if an Exception occurs while polling
     */
    @SuppressWarnings("unchecked")
    public T poll(PollingConfig pollingConfig, Object retryValue, Callable<T> callable) {
        Status<T> status = new CallExecutorBuilder<T>()
                .config(new RetryConfigBuilder()
                        .withMaxNumberOfTries(pollingConfig.isEnabled() ? pollingConfig.getAttempts() : 1)
                        .withDelayBetweenTries(pollingConfig.getPause(), pollingConfig.getChronoUnit())
                        .retryOnReturnValue(retryValue)
                        .withFixedBackoff()
                        .failOnAnyException()
                        .build())
                .build()
                .execute(callable);

        return status.getResult();
    }
}
