create table users(
  id serial not null,
  email varchar(50) not null,
  primary key(id)
);

insert into users(email) values
  ('user1'),
  ('user2');
