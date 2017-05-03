package sample;

public class Data {
    private final String alcocode;
    private final Integer counter;

    public Data(String alcocode, Integer counter) {
        this.alcocode = alcocode;
        this.counter = counter;
    }

    public String getAlcocode() {
        return alcocode;
    }

    public Integer getCounter() {
        return counter;
    }
}
