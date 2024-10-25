package dev.mirrex.core.handler;

import dev.mirrex.core.light.AbstractTrafficLight;
import dev.mirrex.model.event.QueueData;

public interface EventHandler {
    void notifyQueueUpdate(AbstractTrafficLight source, QueueData data);
}
