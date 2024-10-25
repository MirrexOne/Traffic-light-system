package dev.mirrex.config;

public class TrafficSystemConfig {

    private static final int DEFAULT_OPTIMIZATION_INTERVAL = 5000;
    private static final int DEFAULT_YELLOW_LIGHT_DURATION = 3000;
    private static final int DEFAULT_QUEUE_PROCESSING_INTERVAL = 1000;

    private final int optimizationInterval;
    private final int yellowLightDuration;
    private final int queueProcessingInterval;

    public TrafficSystemConfig() {
        this(DEFAULT_OPTIMIZATION_INTERVAL, DEFAULT_YELLOW_LIGHT_DURATION, DEFAULT_QUEUE_PROCESSING_INTERVAL);
    }

    public TrafficSystemConfig(int optimizationInterval, int yellowLightDuration, int queueProcessingInterval) {
        this.optimizationInterval = optimizationInterval;
        this.yellowLightDuration = yellowLightDuration;
        this.queueProcessingInterval = queueProcessingInterval;
    }

    public int getOptimizationInterval() {
        return optimizationInterval;
    }

    public int getYellowLightDuration() {
        return yellowLightDuration;
    }

    public int getQueueProcessingInterval() {
        return queueProcessingInterval;
    }
}
