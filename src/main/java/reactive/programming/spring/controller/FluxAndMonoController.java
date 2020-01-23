package reactive.programming.spring.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    @GetMapping("/flux")
    public Flux<Integer> returnFlux(){
        return Flux.just(1,2,3,4,5).log().delayElements(Duration.ofSeconds(1));
    }


    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Integer> returnFluxStream(){
        return Flux.just(1,2,3,4,5,6,7,8,9,10).log().delayElements(Duration.ofSeconds(1));
    }

    @GetMapping(value = "/stream-connect", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Integer> returnFluxStreamConnect(){
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).log().delayElements(Duration.ofSeconds(1));
        ConnectableFlux<Integer> publish = integerFlux.publish();
        publish.connect();
        publish.subscribe();
        return Flux.from(publish);
    }
}
