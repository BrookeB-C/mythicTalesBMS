package com.mythictales.bms.taplist.kafka;

public interface DomainEventPublisher {
  void publish(DomainEventMetadata metadata, Object payload);
}
