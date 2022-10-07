package ru.kpechenenko.video.library.service;

import ru.kpechenenko.video.library.model.CountryOfProduction;
import ru.kpechenenko.video.library.model.Movie;
import ru.kpechenenko.video.library.model.Person;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.kpechenenko.video.library.DataSource;

public final class VideoLibraryServiceImpl implements VideoLibraryService {
    private final DataSource dataSource;

    public VideoLibraryServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final String FIND_MOVIES_RELEASED_RECENTLY_QUERY =
        """
            select
                m.id as movie_id,
                m.title as movie_title,
                m.premiere_date as movie_premier_date,
                c.id as country_id,
                c.name as country_name,
                p.id as person_id,
                p.name as person_name,
                p.date_of_birth as person_date_of_birth
            from movies m
                     left join producing_countries c on c.id = m.country_of_production_id
                     left join people p on p.id = m.producer_id
            where (m.premiere_date >= current_date - interval '1 year' * ?)
              and (m.premiere_date < current_date)
            """;

    private static final String FIND_ACTORS_OF_MOVIE_QUERY =
        """
            select
                p.id,
                p.name,
                p.date_of_birth
            from actors_in_movies aim
                     left join people p on aim.actor_id = p.id
            where aim.movie_id = ?
            """;

    private static final String FIND_ACTORS_WHO_STARRED_ON_N_MOVIES_QUERY =
        """
            select
                p.id,
                p.name,
                p.date_of_birth
            from actors_in_movies aim
                     left join people p on aim.actor_id = p.id
            group by p.id
            having count(movie_id) >= ?
            """;

    private static final String FIND_ACTORS_WHO_WERE_PRODUCERS_QUERY =
        """
            select
                p.id,
                p.name,
                p.date_of_birth
            from movies m
                     left join people p on m.producer_id = p.id
            where producer_id in (select actor_id from actors_in_movies)
            """;

    private static final String DELETE_ALL_OLD_MOVIES_QUERY =
        """
            delete
            from movies
            where (premiere_date <= current_date - interval '1 year' * ?)
            """;

    @Override
    public List<Movie> findAllMoviesReleasedInLastNYears(Integer numberOfYears) {
        try (
            var connection = this.dataSource.getConnection();
            var preparedStatement = connection.prepareStatement(FIND_MOVIES_RELEASED_RECENTLY_QUERY)
        ) {
            preparedStatement.setInt(1, numberOfYears);
            try (var resultSet = preparedStatement.executeQuery()) {
                List<Movie> movies = new ArrayList<>();
                while (resultSet.next()) {
                    movies.add(
                        new Movie(
                            resultSet.getInt("movie_id"),
                            resultSet.getString("movie_title"),
                            resultSet.getDate("movie_premier_date").toLocalDate(),
                            new CountryOfProduction(
                                resultSet.getInt("country_id"),
                                resultSet.getString("country_name")
                            ),
                            new Person(
                                resultSet.getInt("person_id"),
                                resultSet.getString("person_name"),
                                resultSet.getDate("person_date_of_birth").toLocalDate()
                            ),
                            this.findAllActorsOfMovie(resultSet.getInt("movie_id"))
                        )
                    );
                }
                return movies;
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                "Error when searching all movies released in last %d years.".formatted(numberOfYears),
                e
            );
        }
    }

    @Override
    public List<Person> findAllActorsOfMovie(Integer movieId) {
        try (
            var connection = this.dataSource.getConnection();
            var preparedStatement = connection.prepareStatement(FIND_ACTORS_OF_MOVIE_QUERY)
        ) {
            preparedStatement.setInt(1, movieId);
            try (var resultSet = preparedStatement.executeQuery()) {
                return this.extractActors(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                "Error when searching actors of movie with id #%d.".formatted(movieId),
                e
            );
        }
    }

    @Override
    public List<Person> findAllActorsWhoStarredOnNMovies(Integer numberOfMovies) {
        try (
            var connection = this.dataSource.getConnection();
            var preparedStatement = connection.prepareStatement(FIND_ACTORS_WHO_STARRED_ON_N_MOVIES_QUERY)
        ) {
            preparedStatement.setInt(1, numberOfMovies);
            try (var resultSet = preparedStatement.executeQuery()) {
                return this.extractActors(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                "Error when searching actors who starred on %d movies.".formatted(numberOfMovies),
                e
            );
        }
    }

    @Override
    public List<Person> findAllActorsWhoWereProducers() {
        try (
            var connection = this.dataSource.getConnection();
            var preparedStatement = connection.prepareStatement(FIND_ACTORS_WHO_WERE_PRODUCERS_QUERY);
            var resultSet = preparedStatement.executeQuery()
        ) {
            return this.extractActors(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(
                "Error when searching actors who were producers.",
                e
            );
        }
    }

    @Override
    public int deleteAllMoviesThatPremierEarlyThatNYearsAgo(Integer numberOfYears) {
        try (
            var connection = this.dataSource.getConnection();
            var preparedStatement = connection.prepareStatement(DELETE_ALL_OLD_MOVIES_QUERY)
        ) {
            preparedStatement.setInt(1, numberOfYears);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(
                "Error when deleting movies that premier early that %d years ago.".formatted(numberOfYears),
                e
            );
        }
    }

    private List<Person> extractActors(ResultSet resultSet) throws SQLException {
        List<Person> actors = new ArrayList<>();
        while (resultSet.next()) {
            actors.add(
                new Person(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getDate("date_of_birth").toLocalDate()
                )
            );
        }
        return actors;
    }
}
