package reactive.programming.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class BackPressure {

    @Test
    public void back_pressure_test() {

        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    public void back_pressure() {

        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();

        integerFlux.subscribe((element) -> System.out.println("Value is " + element)
                , (e) -> System.out.println(e)
                , () -> System.out.println("Done"),
                (subscription -> subscription.request(2)));
    }
    @Test
    public void back_pressure_cancel() {

        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();

        integerFlux.subscribe((element) -> System.out.println("Value is " + element)
                , (e) -> System.out.println(e)
                , () -> System.out.println("Done"),
                (subscription -> subscription.cancel()));
    }

    @Test
    public void back_pressure_custom() {

        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();

        integerFlux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println("Value is "+ value);
                if(value == 4)
                    cancel();
            }
        });
    }
}
