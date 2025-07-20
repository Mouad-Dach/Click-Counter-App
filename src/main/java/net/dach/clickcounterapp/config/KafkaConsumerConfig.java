package net.dach.clickcounterapp.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry() {
        return new KafkaListenerEndpointRegistry();
    }

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Bean
    public ConsumerFactory<String, String> stringConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // Add configuration to handle connection failures
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 5000);
        config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 2000);
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);
        // Add client.dns.lookup configuration to use the system's hosts file
        config.put("client.dns.lookup", "use_all_dns_ips");
        // Add reconnect.backoff.ms to handle connection failures
        config.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, 1000);
        config.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 10000);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stringConsumerFactory());
        // Set missing topics fatal to false to prevent the application from stopping if the topics don't exist
        factory.setMissingTopicsFatal(false);
        // Set a retry interval for authentication exceptions
        factory.getContainerProperties().setAuthExceptionRetryInterval(Duration.ofMillis(10000));
        // Set auto-startup to false to prevent the application from stopping if Kafka is not available
        factory.setAutoStartup(false);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Long> longConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        // Add configuration to handle connection failures
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 5000);
        config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 2000);
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);
        // Add client.dns.lookup configuration to use the system's hosts file
        config.put("client.dns.lookup", "use_all_dns_ips");
        // Add reconnect.backoff.ms to handle connection failures
        config.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, 1000);
        config.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 10000);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Long> longKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Long> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(longConsumerFactory());
        // Set missing topics fatal to false to prevent the application from stopping if the topics don't exist
        factory.setMissingTopicsFatal(false);
        // Set a retry interval for authentication exceptions
        factory.getContainerProperties().setAuthExceptionRetryInterval(Duration.ofMillis(10000));
        // Set auto-startup to false to prevent the application from stopping if Kafka is not available
        factory.setAutoStartup(false);
        return factory;
    }
}
