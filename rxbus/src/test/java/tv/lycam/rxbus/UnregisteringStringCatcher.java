package tv.lycam.rxbus;

import java.util.ArrayList;
import java.util.List;

import tv.lycam.rxbus.annotation.Subscribe;
import tv.lycam.rxbus.thread.EventThread;

/**
 * An SubscriberEvent mock that records a String and unregisters itself in the subscriber.
 */
public class UnregisteringStringCatcher {
    private final Bus bus;

    private List<String> events = new ArrayList<String>();

    public UnregisteringStringCatcher(Bus bus) {
        this.bus = bus;
    }

    @Subscribe(
            thread = EventThread.SINGLE
    )
    public void unregisterOnString(String event) {
        bus.unregister(this);
        events.add(event);
    }

    @Subscribe(
            thread = EventThread.SINGLE
    )
    public void zzzSleepinOnStrings(String event) {
        events.add(event);
    }

    @Subscribe(
            thread = EventThread.SINGLE
    )
    public void haveAnInteger(Integer event) {
    }

    @Subscribe(
            thread = EventThread.SINGLE
    )
    public void enjoyThisLong(Long event) {
    }

    @Subscribe(
            thread = EventThread.SINGLE
    )
    public void perhapsATastyDouble(Double event) {
    }

    public List<String> getEvents() {
        return events;
    }
}
