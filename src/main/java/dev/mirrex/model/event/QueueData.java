package dev.mirrex.model.event;

import dev.mirrex.model.enums.Direction;

public class QueueData {
    private final int queueSize;
    private final Direction direction;

    public QueueData(int queueSize, Direction direction) {
        this.queueSize = queueSize;
        this.direction = direction;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public Direction getDirection() {
        return direction;
    }
}
