package tv.lycam.rxbus.thread;

import android.os.Handler;

import java.util.concurrent.Executor;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public enum EventThread {
    /**
     * {@link Scheduler} which will execute actions on the Android UI thread.
     */
    MAIN_THREAD,

    /**
     * Creates and returns a {@link Scheduler} that creates a new {@link Thread} for each unit of work.
     *
     * Unhandled errors will be delivered to the scheduler Thread's {@link java.lang.Thread.UncaughtExceptionHandler}.
     */
    NEW_THREAD,

    /**
     * Creates and returns a {@link Scheduler} intended for IO-bound work.
     * 
     * The implementation is backed by an {@link Executor} thread-pool that will grow as needed.
     * 
     * This can be used for asynchronously performing blocking IO.
     * 
     * Do not perform computational work on this scheduler. Use computation() instead.
     * 
     * Unhandled errors will be delivered to the scheduler Thread's {@link java.lang.Thread.UncaughtExceptionHandler}.
     */
    IO,

    /**
     * Creates and returns a {@link Scheduler} intended for computational work.
     * 
     * This can be used for event-loops, processing callbacks and other computational work.
     * 
     * Do not perform IO-bound work on this scheduler. Use io() instead.
     * 
     * Unhandled errors will be delivered to the scheduler Thread's {@link java.lang.Thread.UncaughtExceptionHandler}.
     */
    COMPUTATION,

    /**
     * Creates and returns a {@link Scheduler} that queues work on the current thread to be executed after the
     * current work completes.
     */
    TRAMPOLINE,
    /**
     * Creates and returns a {@link Scheduler} that executes work immediately on the current thread.
     */
    SINGLE,

    /**
     * Converts an {@link Executor} into a new Scheduler instance.
     */
    EXECUTOR,

    /**
     * {@link Scheduler} which uses the provided {@link Handler} to execute actions.
     */
    HANDLER;

    public static Scheduler getScheduler(EventThread thread) {
        Scheduler scheduler;
        switch (thread) {
            case MAIN_THREAD:
                scheduler = AndroidSchedulers.mainThread();
                break;
            case NEW_THREAD:
                scheduler = Schedulers.newThread();
                break;
            case IO:
                scheduler = Schedulers.io();
                break;
            case COMPUTATION:
                scheduler = Schedulers.computation();
                break;
            case TRAMPOLINE:
                scheduler = Schedulers.trampoline();
                break;
            case SINGLE:
                scheduler = Schedulers.single();
                break;
            case EXECUTOR:
                scheduler = Schedulers.from(ThreadHandler.DEFAULT.getExecutor());
                break;
            case HANDLER:
            default:
                scheduler = AndroidSchedulers.mainThread();
                break;
        }
        return scheduler;
    }
}
