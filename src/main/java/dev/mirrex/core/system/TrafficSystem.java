package dev.mirrex.core.system;

import dev.mirrex.config.TrafficSystemConfig;
import dev.mirrex.core.handler.EventHandler;
import dev.mirrex.core.light.AbstractTrafficLight;
import dev.mirrex.core.light.PedestrianTrafficLight;
import dev.mirrex.core.light.VehicleTrafficLight;
import dev.mirrex.model.enums.Direction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TrafficSystem implements EventHandler {
    private final Map<String, AbstractTrafficLight> trafficLights;
    private final ScheduledExecutorService scheduler;
    private final ExecutorService trafficLightExecutor;
    private final TrafficSystemConfig config;
    private final OptimizationStrategy optimizationStrategy;

    public TrafficSystem(TrafficSystemConfig config) {
        this.config = config;
        this.trafficLights = new ConcurrentHashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.trafficLightExecutor = Executors.newCachedThreadPool();
        this.optimizationStrategy = new OptimizationStrategy();
        
        initializeTrafficLights();
        startAdaptiveControl();
    }

    private void initializeTrafficLights() {
        // Vehicle traffic lights
        TrafficLightUtil.VEHICLE_CONFIGURATIONS.forEach(this::createVehicleTrafficLight);
        
        // Pedestrian traffic lights
        TrafficLightUtil.PEDESTRIAN_CONFIGURATIONS.forEach(this::createPedestrianTrafficLight);
    }

    private void createVehicleTrafficLight(String id, Direction direction) {
        VehicleTrafficLight light = new VehicleTrafficLight(id, direction, this, scheduler, config);
        trafficLights.put(id, light);
        trafficLightExecutor.submit(light);
    }

    private void createPedestrianTrafficLight(String id, Direction direction) {
        PedestrianTrafficLight light = new PedestrianTrafficLight(id, direction, this, scheduler, config);
        trafficLights.put(id, light);
        trafficLightExecutor.submit(light);
    }

    private void startAdaptiveControl() {
        scheduler.scheduleAtFixedRate(
            () -> {
                Map<Direction, Integer> directionQueues = TrafficLightUtil.aggregateQueuesByDirection(trafficLights);
                Direction optimalDirection = optimizationStrategy.determineOptimalDirection(directionQueues);
                updateTrafficLightStates(optimalDirection);
            },
            0,
            config.getOptimizationInterval(),
            TimeUnit.MILLISECONDS
        );
    }

    private void updateTrafficLightStates(Direction greenDirection) {
        trafficLights.values().forEach(light -> {
            if (light.getDirection() == greenDirection) {
                sendGreenLightEvent(light);
            } else {
                sendRedLightEvent(light);
            }
        });
    }

    private void sendGreenLightEvent(AbstractTrafficLight light) {
        Object newState = light.getType() == TrafficLightType.VEHICLE ?
                VehicleTrafficLightState.GREEN : PedestrianTrafficLightState.GREEN;
        light.sendEvent(new Event("SYSTEM", light.getId(), EventType.STATE_CHANGE, newState));
    }

    private void sendRedLightEvent(AbstractTrafficLight light) {
        Object newState = light.getType() == TrafficLightType.VEHICLE ?
                VehicleTrafficLightState.RED : PedestrianTrafficLightState.RED;
        light.sendEvent(new Event("SYSTEM", light.getId(), EventType.STATE_CHANGE, newState));
    }

    @Override
    public void notifyQueueUpdate(AbstractTrafficLight source, QueueData queueData) {
        Event event = new Event(source.getId(), null, EventType.QUEUE_UPDATE, queueData);
        broadcastEvent(event);
    }

    private void broadcastEvent(Event event) {
        trafficLights.values().forEach(light -> {
            if (!light.getId().equals(event.getSourceId())) {
                light.sendEvent(event);
            }
        });
    }

    public void shutdown() {
        trafficLights.values().forEach(AbstractTrafficLight::stop);
        scheduler.shutdown();
        trafficLightExecutor.shutdown();
    }
}