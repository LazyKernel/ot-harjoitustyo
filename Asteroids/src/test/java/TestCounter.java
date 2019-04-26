public class TestCounter {
    private int[] counter;

    public TestCounter(int numCounters) {
        counter = new int[numCounters];
    }

    public int get(int i) {
        return counter[i];
    }

    public void increment(int i) {
        counter[i]++;
    }
}
