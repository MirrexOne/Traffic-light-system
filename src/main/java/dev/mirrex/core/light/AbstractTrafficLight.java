package dev.mirrex.core.light;

import dev.mirrex.config.TrafficSystemConfig;
import dev.mirrex.core.handler.EventHandler;
import dev.mirrex.model.enums.Direction;
import dev.mirrex.model.enums.TrafficLightType;
import dev.mirrex.model.event.Event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractTrafficLight implements Runnable {

    protected final String id;
    protected final Direction direction;
    protected final BlockingQueue<Event> eventQueue;
    protected final AtomicInteger queueSize;
    protected final EventHandler eventHandler;
    protected final ScheduledExecutorService scheduler;
    protected final TrafficSystemConfig config;
    protected volatile boolean running;

    protected AbstractTrafficLight(
            String id,
            Direction direction,
            EventHandler eventHandler,
            ScheduledExecutorService scheduler,
            TrafficSystemConfig config) {
        this.id = id;
        this.direction = direction;
        this.eventHandler = eventHandler;
        this.scheduler = scheduler;
        this.config = config;
        this.eventQueue = new LinkedBlockingQueue<>();
        this.queueSize = new AtomicInteger(0);
        this.running = true;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Event event = eventQueue.take();
                processEvent(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void sendEvent(Event event) {
        eventQueue.add(event);
    }

    public String getId() {
        return id;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getQueueSize() {
        return queueSize.get();
    }

    public void stop() {
        running = false;
    }

    protected abstract void processEvent(Event event);

    public abstract TrafficLightType getType();

    public abstract Object getCurrentState();
}
