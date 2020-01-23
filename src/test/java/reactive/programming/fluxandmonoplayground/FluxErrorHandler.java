package reactive.programming.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxErrorHandler {

    @Test
    public void fluxErrorHandling(){
        String[] strings = {"A", "B", "C", "D", "E"};
        String[] string1 = {"Default1", "Default2"};
        Flux<String> stringFlux = Flux.just(strings)
                .concatWith(Flux.error(new RuntimeException("Error Handling")))
                .concatWith(Flux.just("F"))
                .onErrorResume((e) ->
                {
                    System.out.println("Exception is: " + e.getMessage());
                    return Flux.just(string1);
                });

        StepVerifier.create(stringFlux.log())
                .expectNext(strings)
                .expectNext(string1)
//                .expectError(RuntimeException.class)
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandling_Returned(){
        String[] strings = {"A", "B", "C", "D", "E"};
        String[] string1 = {"Default1", "Default2"};
        Flux<String> stringFlux = Flux.just(strings)
                .concatWith(Flux.error(new RuntimeException("Error Handling")))
                .concatWith(Flux.just("F"))
                .onErrorReturn(string1[0]);

        StepVerifier.create(stringFlux.log())
                .expectNext(strings)
                .expectNext(string1[0])
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandling_error_map(){
        String[] strings = {"A", "B", "C", "D", "E"};
        Flux<String> stringFlux = Flux.just(strings)
                .concatWith(Flux.error(new RuntimeException("Error Handling")))
                .concatWith(Flux.just("F"))
                .onErrorMap((e) -> new CustomError(e));

        StepVerifier.create(stringFlux.log())
                .expectNext(strings)
                .expectError(CustomError.class)
                .verify();
    }

    private class CustomError extends Throwable {

        private String msg;
        public CustomError(String message) {
            this.msg = message;
        }

        public CustomError(Throwable cause) {
            super(cause);
            this.msg = cause.getMessage();
        }
    }


    @Test
    public void fluxErrorHandling_error_map_retry(){
        String[] strings = {"A", "B", "C", "D", "E"};
        Flux<String> stringFlux = Flux.just(strings)
                .concatWith(Flux.error(new RuntimeException("Error Handling")))
                .concatWith(Flux.just("F"))
                .onErrorMap((e) -> new CustomError(e))
                .retry(2);

        StepVerifier.create(stringFlux.log())
                .expectNext(strings)
                .expectNext(strings)
                .expectNext(strings)
                .expectError(CustomError.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_error_map_retry_back_off(){
        String[] strings = {"A", "B", "C", "D", "E"};
        Flux<String> stringFlux = Flux.just(strings)
                .concatWith(Flux.error(new RuntimeException("Error Handling")))
                .concatWith(Flux.just("F"))
                .onErrorMap((e) -> new CustomError(e))
                .retryBackoff(2, Duration.ofSeconds(5));

        StepVerifier.create(stringFlux.log())
                .expectNext(strings)
                .expectNext(strings)
                .expectNext(strings)
                .expectError(IllegalStateException.class)
                .verify();
    }
}
