package tasks;

import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseTaskTest<T extends Task> {

    protected Duration duration;
    protected LocalDateTime localDateTime;

    @BeforeEach
    public void setUp() {
        duration = Duration.ofMinutes(30);
        localDateTime = LocalDateTime.now();
    }
}
