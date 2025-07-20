package net.dach.clickcounterapp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClickCountService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topicName;

    public ClickCountService(KafkaTemplate<String, String> kafkaTemplate,
                             @Value("${click.topic.name}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void sendClick(String userId) {
        try {
            kafkaTemplate.send(topicName, userId, "click");
            System.out.println("Click sent to Kafka - User: " + userId);
        } catch (Exception e) {
            System.err.println("Failed to send click to Kafka: " + e.getMessage());
            System.err.println("Application will continue without Kafka functionality");
        }
    }
}
