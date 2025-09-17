-- Keg movement history: records movements between venues or to external partners
CREATE TABLE IF NOT EXISTS keg_movement_history (
  id BIGSERIAL PRIMARY KEY,
  keg_id BIGINT NOT NULL REFERENCES keg(id) ON DELETE CASCADE,
  from_venue_id BIGINT NULL REFERENCES venue(id) ON DELETE SET NULL,
  to_venue_id BIGINT NULL REFERENCES venue(id) ON DELETE SET NULL,
  external_partner VARCHAR(255) NULL,
  actor_user_id BIGINT NULL,
  moved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_keg_move_keg ON keg_movement_history(keg_id);
CREATE INDEX IF NOT EXISTS idx_keg_move_time ON keg_movement_history(moved_at);
CREATE INDEX IF NOT EXISTS idx_keg_move_to_venue ON keg_movement_history(to_venue_id);

