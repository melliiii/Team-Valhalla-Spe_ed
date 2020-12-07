package src.commands;

import src.Main;
import src.SafeThread;

public class StopCommand implements Command{
    @Override
    public void onCommand(String[] args) {

        boolean found = false;
        for(String str : args){
            try {
                int id = Integer.parseInt(str);
                Main.getRunningThreads().get(id).terminate();
                System.out.println("Thread with ID " + id + " will be terminated shortly...");
                return;
            }catch(NumberFormatException ignored) { }
            for(SafeThread thread : Main.getRunningThreads()){
                if(thread.toString().contains(str) && thread.isRunning()){
                    found = true;
                    System.out.println("Thread with ID " + Main.getRunningThreads().indexOf(thread) + " will be terminated shortly...");
                    thread.terminate();
                }
            }
        }
        if(!found){
            System.out.println("No such named thread...");
            new GetThreadsCommand().onCommand(null);
        }
    }

    @Override
    public String getDescription() {
        return "terminates a given thread (either by id or name); USAGE: stop [name/id]";
    }
}
