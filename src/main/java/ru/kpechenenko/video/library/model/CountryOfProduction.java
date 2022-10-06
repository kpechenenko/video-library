package ru.kpechenenko.video.library.model;

public record CountryOfProduction(Integer id, String name) {
    @Override
    public String toString() {
        return "'%s'".formatted(this.name);
    }
}
