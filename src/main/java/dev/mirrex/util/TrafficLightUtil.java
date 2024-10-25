package dev.mirrex.util;

import dev.mirrex.core.light.AbstractTrafficLight;
import dev.mirrex.model.enums.Direction;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class TrafficLightUtil {
    public static final List<TrafficLightConfiguration> VEHICLE_CONFIGURATIONS = Arrays.asList(
        new TrafficLightConfiguration("V_N", Direction.NORTH),
        new TrafficLightConfiguration("V_S", Direction.SOUTH),
        new TrafficLightConfiguration("V_E", Direction.EAST),
        new TrafficLightConfiguration("V_W", Direction.WEST)
    );

    public static final List<TrafficLightConfiguration> PEDESTRIAN_CONFIGURATIONS = Arrays.asList(
        new TrafficLightConfiguration("P_N1", Direction.NORTH),
        new TrafficLightConfiguration("P_N2", Direction.NORTH),
        new TrafficLightConfiguration("P_S1", Direction.SOUTH),
        new TrafficLightConfiguration("P_S2", Direction.SOUTH),
        new TrafficLightConfiguration("P_E1", Direction.EAST),
        new TrafficLightConfiguration("P_E2", Direction.EAST),
        new TrafficLightConfiguration("P_W1", Direction.WEST),
        new TrafficLightConfiguration("P_W2", Direction.WEST)
    );

    public static Map<Direction, Integer> aggregateQueuesByDirection(Map<String, AbstractTrafficLight> trafficLights) {
        Map<Direction, Integer> queueSizes = new EnumMap<>(Direction.class);
        
        trafficLights.values().forEach(light -> {
            Direction direction = light.getDirection();
            queueSizes.merge(direction, light.getQueueSize(), Integer::sum);
        });
        
        return queueSizes;
    }

    public static class TrafficLightConfiguration {
        private final String id;
        private final Direction direction;

        public TrafficLightConfiguration(String id, Direction direction) {
            this.id = id;
            this.direction = direction;
        }

        public String getId() {
            return id;
        }

        public Direction getDirection() {
            return direction;
        }
    }
}
