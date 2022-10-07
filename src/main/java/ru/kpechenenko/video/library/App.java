package ru.kpechenenko.video.library;

import ru.kpechenenko.video.library.service.VideoLibraryServiceImpl;

public final class App {
    public static void main(String[] args) {
        var videoLibraryService = new VideoLibraryServiceImpl(new DataSource());
        var movies = videoLibraryService.findAllMoviesReleasedInLastNYears(30);
        for (var movie : movies) {
            System.out.println(movie);
        }
    }
}
