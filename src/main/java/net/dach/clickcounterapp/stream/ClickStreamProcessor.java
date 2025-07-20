package net.dach.clickcounterapp.stream;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class ClickStreamProcessor {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.streams.application-id}")
    private String applicationId;

    @Value("${click.topic.name}")
    private String clicksTopicName;

    @Value("${click.topic.counts}")
    private String clickCountsTopicName;

    @PostConstruct
    public void start() {
        try {
            Properties props = new Properties();
            props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
            props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
            props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);

            // Add client.dns.lookup configuration to use the system's hosts file
            props.put("client.dns.lookup", "use_all_dns_ips");

            // Add configuration to handle connection failures
            props.put(StreamsConfig.RECONNECT_BACKOFF_MS_CONFIG, 1000);
            props.put(StreamsConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 10000);
            props.put(StreamsConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
            props.put(StreamsConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);

            StreamsBuilder builder = new StreamsBuilder();
            KStream<String, String> clickStream = builder.stream(clicksTopicName);

            // Afficher les clics reçus
            clickStream.peek((key, value) -> System.out.println("Clic reçu - User: " + key + ", Action: " + value));

            // Compter les clics par utilisateur
            KTable<String, Long> clickCounts = clickStream
                    .groupBy((key, value) -> key)  // regrouper par utilisateur
                    .count();  // compter

            // Convertir le KTable en KStream pour l'envoyer dans un topic Kafka
            KStream<String, Long> resultStream = clickCounts.toStream();

            // Écrire les résultats dans le topic "click-counts"
            resultStream.to(clickCountsTopicName, Produced.with(Serdes.String(), Serdes.Long()));

            // Afficher les résultats dans la console
            resultStream.peek((user, count) -> System.out.println("Utilisateur : " + user + " -> Total de clics : " + count));

            // Démarrer Kafka Streams
            KafkaStreams streams = new KafkaStreams(builder.build(), props);
            streams.start();

            // Arrêter proprement Kafka Streams à la fermeture de l'application
            Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

            System.out.println("Kafka Streams started successfully");
        } catch (Exception e) {
            System.err.println("Failed to start Kafka Streams: " + e.getMessage());
            System.err.println("Application will continue without Kafka Streams functionality");
            e.printStackTrace();
        }
    }
}
