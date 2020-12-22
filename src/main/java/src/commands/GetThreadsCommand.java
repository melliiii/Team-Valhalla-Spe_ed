package src.commands;

import src.Main;
import src.threads.SafeThread;

public class GetThreadsCommand implements Command{
    @Override
    public void onCommand(String[] args) {
        for(SafeThread thread : Main.getRunningThreads()){
            System.out.println("- ID: " + Main.getRunningThreads().indexOf(thread) + "; Name: " + thread.toString() + "; Status: " + (thread.isRunning() ? "running..." : "terminated!"));
        }
        if(Main.getRunningThreads().size() == 0){
            System.out.println("-> No Threads running :c");
        }
    }

    @Override
    public String getDescription() {
        return "displays a list of information for running side threads; USAGE: get_threads";
    }
}
