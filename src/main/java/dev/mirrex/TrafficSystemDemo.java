package dev.mirrex;

import dev.mirrex.config.TrafficSystemConfig;
import dev.mirrex.core.light.AbstractTrafficLight;
import dev.mirrex.core.system.TrafficSystem;
import dev.mirrex.model.enums.EventType;
import dev.mirrex.model.enums.TrafficLightType;
import dev.mirrex.model.event.Event;
import dev.mirrex.model.event.QueueData;
import java.util.Map;
import java.util.Random;

public class TrafficSystemDemo {

    private static final Random RANDOM = new Random();

    public static void main(String[] args) throws InterruptedException {

        TrafficSystemConfig config = new TrafficSystemConfig(3000, 2000, 1000);

        TrafficSystem trafficSystem = new TrafficSystem(config);

        for (int i = 0; i < 5; i++) {
            Map<String, AbstractTrafficLight> lights = trafficSystem.getTrafficLights();
            for (AbstractTrafficLight light : lights.values()) {
                if (RANDOM.nextDouble() < 0.3) {
                    QueueData queueData = new QueueData(RANDOM.nextInt(3) + 1, light.getDirection());
                    light.sendEvent(new Event("SIMULATOR", light.getId(), EventType.QUEUE_UPDATE, queueData));
                }
            }

            System.out.println("\n=== Состояние системы ===");
            System.out.println("\nАвтомобильные светофоры:");
            lights.values().stream()
                    .filter(light -> light.getType() == TrafficLightType.VEHICLE)
                    .forEach(light -> System.out.printf(
                            "ID: %-4s | Направление: %-5s | Состояние: %-6s | Очередь: %d%n",
                            light.getId(),
                            light.getDirection(),
                            light.getCurrentState(),
                            light.getQueueSize()
                    ));

            System.out.println("\nПешеходные светофоры:");
            lights.values().stream()
                    .filter(light -> light.getType() == TrafficLightType.PEDESTRIAN)
                    .forEach(light -> System.out.printf(
                            "ID: %-4s | Направление: %-5s | Состояние: %-6s | Очередь: %d%n",
                            light.getId(),
                            light.getDirection(),
                            light.getCurrentState(),
                            light.getQueueSize()
                    ));

            Thread.sleep(1000);
        }

        trafficSystem.shutdown();
        System.out.println("\nДемонстрация завершена");
    }
}
