package ru.kpechenenko.video.library.model;

import java.time.LocalDate;

public record Person(Integer id, String name, LocalDate dateOfBirth) {
    @Override
    public String toString() {
        return "'%s', %s".formatted(name, dateOfBirth);
    }
}
