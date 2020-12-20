package src;

public interface SafeThread {
    boolean isRunning();
    void terminate();
    void start();
}
