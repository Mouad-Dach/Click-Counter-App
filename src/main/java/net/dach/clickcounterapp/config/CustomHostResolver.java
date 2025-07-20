package net.dach.clickcounterapp.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomHostResolver {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @PostConstruct
    public void init() {
        // Add entries to the hosts file programmatically (this is just for logging)
        System.out.println("Using Kafka bootstrap servers: " + bootstrapServers);

        // Try to add a system property for DNS resolution
        try {
            java.security.Security.setProperty("networkaddress.cache.ttl", "0");
            System.setProperty("jdk.net.hosts.file", System.getProperty("java.io.tmpdir") + "/hosts");

            // Create a temporary hosts file
            java.io.File hostsFile = new java.io.File(System.getProperty("jdk.net.hosts.file"));
            java.io.PrintWriter writer = new java.io.PrintWriter(hostsFile);
            writer.println("127.0.0.1 localhost");
            writer.println(bootstrapServers.split(":")[0] + " kafka");
            writer.close();

            System.out.println("Created custom hosts file at: " + hostsFile.getAbsolutePath());
            System.out.println("Mapped 'kafka' to '" + bootstrapServers.split(":")[0] + "'");
        } catch (Exception e) {
            System.err.println("Failed to configure custom host resolution: " + e.getMessage());
            System.err.println("Application will continue with default host resolution");
        }
    }
}
