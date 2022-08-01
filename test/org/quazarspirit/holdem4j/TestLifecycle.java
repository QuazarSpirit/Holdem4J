package org.quazarspirit.holdem4j;

import org.junit.jupiter.api.AfterAll;

import java.util.concurrent.TimeUnit;

public class TestLifecycle {
    @AfterAll
    public static void tearDown() throws InterruptedException {
        // Waiting to let Loggers finish logging after test
        TimeUnit.SECONDS.sleep(1);
    }
}
