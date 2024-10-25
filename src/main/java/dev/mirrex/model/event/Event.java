package dev.mirrex.model.event;

import dev.mirrex.model.enums.EventType;

public class Event {

    private final String sourceId;
    private final String targetId;
    private final EventType type;
    private final Object data;
    private final long timestamp;

    public Event(String sourceId, String targetId, EventType type, Object data) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public EventType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
