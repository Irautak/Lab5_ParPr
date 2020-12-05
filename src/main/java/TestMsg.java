import java.util.Optional;

public class TestMsg {
    private Result result;

    public TestMsg(Result result) {
        this.result = result;
    }

    public Optional<Result> getResult() {
        if (result.getTime() != null) {
            return Optional.ofNullable(result);
        } else {
            return Optional.empty();
        }
    }
}
