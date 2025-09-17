package com.mythictales.bms.taplist.keginventory.service;

import org.springframework.stereotype.Component;

import com.mythictales.bms.taplist.domain.Keg;
import com.mythictales.bms.taplist.domain.Venue;
import com.mythictales.bms.taplist.keginventory.domain.KegMovementHistory;
import com.mythictales.bms.taplist.keginventory.repo.KegMovementHistoryRepository;
import com.mythictales.bms.taplist.security.CurrentUser;

@Component
public class KegMovementRecorder {
  private final KegMovementHistoryRepository history;

  public KegMovementRecorder(KegMovementHistoryRepository history) {
    this.history = history;
  }

  public void record(Keg keg, Venue from, Venue to, String externalPartner, CurrentUser user) {
    Long actor = user != null ? user.getId() : null;
    history.save(new KegMovementHistory(keg, from, to, externalPartner, actor));
  }
}
