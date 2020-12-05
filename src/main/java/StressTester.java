import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.asynchttpclient.AsyncHttpClient;

import java.net.http.HttpRequest;

public class StressTester {
    private ActorRef actorRef;
    private ActorMaterializer materializer;
    private AsyncHttpClient asyncHttpClient;

    public StressTester(AsyncHttpClient asyncHttpClient, ActorSystem system, ActorMaterializer materializer) {
        this.actorRef = system.actorOf(Props.create(**))
        this.materializer = materializer;
        this.asyncHttpClient = asyncHttpClient;
    }

    public Flow<HttpRequest, HttpResponse, NotUsed> createRoute() {

    }
}
