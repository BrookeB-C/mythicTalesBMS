-- Add optional brewery link and fallback brewery name to beer

alter table beer add column  brewery_id bigint;
alter table beer add column  brewery_name varchar(255);

alter table beer add constraint  fk_beer_brewery foreign key (brewery_id) references brewery(id);
create index  idx_beer_brewery on beer(brewery_id);

