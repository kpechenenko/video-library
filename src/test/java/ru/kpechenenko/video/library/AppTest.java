package ru.kpechenenko.video.library;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class AppTest {
    @Test
    public void shouldReturnHelloWorld() {
        assertEquals("Hello World!", App.getGreeting());
    }
}
