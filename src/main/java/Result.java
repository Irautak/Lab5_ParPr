import java.io.Serializable;

public class Result implements Serializable {
    private Long time;
    private Test test;

    public Result(Test test, Long time) {
        this.test = test;
        this.time = time;
    }

    public Test getTest() {
        return test;
    }

    public Long getTime() {
        return time;
    }
}
