import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.model.*;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.AsyncHttpClient;
import scala.compat.java8.FutureConverters;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.regex.Pattern;


public class StressTester {
    private ActorRef actorRef;
    private ActorMaterializer materializer;
    private AsyncHttpClient asyncHttpClient;

    public StressTester(AsyncHttpClient asyncHttpClient, ActorSystem system, ActorMaterializer materializer) {
        this.actorRef = system.actorOf(Props.create(StoreActor.class));
        this.materializer = materializer;
        this.asyncHttpClient = asyncHttpClient;
    }

    public Flow<HttpRequest, HttpResponse, NotUsed> createRoute() {
        return Flow.of(HttpRequest.class)
                    .map(this::request)
                    .mapAsync(8, this::handleTest)
                    .map(this::finishReq);
    }

    private Test request(HttpRequest httpRequest) {
        Query qry =  httpRequest.getUri().query();
        Optional<String> testURL = qry.get("testUrl");
        Optional<String> count = qry.get("count");
        return new Test(testURL.get(), (int) Long.parseLong(count.get()));
    }

    private CompletionStage<Result> handleTest(Test test) {
        return FutureConverters.toJava(Patterns.ask(this.actorRef, test, 5000))
                                .thenApply(x -> (TestMsg)x)
                                .thenCompose(res -> {
                                    Optional<Result> result = res.getResult();
                                    if (result.isPresent()) {
                                        return CompletableFuture.completedFuture(result.get());
                                    } return doTest(test);
                                });
    }

    private CompletionStage<Result> doTest(Test test) {
        final Sink<Test, CompletionStage<Long> > sink = createSink();
        return Source.from(Collections.singletonList(test))
                .toMat(sink, Keep.right())
                .run(materializer)
                .thenApply(sum -> {
                    Result result = new Result(test, sum/test.getCount());
                    actorRef.tell(result, ActorRef.noSender());
                    return result;
                });
    }

    private Sink<Test, CompletionStage<Long> > createSink() {
        return Flow.<Test>create()
                .mapConcat(tst -> Collections.nCopies(tst.getCount(), tst.getUrl()))
                .mapAsync(8, this::getTime)
                .toMat(Sink.fold(0L, Long::sum), Keep.right());
    }

    private CompletableFuture<Long> getTime(String url) {
        Instant begin = Instant.now();
        return asyncHttpClient.prepareGet(url).execute()
                                .toCompletableFuture()
                                .thenCompose(r -> CompletableFuture.completedFuture(
                                        Duration.between(begin, Instant.now()).getSeconds()
                                ));
    }



    private HttpResponse finishReq(Result result) throws JsonProcessingException {
        actorRef.tell(result, ActorRef.noSender());
        return HttpResponse.create()
                        .withStatus(StatusCodes.OK)
                        .withEntity(ContentTypes.APPLICATION_JSON, ByteString.fromString(
                                new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(result)
                        ));
    }

}
