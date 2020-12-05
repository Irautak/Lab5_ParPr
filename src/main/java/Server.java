import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.asynchttpclient.AsyncHttpClient;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletionStage;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class Server {
    public static void main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create("routes");
        AsyncHttpClient asyncHttpClient = asyncHttpClient();
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        StressTester stressTester = new StressTester(**)
        final Flow<HttpRequest, HttpResponse, NotUsed> flow = stressTester.createRoute(http, system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                flow,
                ConnectHttp.toHost("localhost", 8080),
                materializer
        );
        System.out.println("Server online");
        System.in.read();
        binding.thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());

    }
}
