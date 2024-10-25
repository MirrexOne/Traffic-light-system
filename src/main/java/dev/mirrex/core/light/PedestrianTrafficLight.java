package dev.mirrex.core.light;

import dev.mirrex.config.TrafficSystemConfig;
import dev.mirrex.core.handler.EventHandler;
import dev.mirrex.model.enums.Direction;
import dev.mirrex.model.enums.PedestrianTrafficLightState;
import dev.mirrex.model.enums.TrafficLightType;
import dev.mirrex.model.event.Event;
import dev.mirrex.model.event.QueueData;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PedestrianTrafficLight extends AbstractTrafficLight {

    private PedestrianTrafficLightState currentState;

    public PedestrianTrafficLight(
            String id,
            Direction direction,
            EventHandler eventHandler,
            ScheduledExecutorService scheduler,
            TrafficSystemConfig config) {
        super(id, direction, eventHandler, scheduler, config);
        this.currentState = PedestrianTrafficLightState.RED;
    }

    @Override
    protected void processEvent(Event event) {
        switch (event.getType()) {
            case QUEUE_UPDATE:
                handleQueueUpdate((QueueData) event.getData());
                break;
            case STATE_CHANGE:
                handleStateChange((PedestrianTrafficLightState) event.getData());
                break;
            case TIMER_ELAPSED:
                handleTimerElapsed();
                break;
        }
    }

    private void handleQueueUpdate(QueueData data) {
        queueSize.set(data.getQueueSize());
        eventHandler.notifyQueueUpdate(this, data);
    }

    private void handleStateChange(PedestrianTrafficLightState newState) {
        currentState = newState;
        if (newState == PedestrianTrafficLightState.GREEN) {
            scheduleQueueProcessing();
        }
    }

    private void handleTimerElapsed() {
        if (currentState == PedestrianTrafficLightState.GREEN) {
            currentState = PedestrianTrafficLightState.RED;
        }
    }

    private void scheduleQueueProcessing() {
        scheduler.scheduleAtFixedRate(
                () -> {
                    if (currentState == PedestrianTrafficLightState.GREEN && queueSize.get() > 0) {
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
        return TrafficLightType.PEDESTRIAN;
    }

    @Override
    public Object getCurrentState() {
        return currentState;
    }
}
