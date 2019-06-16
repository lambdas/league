create table user_results(
  user_id integer references users(id) not null,
  result_id integer references results(id) not null,
  visible boolean not null,
  unique(user_id, result_id)
);
