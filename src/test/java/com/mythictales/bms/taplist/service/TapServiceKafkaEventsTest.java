package com.mythictales.bms.taplist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.mythictales.bms.taplist.domain.Beer;
import com.mythictales.bms.taplist.domain.Brewery;
import com.mythictales.bms.taplist.domain.Keg;
import com.mythictales.bms.taplist.domain.KegPlacement;
import com.mythictales.bms.taplist.domain.KegSizeSpec;
import com.mythictales.bms.taplist.domain.KegStatus;
import com.mythictales.bms.taplist.domain.Role;
import com.mythictales.bms.taplist.domain.Tap;
import com.mythictales.bms.taplist.domain.Taproom;
import com.mythictales.bms.taplist.domain.UserAccount;
import com.mythictales.bms.taplist.domain.Venue;
import com.mythictales.bms.taplist.kafka.DomainEventMetadata;
import com.mythictales.bms.taplist.kafka.DomainEventPublisher;
import com.mythictales.bms.taplist.repo.KegEventRepository;
import com.mythictales.bms.taplist.repo.KegPlacementRepository;
import com.mythictales.bms.taplist.repo.KegRepository;
import com.mythictales.bms.taplist.repo.TapRepository;
import com.mythictales.bms.taplist.repo.UserAccountRepository;

@ExtendWith(MockitoExtension.class)
class TapServiceKafkaEventsTest {

  @Mock private TapRepository tapRepository;
  @Mock private KegRepository kegRepository;
  @Mock private KegPlacementRepository placementRepository;
  @Mock private KegEventRepository eventRepository;
  @Mock private UserAccountRepository userRepository;
  @Mock private ApplicationEventPublisher applicationEventPublisher;
  @Mock private DomainEventPublisher domainEventPublisher;

  @Captor private ArgumentCaptor<DomainEventMetadata> metadataCaptor;
  @Captor private ArgumentCaptor<Object> payloadCaptor;

  private TapService tapService;
  private Brewery brewery;
  private Taproom taproom;
  private Venue venue;
  private Tap tap;
  private Keg keg;
  private UserAccount actor;

  @BeforeEach
  void setUp() {
    tapService =
        new TapService(
            tapRepository,
            kegRepository,
            placementRepository,
            eventRepository,
            userRepository,
            applicationEventPublisher,
            Optional.of(domainEventPublisher));

    brewery = new Brewery("Mythic");
    brewery.setId(1L);

    taproom = new Taproom("Main Room", brewery);
    taproom.setId(10L);

    venue = new Venue();
    venue.setId(20L);
    venue.setName("Main Venue");
    venue.setBrewery(brewery);

    tap = new Tap(5);
    tap.setId(30L);
    tap.setTaproom(taproom);
    tap.setVenue(venue);

    Beer beer = new Beer("Mystic IPA", "IPA", 6.5);
    beer.setId(40L);

    KegSizeSpec size = new KegSizeSpec("HALF", 15.5);
    size.setGallons(15.5);

    keg = new Keg();
    keg.setId(50L);
    keg.setSerialNumber("K-123");
    keg.setBrewery(brewery);
    keg.setSize(size);
    keg.setBeer(beer);
    keg.setRemainingOunces(1984);
    keg.setTotalOunces(1984);
    keg.setStatus(KegStatus.TAPPED);
    tap.setKeg(keg);

    actor = new UserAccount("tapadmin", "pw", Role.TAPROOM_ADMIN);
    actor.setId(60L);

    when(tapRepository.findById(tap.getId())).thenReturn(Optional.of(tap));
    org.mockito.Mockito.lenient()
        .when(placementRepository.save(any(KegPlacement.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(userRepository.findById(actor.getId())).thenReturn(Optional.of(actor));
  }

  @Test
  void tapKegPublishesKafkaEvent() {
    when(kegRepository.findById(keg.getId())).thenReturn(Optional.of(keg));
    when(placementRepository.findFirstByTapIdAndEndedAtIsNull(tap.getId()))
        .thenReturn(Optional.empty());

    tapService.tapKeg(tap.getId(), keg.getId(), actor.getId());

    verify(domainEventPublisher).publish(metadataCaptor.capture(), payloadCaptor.capture());
    DomainEventMetadata metadata = metadataCaptor.getValue();
    assertThat(metadata.domain()).isEqualTo("taproom");
    assertThat(metadata.eventType()).isEqualTo("KegTapped");
    assertThat(metadata.breweryId()).isEqualTo(uuidFor("brewery", brewery.getId()));
    assertThat(metadata.facilityId()).contains(uuidFor("taproom", taproom.getId()));
    assertThat(metadata.venueId()).contains(uuidFor("venue", venue.getId()));

    @SuppressWarnings("unchecked")
    Map<String, Object> payload = (Map<String, Object>) payloadCaptor.getValue();
    assertThat(payload).containsEntry("tapId", tap.getId());
    assertThat(payload).containsEntry("kegId", keg.getId());
    assertThat(payload).containsEntry("actorUserId", actor.getId());
    assertThat(payload.get("beerName")).isEqualTo("Mystic IPA");
  }

  @Test
  void pourPublishesBeerPouredEvent() {
    KegPlacement placement = new KegPlacement(tap, keg);
    when(placementRepository.findFirstByTapIdAndEndedAtIsNull(tap.getId()))
        .thenReturn(Optional.of(placement));

    tapService.pour(tap.getId(), 12.0, actor.getId());

    verify(domainEventPublisher).publish(metadataCaptor.capture(), payloadCaptor.capture());
    DomainEventMetadata metadata = metadataCaptor.getValue();
    assertThat(metadata.eventType()).isEqualTo("BeerPoured");

    @SuppressWarnings("unchecked")
    Map<String, Object> payload = (Map<String, Object>) payloadCaptor.getValue();
    assertThat(payload).containsEntry("ounces", 12.0);
  }

  @Test
  void blowPublishesBlownAndUntappedEvents() {
    KegPlacement placement = new KegPlacement(tap, keg);
    when(placementRepository.findFirstByTapIdAndEndedAtIsNull(tap.getId()))
        .thenReturn(Optional.of(placement));

    tapService.blow(tap.getId(), actor.getId());

    verify(domainEventPublisher, times(2))
        .publish(metadataCaptor.capture(), payloadCaptor.capture());
    assertThat(metadataCaptor.getAllValues())
        .extracting(DomainEventMetadata::eventType)
        .containsExactly("KegBlown", "KegUntapped");
  }

  private UUID uuidFor(String scope, Long id) {
    return UUID.nameUUIDFromBytes((scope + ":" + id).getBytes(StandardCharsets.UTF_8));
  }
}
