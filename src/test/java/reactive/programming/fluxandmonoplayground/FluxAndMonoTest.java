package reactive.programming.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoTest {

    @Test
    public void fluxTest() {

        Flux<String> stringFlux = Flux
                .just("Spring", "Spring boot", "Spring reactive programming")
//                .concatWith(Flux.error(new RuntimeException("Exception ocurred!")))
                .concatWith(Flux.just("After error"))
                .log();

        stringFlux
                .subscribe(System.out::println,
                        (e) -> System.out.println("Exception: " + e),
                        () -> System.out.println("Completed Flux"));
    }

    @Test
    public void fluxTest_WithoutError() {

        Flux<String> stringFlux = Flux
                .just("Spring", "Spring boot", "Spring reactive programming")
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring boot")
                .expectNext("Spring reactive programming")
                .verifyComplete();
    }

    @Test
    public void fluxTest_WithError() {

        Flux<String> stringFlux = Flux
                .just("Spring", "Spring boot", "Spring reactive programming")
                .concatWith(Flux.error(new RuntimeException("Error this")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring boot")
                .expectNext("Spring reactive programming")
//                .expectError(RuntimeException.class)
                .expectErrorMessage("Error this")
                .verify();
    }

    @Test
    public void fluxTest_ElmentCount_WithError() {

        Flux<String> stringFlux = Flux
                .just("Spring", "Spring boot", "Spring reactive programming")
                .concatWith(Flux.error(new RuntimeException("Error this")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .expectError(RuntimeException.class)
//                .expectErrorMessage("Error this")
                .verify();
    }

    @Test
    public void fluxTest_WithError_ExpectOneLine() {

        Flux<String> stringFlux = Flux
                .just("Spring", "Spring boot", "Spring reactive programming")
                .concatWith(Flux.error(new RuntimeException("Error this")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring", "Spring boot", "Spring reactive programming") //One line
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void monoTest() {

        Mono<String> stringMono = Mono.just("Spring");

        StepVerifier.create(stringMono.log())
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void monoTest_Error() {

        StepVerifier.create(Mono.error(new RuntimeException("Error")).log())
                .expectError(RuntimeException.class)
                .verify();
    }
}
