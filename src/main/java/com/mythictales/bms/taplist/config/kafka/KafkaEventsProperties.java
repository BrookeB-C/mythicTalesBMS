package com.mythictales.bms.taplist.config.kafka;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bms.kafka")
public class KafkaEventsProperties {

  private boolean enabled = false;
  private List<String> domains =
      new ArrayList<>(
          List.of(
              "prodinventory",
              "keginventory",
              "taproom",
              "catalog",
              "sales",
              "distribution",
              "procurement",
              "maintenance",
              "analytics",
              "billing",
              "compliance",
              "iam"));
  private int defaultPartitions = 6;
  private short defaultReplicationFactor = 1;
  private Duration retention = Duration.ofDays(7);
  private final Map<String, TopicSettings> overrides = new HashMap<>();

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public List<String> getDomains() {
    return domains;
  }

  public void setDomains(List<String> domains) {
    if (domains == null || domains.isEmpty()) {
      this.domains = new ArrayList<>();
      return;
    }
    this.domains =
        domains.stream()
            .map(String::trim)
            .map(value -> value.toLowerCase(Locale.US))
            .filter(s -> !s.isBlank())
            .collect(Collectors.toCollection(ArrayList::new));
  }

  public int getDefaultPartitions() {
    return defaultPartitions;
  }

  public void setDefaultPartitions(int defaultPartitions) {
    this.defaultPartitions = defaultPartitions;
  }

  public short getDefaultReplicationFactor() {
    return defaultReplicationFactor;
  }

  public void setDefaultReplicationFactor(short defaultReplicationFactor) {
    this.defaultReplicationFactor = defaultReplicationFactor;
  }

  public Duration getRetention() {
    return retention;
  }

  public void setRetention(Duration retention) {
    this.retention = retention == null ? Duration.ofDays(7) : retention;
  }

  public Map<String, TopicSettings> getOverrides() {
    return overrides;
  }

  public Set<String> resolvedDomains() {
    Set<String> values = new LinkedHashSet<>();
    values.addAll(domains);
    values.addAll(overrides.keySet());
    return values.stream()
        .map(String::trim)
        .map(value -> value.toLowerCase(Locale.US))
        .filter(s -> !s.isBlank())
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public TopicDescriptor descriptorFor(String domain) {
    Objects.requireNonNull(domain, "domain must not be null");
    String key = domain.trim().toLowerCase(Locale.US);
    TopicSettings settings = overrides.get(key);
    String topicName =
        settings != null && settings.getTopic() != null ? settings.getTopic() : key + ".events.v1";
    int partitions =
        settings != null && settings.getPartitions() != null
            ? settings.getPartitions()
            : defaultPartitions;
    short replication =
        settings != null && settings.getReplicationFactor() != null
            ? settings.getReplicationFactor()
            : defaultReplicationFactor;
    Duration topicRetention =
        settings != null && settings.getRetention() != null ? settings.getRetention() : retention;
    return new TopicDescriptor(topicName, partitions, replication, topicRetention);
  }

  public record TopicDescriptor(
      String name, int partitions, short replicationFactor, Duration retention) {
    public TopicDescriptor {
      Objects.requireNonNull(name, "name must not be null");
      Objects.requireNonNull(retention, "retention must not be null");
      if (partitions <= 0) {
        throw new IllegalArgumentException("partitions must be positive");
      }
      if (replicationFactor <= 0) {
        throw new IllegalArgumentException("replicationFactor must be positive");
      }
    }
  }

  public static class TopicSettings {
    private String topic;
    private Integer partitions;
    private Short replicationFactor;
    private Duration retention;

    public String getTopic() {
      return topic;
    }

    public void setTopic(String topic) {
      this.topic = (topic == null || topic.isBlank()) ? null : topic;
    }

    public Integer getPartitions() {
      return partitions;
    }

    public void setPartitions(Integer partitions) {
      this.partitions = partitions;
    }

    public Short getReplicationFactor() {
      return replicationFactor;
    }

    public void setReplicationFactor(Short replicationFactor) {
      this.replicationFactor = replicationFactor;
    }

    public Duration getRetention() {
      return retention;
    }

    public void setRetention(Duration retention) {
      this.retention = retention;
    }
  }
}
