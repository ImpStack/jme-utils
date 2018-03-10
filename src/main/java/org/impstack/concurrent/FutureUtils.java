package org.impstack.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.Future;

/**
 * @author remy
 * @since 14/07/17
 */
public final class FutureUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FutureUtils.class);

    public static <T> boolean isDone(Future<T> future) {
        return future != null && future.isDone();
    }

    public static <T> T silentGet(Future<T> future) {
        try {
            return future.get();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> Optional<T> get(Future<T> future) {
        try {
            return Optional.ofNullable(future.get());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return Optional.empty();
    }
}
