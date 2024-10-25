package dev.mirrex;

import dev.mirrex.config.TrafficSystemConfig;
import dev.mirrex.core.light.AbstractTrafficLight;
import dev.mirrex.core.system.TrafficSystem;
import dev.mirrex.model.enums.EventType;
import dev.mirrex.model.enums.PedestrianTrafficLightState;
import dev.mirrex.model.enums.TrafficLightType;
import dev.mirrex.model.enums.VehicleTrafficLightState;
import dev.mirrex.model.event.Event;
import dev.mirrex.model.event.QueueData;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TrafficSystemDemo {
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        // Создаем конфигурацию с более короткими интервалами для демонстрации
        TrafficSystemConfig config = new TrafficSystemConfig(
                3000,  // Оптимизация каждые 3 секунды
                2000,  // Желтый свет 2 секунды
                1000   // Обработка очереди каждую секунду
        );

        TrafficSystem trafficSystem = new TrafficSystem(config);

        // Запускаем симуляцию случайного появления машин и пешеходов
        Thread simulationThread = new Thread(() -> {
            try {
                while (true) {
                    simulateRandomTraffic(trafficSystem);
                    printSystemState(trafficSystem);
                    Thread.sleep(2000); // Обновление каждые 2 секунды
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        simulationThread.start();

        // Даем системе поработать 2 минуты
        try {
            Thread.sleep(TimeUnit.MINUTES.toMillis(2));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Завершаем работу
        simulationThread.interrupt();
        trafficSystem.shutdown();
    }

    private static void simulateRandomTraffic(TrafficSystem trafficSystem) {
        // Симулируем случайное появление транспорта и пешеходов
        for (AbstractTrafficLight light : trafficSystem.getTrafficLights().values()) {
            if (RANDOM.nextDouble() < 0.3) { // 30% шанс появления
                int queueIncrement = RANDOM.nextInt(3) + 1; // 1-3 новых участника движения
                QueueData queueData = new QueueData(queueIncrement, light.getDirection());
                Event event = new Event(
                        "SIMULATOR",
                        light.getId(),
                        EventType.QUEUE_UPDATE,
                        queueData
                );
                light.sendEvent(event);
            }
        }
    }

    private static void printSystemState(TrafficSystem trafficSystem) {
        System.out.println("\n=== Текущее состояние системы ===");
        System.out.println("Время: " + System.currentTimeMillis());
        System.out.println("\nСостояния светофоров:");

        // Сначала выводим автомобильные светофоры
        System.out.println("\nАвтомобильные светофоры:");
        trafficSystem.getTrafficLights().values().stream()
                .filter(light -> light.getType() == TrafficLightType.VEHICLE)
                .forEach(light -> printTrafficLightState(light));

        // Затем пешеходные
        System.out.println("\nПешеходные светофоры:");
        trafficSystem.getTrafficLights().values().stream()
                .filter(light -> light.getType() == TrafficLightType.PEDESTRIAN)
                .forEach(light -> printTrafficLightState(light));

        System.out.println("\n--------------------------------");
    }

    private static void printTrafficLightState(AbstractTrafficLight light) {
        String stateColor = getStateColor(light.getCurrentState());
        System.out.printf("ID: %-4s | Направление: %-5s | Состояние: %-6s | Очередь: %d%n",
                light.getId(),
                light.getDirection(),
                stateColor,
                light.getQueueSize()
        );
    }

    private static String getStateColor(Object state) {
        if (state instanceof VehicleTrafficLightState) {
            return state.toString();
        } else if (state instanceof PedestrianTrafficLightState) {
            return state.toString();
        }
        return "UNKNOWN";
    }
}
