package src.threads;

public interface SafeThread {
    boolean isRunning();
    void terminate();
    void start();
}
