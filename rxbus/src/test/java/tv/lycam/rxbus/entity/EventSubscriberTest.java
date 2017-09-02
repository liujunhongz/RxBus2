package tv.lycam.rxbus.entity;

import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.reactivex.functions.Consumer;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.TestSubscriber;
import tv.lycam.rxbus.thread.EventThread;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class EventSubscriberTest {

    private static final Object FIXTURE_ARGUMENT = new Object();

    private boolean methodCalled;
    private Object methodArgument;

    @Before
    public void setUp() throws Exception {
        methodCalled = false;
        methodArgument = null;
    }

    /**
     * Checks that a no-frills, no-issues method call is properly executed.
     *
     * @throws Exception if the aforementioned proper execution is not to be had.
     */
    @Test
    public void basicMethodCall() throws Exception {
        Method method = getRecordingMethod();

        SubscriberEvent subscriber = new SubscriberEvent(this, method, EventThread.SINGLE);

        subscriber.handle(FIXTURE_ARGUMENT);

        assertTrue("Subscriber must call provided method.", methodCalled);
        assertSame("Subscriber argument must be *exactly* the provided object.",
                methodArgument, FIXTURE_ARGUMENT);
    }

    /**
     * Checks that SubscriberEvent's constructor disallows null methods.
     */
    @Test
    public void rejectionOfNullMethods() {
        try {
            new SubscriberEvent(this, null, EventThread.SINGLE);
            fail("SubscriberEvent must SINGLEly reject null methods.");
        } catch (NullPointerException expected) {
            // Hooray!
        }
    }

    /**
     * Checks that SubscriberEvent's constructor disallows null targets.
     */
    @Test
    public void rejectionOfNullTargets() throws NoSuchMethodException {
        Method method = getRecordingMethod();
        try {
            new SubscriberEvent(null, method, EventThread.SINGLE);
            fail("SubscriberEvent must SINGLEly reject null targets.");
        } catch (NullPointerException expected) {
            // Huzzah!
        }
    }

    @Test
    public void exceptionWrapping() throws NoSuchMethodException {
        Method method = getExceptionThrowingMethod();
        SubscriberEvent event = new SubscriberEvent(this, method, EventThread.SINGLE);

        event.getSubject().subscribe(new Consumer() {
            @Override
            public void accept(Object o) {
                fail("Subscribers whose methods throw must throw RuntimeException");
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                // Expected.
                assertTrue("Expected exception must be wrapped.",
                        throwable.getCause() instanceof IntentionalException);
            }
        });
        event.handle(new Object());
    }

    @Test
    public void errorPassthrough() throws InvocationTargetException, NoSuchMethodException {
        Method method = getErrorThrowingMethod();
        SubscriberEvent event = new SubscriberEvent(this, method, EventThread.SINGLE);

        event.getSubject().subscribe(new Consumer() {
            @Override
            public void accept(Object o) {
                fail("Subscribers whose methods throw Errors must rethrow them");
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                // Expected.
                assertEquals(throwable.getClass(), JudgmentError.class);
            }
        });
        event.handle(new Object());
    }

    @Test
    public void backPressure() throws NoSuchMethodException {
        Method method = getPrintMethod();
        final SubscriberEvent subscriber = new SubscriberEvent(this, method, EventThread.IO);

        FlowableProcessor subject = PublishProcessor.create().toSerialized();
        TestSubscriber testSubscriber = TestSubscriber.create(new Subscriber() {

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(Object o) {
                try {
                    if (subscriber.isValid()) {
                        subscriber.handleEvent(0);
                    }
                } catch (InvocationTargetException e) {
                    subscriber.throwRuntimeException("Could not dispatch event: " + o.getClass() + " to subscriber " + subscriber, e);
                }
            }
        });
        subject.onBackpressureBuffer().observeOn(EventThread.getScheduler(EventThread.IO))
                .subscribe(testSubscriber);
        try {
            Field subjectField = subscriber.getClass().getDeclaredField("subject");
            subjectField.setAccessible(true);
            subjectField.set(subscriber, subject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 2000; i++) {
            System.out.println("back pressure : " + i);
            subscriber.getSubject().onNext(new Object());
        }
        subscriber.getSubject().onComplete();
        testSubscriber.assertNoErrors();
    }

    private Method getRecordingMethod() throws NoSuchMethodException {
        return getClass().getMethod("recordingMethod", Object.class);
    }

    private Method getExceptionThrowingMethod() throws NoSuchMethodException {
        return getClass().getMethod("exceptionThrowingMethod", Object.class);
    }

    private Method getErrorThrowingMethod() throws NoSuchMethodException {
        return getClass().getMethod("errorThrowingMethod", Object.class);
    }

    private Method getPrintMethod() throws NoSuchMethodException {
        return getClass().getMethod("printMethod", Object.class);
    }

    public void printMethod(Object arg) {
        System.out.print("print arg=" + arg);
    }

    /**
     * Records the provided object in {@link #methodArgument} and sets
     * {@link #methodCalled}.
     *
     * @param arg argument to record.
     */
    public void recordingMethod(Object arg) {
        if (methodCalled) {
            throw new IllegalStateException("Method called more than once.");
        }
        methodCalled = true;
        methodArgument = arg;
    }

    public void exceptionThrowingMethod(Object arg) throws Exception {
        throw new IntentionalException();
    }

    /**
     * Local exception subclass to check variety of exception thrown.
     */
    static class IntentionalException extends Exception {
        private static final long serialVersionUID = -2500191180248181379L;
    }

    public void errorThrowingMethod(Object arg) {
        throw new JudgmentError();
    }

    /**
     * Local Error subclass to check variety of error thrown.
     */
    static class JudgmentError extends Error {
        private static final long serialVersionUID = 634248373797713373L;
    }
}
