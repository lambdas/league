create table results(
  id serial not null,
  date date not null,
  road_team varchar(255) not null,
  home_team varchar(255) not null,
  road_score smallint not null,
  home_score smallint not null,
  visible boolean not null,
  primary key(id),
  unique (date, road_team, home_team)
);
