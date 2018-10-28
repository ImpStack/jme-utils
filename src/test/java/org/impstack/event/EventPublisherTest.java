package org.impstack.event;

public class EventPublisherTest {

    public static void main(String[] args) {
        EventPublisherTest test = new EventPublisherTest();
        test.publishEvent();
        test.receiveMultipleEvents();
        test.receiveMultipleEventsMultiThreaded();
    }

    private void receiveMultipleEventsMultiThreaded() {
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(this::receiveMultipleEvents);
            t.start();
        }
    }

    public void publishEvent() {
        EventListener<MyEvent> listener = new MyListener();
        Event event = new MyEvent();

        EventPublisher.INSTANCE.addListener(listener, MyEvent.class);
        EventPublisher.INSTANCE.publish(event);
        EventPublisher.INSTANCE.removeListener(listener, MyEvent.class);
    }

    public void receiveMultipleEvents() {
        EventListener genericListener = new GenericListener();
        EventListener<MyEvent> specificListener = new MyListener();
        Event event = new MyEvent();
        Event anotherEvent = new AnotherEvent();

        EventPublisher.INSTANCE.addListener(genericListener, MyEvent.class, AnotherEvent.class);
        EventPublisher.INSTANCE.addListener(specificListener, MyEvent.class);
        EventPublisher.INSTANCE.publish(event);
        EventPublisher.INSTANCE.publish(anotherEvent);
        EventPublisher.INSTANCE.removeListener(genericListener, MyEvent.class);
        EventPublisher.INSTANCE.removeListener(genericListener, MyEvent.class, AnotherEvent.class);
    }

    private class MyEvent implements Event {
        @Override
        public String toString() {
            return "MyEvent{}";
        }
    }

    private class AnotherEvent implements Event {
        @Override
        public String toString() {
            return "AnotherEvent{}";
        }
    }

    private class MyListener implements EventListener<MyEvent> {
        @Override
        public void onEvent(MyEvent event) {
            System.out.println("My listener received: " + event);
        }
    }

    private class GenericListener implements EventListener {
        @Override
        public void onEvent(Event event) {
            System.out.println("Generic listener received: " + event);
        }
    }

}
