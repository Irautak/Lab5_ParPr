import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import java.util.HashMap;

public class StoreActor extends AbstractActor {
    private HashMap<Test, Long> storage;

    public StoreActor() {
        this.storage = new HashMap<>();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Test.class,this::getTest)
                .match(Result.class, this::putResult)
                .build();
    }

    private void getTest(Test testURL) {
        getSender().tell(new TestMsg(new Result(testURL, storage.get(testURL))), ActorRef.noSender());
    }

    private void putResult (Result result) {
        storage.put(result.getTest(), result.getTime());
    }
}
