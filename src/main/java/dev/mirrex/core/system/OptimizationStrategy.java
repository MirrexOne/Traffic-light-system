package dev.mirrex.core.system;

import dev.mirrex.model.enums.Direction;

import java.util.Map;

public class OptimizationStrategy {
    public Direction determineOptimalDirection(Map<Direction, Integer> directionQueues) {
        return directionQueues.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(Direction.NORTH);
    }
}
