import akka.actor.AbstractActor;

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
    }

    private void getTest(Test testURL) {
        getSender().tell();
    }
}
