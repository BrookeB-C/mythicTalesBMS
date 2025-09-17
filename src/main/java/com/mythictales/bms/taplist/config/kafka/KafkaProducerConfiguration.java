package com.mythictales.bms.taplist.config.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.observation.ObservationRegistry;

@Configuration
@EnableConfigurationProperties({KafkaEventsProperties.class, KafkaSampleProperties.class})
@ConditionalOnProperty(prefix = "bms.kafka", name = "enabled", havingValue = "true")
public class KafkaProducerConfiguration {

  private final KafkaProperties kafkaProperties;

  public KafkaProducerConfiguration(KafkaProperties kafkaProperties) {
    this.kafkaProperties = kafkaProperties;
  }

  @Bean
  public ProducerFactory<String, Object> domainEventProducerFactory(ObjectMapper objectMapper) {
    Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties(null));
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    props.put(ProducerConfig.RETRIES_CONFIG, 5);
    props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120_000);
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
    props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

    return new DefaultKafkaProducerFactory<>(
        props, new StringSerializer(), new JsonSerializer<>(objectMapper));
  }

  @Bean
  public KafkaTemplate<String, Object> domainEventKafkaTemplate(
      ProducerFactory<String, Object> domainEventProducerFactory,
      ObjectProvider<ObservationRegistry> observationRegistry) {
    KafkaTemplate<String, Object> template = new KafkaTemplate<>(domainEventProducerFactory);
    observationRegistry.ifAvailable(registry -> template.setObservationEnabled(true));
    return template;
  }

  @Bean
  public List<NewTopic> domainEventTopics(KafkaEventsProperties properties) {
    return properties.resolvedDomains().stream()
        .map(
            domain -> {
              KafkaEventsProperties.TopicDescriptor descriptor = properties.descriptorFor(domain);
              return TopicBuilder.name(descriptor.name())
                  .partitions(descriptor.partitions())
                  .replicas(descriptor.replicationFactor())
                  .config(
                      TopicConfig.RETENTION_MS_CONFIG,
                      String.valueOf(descriptor.retention().toMillis()))
                  .build();
            })
        .toList();
  }
}
