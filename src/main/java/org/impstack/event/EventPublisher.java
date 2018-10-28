package org.impstack.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A thread-safe eventbus implementation. Listeners can subscribe to receive specific events.
 * An event should implement the {@link Event} interface, an event listener should implement the {@link EventListener}
 * interface.
 */
public enum EventPublisher {
    INSTANCE;

    private final Logger LOG = LoggerFactory.getLogger(EventPublisher.class);
    private final ConcurrentMap<Class<? extends Event>, List<EventListener>> listeners = new ConcurrentHashMap<>();

    public void addListener(EventListener<? extends Event> listener, Class<? extends Event> event) {
        getListenersForEvent(event).add(listener);
    }

    public void addListener(EventListener<? extends Event> listener, Class<? extends Event>... events) {
        Arrays.stream(events).forEach(event -> addListener(listener, event));
    }

    public void removeListener(EventListener listener, Class<? extends Event> event) {
        getListenersForEvent(event).remove(listener);
    }

    public void removeListener(EventListener listener, Class<? extends Event>... events) {
        Arrays.stream(events).forEach(event -> removeListener(listener, event));
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void publish(T event) {
        LOG.trace("Publishing {}", event);
        List<EventListener> listeners = getListenersForEvent(event.getClass());
        synchronized (listeners) {
            listeners.forEach(l -> l.onEvent(event));
        }
    }

    private List<EventListener> getListenersForEvent(Class<? extends Event> event) {
        return listeners.computeIfAbsent(event, f -> Collections.synchronizedList(new ArrayList<>()));
    }
}
