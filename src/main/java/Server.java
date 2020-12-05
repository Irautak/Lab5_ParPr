import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.asynchttpclient.AsyncHttpClient;

import java.io.IOException;

import java.util.concurrent.CompletionStage;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class Server {
    public static void main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create("routes");
        AsyncHttpClient asyncHttpClient = asyncHttpClient();
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        StressTester stressTester = new StressTester(asyncHttpClient, system, materializer);
        final Flow<HttpRequest, HttpResponse, NotUsed> flow = stressTester.createRoute();
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                flow,
                ConnectHttp.toHost("localhost", 8080),
                materializer
        );
        System.out.println("Server online");
        System.in.read();
        binding.thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> {
                                        system.terminate();
                                        try {
                                            asyncHttpClient.close();
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                });

    }
}
