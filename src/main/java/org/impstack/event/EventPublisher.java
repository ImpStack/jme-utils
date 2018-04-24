package org.impstack.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum EventPublisher {
    INSTANCE;

    private final Logger LOG = LoggerFactory.getLogger(EventPublisher.class);
    private final ConcurrentMap<Class<? extends Event>, List<EventListener>> listeners = new ConcurrentHashMap<>();

    public void addListener(EventListener<? extends Event> listener, Class<? extends Event> event) {
        getListenersForEvent(event).add(listener);
    }

    public void removeListener(EventListener listener, Class<? extends Event> event) {
        getListenersForEvent(event).remove(listener);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void publish(T event) {
        LOG.trace("Publishing {}", event);
        List<EventListener> listeners = getListenersForEvent(event.getClass());
        synchronized (listeners) {
            getListenersForEvent(event.getClass()).forEach(l -> l.onEvent(event));
        }
    }

    private List<EventListener> getListenersForEvent(Class<? extends Event> event) {
        return listeners.computeIfAbsent(event, f -> Collections.synchronizedList(new ArrayList<>()));
    }
}
