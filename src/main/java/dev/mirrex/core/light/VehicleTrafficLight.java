package dev.mirrex.core.light;

import dev.mirrex.config.TrafficSystemConfig;
import dev.mirrex.model.enums.Direction;
import dev.mirrex.model.enums.TrafficLightType;
import dev.mirrex.model.enums.VehicleTrafficLightState;
import dev.mirrex.model.event.Event;
import dev.mirrex.model.event.QueueData;

import java.beans.EventHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VehicleTrafficLight extends AbstractTrafficLight {

    private VehicleTrafficLightState currentState;

    public VehicleTrafficLight(
            String id,
            Direction direction,
            EventHandler eventHandler,
            ScheduledExecutorService scheduler,
            TrafficSystemConfig config) {
        super(id, direction, eventHandler, scheduler, config);
        this.currentState = VehicleTrafficLightState.RED;
    }

    @Override
    protected void processEvent(Event event) {
        switch (event.getType()) {
            case QUEUE_UPDATE:
                handleQueueUpdate((QueueData) event.getData());
                break;
            case STATE_CHANGE:
                handleStateChange((VehicleTrafficLightState) event.getData());
                break;
            case TIMER_ELAPSED:
                handleTimerElapsed((VehicleTrafficLightState) event.getData());
                break;
        }
    }

    private void handleQueueUpdate(QueueData data) {
        queueSize.set(data.getQueueSize());
        eventHandler.notifyQueueUpdate(this, data);
    }

    private void handleStateChange(VehicleTrafficLightState newState) {
        if (newState == VehicleTrafficLightState.GREEN && currentState != VehicleTrafficLightState.GREEN) {
            currentState = newState;
            scheduleQueueProcessing();
        } else if (newState == VehicleTrafficLightState.RED && currentState == VehicleTrafficLightState.GREEN) {
            currentState = VehicleTrafficLightState.YELLOW;
            scheduleTimer(config.getYellowLightDuration(), VehicleTrafficLightState.RED);
        }
    }

    private void handleTimerElapsed(VehicleTrafficLightState nextState) {
        currentState = nextState;
    }

    private void scheduleTimer(long delay, VehicleTrafficLightState nextState) {
        scheduler.schedule(
            () -> sendEvent(new Event(id, id, EventType.TIMER_ELAPSED, nextState)),
            delay,
            TimeUnit.MILLISECONDS
        );
    }

    private void scheduleQueueProcessing() {
        scheduler.scheduleAtFixedRate(
            () -> {
                if (currentState == VehicleTrafficLightState.GREEN && queueSize.get() > 0) {
                    queueSize.decrementAndGet();
                    eventHandler.notifyQueueUpdate(this, new QueueData(queueSize.get(), direction));
                }
            },
            0,
            config.getQueueProcessingInterval(),
            TimeUnit.MILLISECONDS
        );
    }

    @Override
    public TrafficLightType getType() {
        return TrafficLightType.VEHICLE;
    }

    @Override
    public Object getCurrentState() {
        return currentState;
    }
}