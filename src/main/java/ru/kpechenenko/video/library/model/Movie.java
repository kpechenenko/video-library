package ru.kpechenenko.video.library.model;

import java.time.LocalDate;
import java.util.List;

public record Movie(
    Integer id,
    String title,
    LocalDate premierDate,
    CountryOfProduction country,
    Person producer,
    List<Person> actors
) {
    @Override
    public String toString() {
        return "Movie: '%s', %s, %s, producer %s, actors %s".formatted(title, premierDate, country, producer, actors);
    }
}
