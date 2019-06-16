create table seasons(
  start_year smallint not null,
  regular_season_start date not null,
  regular_season_end date not null,
  playoffs_start date not null,
  playoffs_end date not null,
  primary key(start_year)
);
