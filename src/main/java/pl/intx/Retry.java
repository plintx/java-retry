package pl.intx;

import org.apache.log4j.Logger;

import java.util.function.Supplier;

public class Retry {
    private static Logger logger = Logger.getLogger(Retry.class.getName());
    private final int maxRetries;
    private int retryCounter;

    private Retry(Integer maxiRetries) {
        this.maxRetries = maxiRetries;
        this.retryCounter = 0;
    }

    public static Retry times(Integer maxiRetries) {
        return new Retry(maxiRetries);
    }
    public void run(Runnable runnable) {
        this.run(() -> {
            runnable.run();
            return null;
        });
    }

    public <T> T run(Supplier<T> closure) {
        while (retryCounter < maxRetries) {
            try {
                logger.info("Execution try: " + retryCounter);
                return closure.get();
            } catch (Exception ex) {
                retryCounter++;
                if (retryCounter >= maxRetries) {
                    logger.error("Execution failed on last retry.");
                    break;
                }
                logger.warn("Execution failed on retry " + retryCounter + " of " + maxRetries + " error: " + ex);
            }
        }
        throw new RuntimeException("Execution failed on all of " + maxRetries + " retries");
    }

    public int getRetryCounter() {
        return this.retryCounter;
    }

}
