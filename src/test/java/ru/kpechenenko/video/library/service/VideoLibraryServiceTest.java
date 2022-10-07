package ru.kpechenenko.video.library.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.kpechenenko.video.library.DataSource;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.Assert.*;

public final class VideoLibraryServiceTest {
    private final DataSource dataSource;
    private final VideoLibraryService videoLibraryService;

    public VideoLibraryServiceTest() {
        this.dataSource = new DataSource();
        this.videoLibraryService = new VideoLibraryServiceImpl(this.dataSource);
    }

    @Before
    public void createTables() {
        final String CREATE_TABLES_QUERY =
            """
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
                """;
        try (var connection = this.dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_TABLES_QUERY);
        } catch (SQLException e) {
            throw new RuntimeException(
                "Error while initializing tables.",
                e
            );
        }
    }

    @After
    public void dropTables() {
        final String DESTROY_TABLES_QUERY =
            """
                drop table if exists actors_in_movies;
                drop table if exists movies;
                drop table if exists people;
                drop table if exists producing_countries;
                """;
        try (var connection = this.dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.executeUpdate(DESTROY_TABLES_QUERY);
        } catch (SQLException e) {
            throw new RuntimeException(
                "Error while destroying tables.",
                e
            );
        }
    }

    private void fillTables() {
        final String FILL_MOVIES_INFO_QUERY = """
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
            """;
        try (var connection = this.dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.executeUpdate(FILL_MOVIES_INFO_QUERY);
        } catch (SQLException e) {
            throw new RuntimeException(
                "Error while filling values to tables.",
                e
            );
        }
    }

    @Test
    public void containsInsertedMoviesTest() {
        try {
            this.fillTables();
            var movies = this.videoLibraryService.findAllMoviesReleasedInLastNYears(30);
            assertTrue(movies.stream().anyMatch(movie -> "Doctor House".equals(movie.title())));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void shouldNotContainsInsertedMovies() {
        try {
            this.fillTables();
            var movies = this.videoLibraryService.findAllMoviesReleasedInLastNYears(30);
            assertFalse(movies.stream().anyMatch(movie -> "Star wars".equals(movie.title())));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void shouldContainsNoOneMoviesWhenTablesIsEmpty() {
        try {
            var movies = videoLibraryService.findAllMoviesReleasedInLastNYears(1);
            assertTrue(movies.isEmpty());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void shouldContainsActorsOfDoctorHouseMovie() {
        try {
            this.fillTables();
            var actors = videoLibraryService.findAllActorsOfMovie(1);
            assertTrue(
                actors.stream()
                    .anyMatch(p -> "Hugh Laurie".equals(p.name())
                        && LocalDate.of(1959, 6, 11).equals(p.dateOfBirth())
                    )
            );
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void shouldContainActorsOfDifferentMovies() {
        try {
            this.fillTables();
            var popularActors = videoLibraryService.findAllActorsWhoStarredOnNMovies(1);
            assertTrue(
                popularActors.stream()
                    .anyMatch(p -> "Quentin Tarantino".equals(p.name())
                        && LocalDate.of(1963, 3, 27).equals(p.dateOfBirth())
                    )
            );
            assertTrue(
                popularActors.stream()
                    .anyMatch(p -> "Hugh Laurie".equals(p.name())
                        && LocalDate.of(1959, 6, 11).equals(p.dateOfBirth())
                    )
            );
            assertTrue(
                popularActors.stream()
                    .anyMatch(p -> "Lisa Edelstein".equals(p.name())
                        && LocalDate.of(1966, 5, 21).equals(p.dateOfBirth())
                    )
            );
            assertTrue(
                popularActors.stream()
                    .anyMatch(p -> "Tim Roth".equals(p.name())
                        && LocalDate.of(1961, 3, 14).equals(p.dateOfBirth())
                    )
            );
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void shouldContainsActorWhoWereProducer() {
        try {
            this.fillTables();
            var actorsWhoWereProducers = this.videoLibraryService.findAllActorsWhoWereProducers();
            assertTrue(
                actorsWhoWereProducers.stream()
                    .anyMatch(p -> "Quentin Tarantino".equals(p.name())
                        && LocalDate.of(1963, 3, 27).equals(p.dateOfBirth())
                    )
            );
            assertFalse(
                actorsWhoWereProducers.stream()
                    .anyMatch(p -> "David Shore".equals(p.name())
                        && LocalDate.of(1959, 7, 3).equals(p.dateOfBirth())
                    )
            );
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void shouldContainsNoOneMovieAfterDeleteIt() {
        try {
            this.fillTables();
            int releaseYearAgo = 30;
            var moviesBefore = this.videoLibraryService.findAllMoviesReleasedInLastNYears(releaseYearAgo);
            assertFalse(moviesBefore.isEmpty());
            this.videoLibraryService.deleteAllMoviesThatPremierEarlyThatNYearsAgo(releaseYearAgo / 2);
            var moviesAfter = this.videoLibraryService.findAllMoviesReleasedInLastNYears(releaseYearAgo);
            assertTrue(moviesAfter.isEmpty());
        } catch (Exception e) {
            fail();
        }
    }
}