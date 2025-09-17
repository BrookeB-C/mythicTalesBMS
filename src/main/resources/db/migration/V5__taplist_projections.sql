-- Projections: taplist_view for fast read of venue tap state

create table if not exists taplist_view (
  tap_id bigint primary key,
  venue_id bigint,
  beer_name varchar(255),
  style varchar(255),
  abv double,
  remaining_ounces double,
  total_ounces double,
  fill_percent integer,
  updated_at timestamp not null
);

create index if not exists idx_tlv_venue on taplist_view(venue_id);

