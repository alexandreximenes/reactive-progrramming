package reactive.programming.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class InfiniteFlux {

    @Test
    public void infinitFlux() throws InterruptedException {

        int millis = 10;
        Flux.interval(Duration.ofMillis(millis))
                .log()
                .subscribe((s) -> System.out.println("Value "+((s+1)*millis)));

        Thread.sleep(3000);
    }

    @Test
    public void finitFlux() throws InterruptedException {

        Flux<Long> longFlux = Flux.interval(Duration.ofMillis(100))
                .take(3)
                .log();

        StepVerifier.create(longFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    public void finitFluxMap() throws InterruptedException {

        Flux<Integer> longFlux = Flux.interval(Duration.ofMillis(100))
                .map(l -> new Integer(l.intValue()))
                .take(3)
                .log();

        StepVerifier.create(longFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }

    @Test
    public void finitFluxMap_WithDelay() throws InterruptedException {

        Flux<Integer> longFlux = Flux.interval(Duration.ofMillis(100))
                .delayElements(Duration.ofSeconds(1))
                .map(l -> new Integer(l.intValue()))
                .take(3)
                .log();

        StepVerifier.create(longFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }
}
