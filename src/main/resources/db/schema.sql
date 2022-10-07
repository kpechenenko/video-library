create table if not exists people
(
    id            serial primary key,
    name          varchar(255) not null,
    date_of_birth date         not null,
    constraint date_of_birth_before_now check (date_of_birth < current_date),
    constraint name_not_empty check (char_length(name) > 0)
);

create table if not exists producing_countries
(
    id   serial primary key,
    name varchar(255) not null,
    constraint unique_country_name unique (name),
    constraint country_name_not_empty check (char_length(name) > 0)
);

create table if not exists movies
(
    id                       serial primary key,
    title                    varchar(255) not null,
    premiere_date            date         not null,
    country_of_production_id int          not null references producing_countries (id) on delete cascade,
    producer_id              int          not null references people (id) on delete set null,
    constraint date_of_premier_before_now check (premiere_date < current_date),
    constraint movie_title_not_empty check (char_length(title) > 0)
);

create table if not exists actors_in_movies
(
    movie_id int references movies (id) on delete cascade,
    actor_id int references people (id) on delete set null,
    primary key (movie_id, actor_id)
);
