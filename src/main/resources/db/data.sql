insert into producing_countries(name)
values ('USA');

-- Doctor House
insert into people (name, date_of_birth)
values ('David Shore', '3-07-1959'),
       ('Hugh Laurie', '11-06-1959'),
       ('Lisa Edelstein', '21-05-1966'),
       ('Robert Sean Leonard', '28-02-1969'),
       ('Omar Epps', '20-07-1979'),
       ('Jesse Spencer', '12-02-1979'),
       ('Jennifer Morrison', '12-04-1979');

insert into movies(title, premiere_date, country_of_production_id, producer_id)
VALUES ('Doctor House', '16-11-2004', 1, 1);

insert into actors_in_movies(movie_id, actor_id)
values (1, 2),
       (1, 3),
       (1, 4),
       (1, 5),
       (1, 6),
       (1, 7);

-- Reservoir Dogs
insert into people (name, date_of_birth)
values ('Quentin Tarantino', '27-03-1963'),
       ('Harvey Keitel', '13-05-1939'),
       ('Michael Madsen', '25-09-1957'),
       ('Steve Buscemi', '13-12-1957'),
       ('Tim Roth', '14-03-1961'),
       ('Chris Penn', '10-10-1995'),
       ('Lawrence Tierney', '15-03-1919'),
       ('Kirk Baltz', '14-09-1959'),
       ('Edward Bunker', '31-12-1933');

insert into movies(title, premiere_date, country_of_production_id, producer_id)
VALUES ('Reservoir Dogs', '21-12-1992', 1, 8);

insert into actors_in_movies(movie_id, actor_id)
values (2, 8),
       (2, 9),
       (2, 10),
       (2, 11),
       (2, 12),
       (2, 13),
       (2, 14),
       (2, 15),
       (2, 16);
