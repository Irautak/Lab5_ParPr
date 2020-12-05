import java.io.Serializable;

public class Test implements Serializable {
    private String url;
    private Integer count;

    public Test(String url, Integer count) {
        this.url = url;
        this.count = count;
    }

    public Integer getCount() {
        return count;
    }

    public String getUrl() {
        return url;
    }
}
