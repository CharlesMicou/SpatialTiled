package dummy;

public class DummyWorkerMain {
    public static void main(String[] args) {
        // Do nothing. The existence of this file allows the spatial cli to conveniently dump its dependencies to the
        // correct places.
        System.out.println("For some reason, someone decided to run a dummy worker " +
                "with the following args:");
        for (String arg : args) {
            System.out.println(arg);
        }
        System.exit(0);
    }
}
