package pl.intx;

import java.util.function.Supplier;

public class App {
    public static void main(String[] args) {
        Retry.times(5).run(() -> System.out.println("HELLO"));

        var hello = new HelloTimes();
        Retry.times(5).run(() -> hello.println("HellO!"));

        var value = Retry.times(5).run(() -> hello.getSize("Hello"));
        System.out.println(value);
    }

    static class HelloTimes {
        int counter = 0;

        void println(String value) {
            counter++;
            if (counter < 3)
                throw new RuntimeException("Failed");
            System.out.println(value);
        }

        public int getSize(String s) {
            return s.length();
        }
    }
}
