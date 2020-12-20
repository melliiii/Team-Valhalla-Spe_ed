package src.threads;

import src.SafeThread;

public class WebBridgeThread extends Thread implements SafeThread {

    private boolean running = true;

    @Override
    public void run() {
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void terminate() {
        running = false;
    }
}
