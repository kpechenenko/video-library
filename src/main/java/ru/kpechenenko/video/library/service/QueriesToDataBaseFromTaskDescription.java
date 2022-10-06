package ru.kpechenenko.video.library.service;

import ru.kpechenenko.video.library.model.Movie;
import ru.kpechenenko.video.library.model.Person;

import java.util.List;

public interface QueriesToDataBaseFromTaskDescription {
    List<Movie> findAllMoviesReleasedInLastNYears(Integer numberOfYears);

    List<Person> findAllActorsOfMovie(Integer movieId);

    List<Person> findAllActorsWhoStarredOnNMovies(Integer numberOfMovies);

    List<Person> findAllActorsWhoWereProducers();

    int deleteAllMoviesThatPremierEarlyThatNYearsAgo(Integer numberOfYears);
}
