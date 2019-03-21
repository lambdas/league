create table teams(
  code varchar(4) not null,
  name varchar(64) not null,
  conference varchar(8) not null,
  division varchar(16) not null,
  primary key(code)
);
