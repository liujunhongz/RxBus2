package tv.lycam.rxbus;

import tv.lycam.rxbus.annotation.Produce;
import tv.lycam.rxbus.thread.EventThread;

public class StringProducer {
    public static final String VALUE = "Hello, Producer";

    @Produce(
            thread = EventThread.SINGLE
    )
    public String produce() {
        return VALUE;
    }
}
