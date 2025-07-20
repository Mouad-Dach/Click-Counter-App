package net.dach.clickcounterapp.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ClickCountConsumer {

    private final Map<String, Long> userClickCounts = new ConcurrentHashMap<>();
    private final AtomicLong totalClicks = new AtomicLong(0);

    @KafkaListener(
            topics = "${click.topic.counts}",
            containerFactory = "longKafkaListenerContainerFactory"
    )
    public void consume(String userId, Long count) {
        try {
            userClickCounts.put(userId, count);
            recalculateTotalClicks();
            System.out.println("Received updated count: User=" + userId + ", Count=" + count);
        } catch (Exception e) {
            System.err.println("Error processing Kafka message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void recalculateTotalClicks() {
        long total = userClickCounts.values().stream().mapToLong(Long::longValue).sum();
        totalClicks.set(total);
    }

    public Map<String, Long> getUserClickCounts() {
        return userClickCounts;
    }

    public long getTotalClicks() {
        return totalClicks.get();
    }
}
