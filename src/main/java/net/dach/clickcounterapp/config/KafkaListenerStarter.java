package net.dach.clickcounterapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class KafkaListenerStarter {

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    /**
     * Start Kafka listeners after a delay to give the application time to start up
     * and to allow the application to continue running even if Kafka is not available.
     */
    @Scheduled(initialDelay = 10000, fixedDelay = Long.MAX_VALUE)
    public void startKafkaListeners() {
        try {
            System.out.println("Starting Kafka listeners...");
            for (MessageListenerContainer container : kafkaListenerEndpointRegistry.getListenerContainers()) {
                if (!container.isRunning()) {
                    try {
                        container.start();
                        System.out.println("Started Kafka listener: " + container.getListenerId());
                    } catch (Exception e) {
                        System.err.println("Failed to start Kafka listener " + container.getListenerId() + ": " + e.getMessage());
                        System.err.println("Application will continue without this Kafka listener");
                    }
                }
            }
            System.out.println("All Kafka listeners started successfully");
        } catch (Exception e) {
            System.err.println("Failed to start Kafka listeners: " + e.getMessage());
            System.err.println("Application will continue without Kafka functionality");
        }
    }
}
