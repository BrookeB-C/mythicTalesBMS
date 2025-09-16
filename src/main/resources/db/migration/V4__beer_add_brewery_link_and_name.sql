-- Add optional brewery link and fallback brewery name to beer

alter table beer add column if not exists brewery_id bigint;
alter table beer add column if not exists brewery_name varchar(255);

alter table beer add constraint if not exists fk_beer_brewery foreign key (brewery_id) references brewery(id);
create index if not exists idx_beer_brewery on beer(brewery_id);

