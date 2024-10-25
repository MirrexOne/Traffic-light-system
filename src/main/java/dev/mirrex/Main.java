package dev.mirrex;

import dev.mirrex.config.TrafficSystemConfig;
import dev.mirrex.core.system.TrafficSystem;

public class Main {
    public static void main(String[] args) {
        TrafficSystemConfig config = new TrafficSystemConfig();
        
        TrafficSystem trafficSystem = new TrafficSystem(config);
        
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            trafficSystem.shutdown();
        }
    }
}
