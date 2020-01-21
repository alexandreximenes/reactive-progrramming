package reactive.programming.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.Arrays.parallelPrefix;
import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoFactoryTest {

    @Test
    public void fluxIterable(){

        List<String> listStrings = asList("name1, name2, name3,name4, name5");

        Flux<String> fluxString = Flux.fromIterable(listStrings).log();

        StepVerifier.create(fluxString)
                .expectNext("name1, name2, name3,name4, name5")
                .verifyComplete();
    }

    @Test
    public void fluxIterableFilter(){

        List<String> listStrings = asList("name1", "name2", "name3" ,"name4", "name5");

        Flux<String> fluxString = Flux.fromIterable(listStrings)
                .filter(s -> s.endsWith("3")).log();

        StepVerifier.create(fluxString)
                .expectNext("name3")
                .verifyComplete();
    }

    @Test
    public void fluxIterableMap(){

        List<String> listStrings = asList("name1", "name2", "name3" ,"name4", "name5");

        Flux<Integer> fluxString = Flux.fromIterable(listStrings)
                .map(s -> s.length())
                .log();

        StepVerifier.create(fluxString)
//                .expectNext("NAME1", "NAME2", "NAME3" ,"NAME4", "NAME5")
                .expectNext(5,5,5,5,5)
                .verifyComplete();
    }

    @Test
    public void fluxIterableMapFilter(){

        List<String> listStrings = asList("name1", "name2", "name3" ,"name4", "name5");

        Flux<Integer> fluxString = Flux.fromIterable(listStrings)
                .filter(s -> s.endsWith("3"))
                .repeat(2) //flux
                .map(s -> s.length())
                .log();

        StepVerifier.create(fluxString)
                .expectNext(5,5,5)
                .verifyComplete();
    }

    @Test
    public void fluxIterableFlatMap(){

        List<String> alphabet = asList("a", "b", "c");

        Flux<String> fluxString = Flux.fromIterable(alphabet)
                .flatMap(s -> {
                    return Flux.fromIterable(convertList(s));
                })
                .log();

        StepVerifier.create(fluxString)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void fluxIterableFlatMap_usingParalell(){

        List<String> alphabet = asList("a", "b", "c");

        Flux<String> fluxString = Flux.fromIterable(alphabet)
                .window(2)
                .flatMap(s -> s.map(this::convertList).subscribeOn(parallel()))
                .flatMap(s -> Flux.fromIterable(s))
                .log();

        StepVerifier.create(fluxString)
                .expectNextCount(6)
                .verifyComplete();
    }

    //Para manter a ordem
    @Test
    public void fluxIterableFlatMap_usingParalell_concatMap(){

        List<String> alphabet = asList("a", "b", "c");

        Flux<String> fluxString = Flux.fromIterable(alphabet)
                .window(2)
                .concatMap(s -> s.map(this::convertList).subscribeOn(parallel()))
                .flatMap(s -> Flux.fromIterable(s))
                .log();

        StepVerifier.create(fluxString)
                .expectNextCount(6)
                .verifyComplete();
    }

    //mais performatico que o concatMap
    @Test
    public void fluxIterableFlatMap_usingParalell_flatMapSequential(){

        List<String> alphabet = asList("a", "b", "c");

        Flux<String> fluxString = Flux.fromIterable(alphabet)
                .window(1)
                .flatMapSequential(s -> s.map(this::convertList).subscribeOn(parallel()))
                .flatMap(s -> Flux.fromIterable(s))
                .log();

        StepVerifier.create(fluxString)
                .expectNextCount(6)
                .verifyComplete();
    }

    private List<String> convertList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return asList(s, "newValue");
    }

    @Test
    public void fluxArray(){
        String[] strings = {"name1, name2, name3,name4, name5"};

        Flux<String> fluxString = Flux.fromArray(strings).log();

        StepVerifier.create(fluxString)
                .expectNext("name1, name2, name3,name4, name5")
                .verifyComplete();


    }

    @Test
    public void flux(){
        List<String> listStrings = asList("name1, name2, name3,name4, name5");

        Flux<String> fromStream = Flux.fromStream(listStrings.stream()).log();

        StepVerifier.create(fromStream)
                .expectNext("name1, name2, name3,name4, name5")
                .verifyComplete();


    }

    @Test
    public void monoUsingJustOrEmpty(){

        Mono<String> objectMono = Mono.justOrEmpty(Optional.empty());
        StepVerifier.create(objectMono.log())
                .verifyComplete();
    }

    @Test
    public void monoUsingSupplier(){

        Supplier<String> stringSupplier =  () -> "Name1";

        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);

        System.out.println(stringSupplier.get());

        StepVerifier.create(stringMono.log())
                .expectNext("Name1")
                .verifyComplete();
    }

    @Test
    public void monoUsingRange(){

        Flux<Integer> range = Flux.range(1, 5);

        StepVerifier.create(range.log())
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }

    @Test
    public void testingWithoutVirtualTime(){

        Flux<Long> take = Flux.interval(Duration.ofSeconds(1)).take(3);

        StepVerifier.create(take.log())
                .expectSubscription()
                .expectNext(0l,1l,2l)
                .verifyComplete();
    }

    @Test
    public void testingWithVirtualTime(){

        VirtualTimeScheduler.getOrSet();

        Flux<Long> take = Flux.interval(Duration.ofSeconds(3)).take(3);

        StepVerifier.withVirtualTime(() -> take.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(0l,1l,2l)
                .verifyComplete();
    }

    @Test
    public void mergeFluxAndWithVirtualTime(){

        VirtualTimeScheduler.getOrSet();

        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("C", "D", "E").delayElements(Duration.ofSeconds(1));

        Flux<String> merge = Flux.merge(flux1, flux2);

        StepVerifier.withVirtualTime(() -> merge.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(6))
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void mergeFluxAndConcat(){

        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("C", "D", "E").delayElements(Duration.ofSeconds(1));

        Flux<String> concat = Flux.concat(flux1, flux2);

        StepVerifier.create(concat.log())
                .expectSubscription()
                .expectNextCount(6)
                .verifyComplete();
    }


    @Test
    public void mergeFluxAndZip(){

        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("C", "D", "E").delayElements(Duration.ofSeconds(1));

        Flux<String> zip = Flux.zip(flux1, flux2, (z1, z2) -> { return z1.concat(z2); } );

        StepVerifier.create(zip.log())
                .expectSubscription()
                .expectNext("AC", "BD", "CE")
                .verifyComplete();
    }
}
