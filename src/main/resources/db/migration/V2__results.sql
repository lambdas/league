create table results(
  id serial not null,
  season integer not null,
  date date not null,
  game_type game_type not null,
  road_team varchar(4) not null,
  home_team varchar(4) not null,
  road_score smallint not null,
  home_score smallint not null,
  primary key(id),
  unique (date, road_team, home_team)
);
