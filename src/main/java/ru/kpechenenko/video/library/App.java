package ru.kpechenenko.video.library;

import ru.kpechenenko.video.library.service.VideoLibraryService;

public final class App {
    public static void main(String[] args) {
        var videoLibraryService = new VideoLibraryService();
        var movies = videoLibraryService.findAllMoviesReleasedInLastNYears(30);
        for (var movie : movies) {
            System.out.println(movie);
        }
    }
}
