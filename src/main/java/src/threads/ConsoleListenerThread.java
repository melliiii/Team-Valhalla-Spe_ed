package src.threads;

import src.commands.CommandManager;

import java.util.Scanner;

public class ConsoleListenerThread extends Thread implements SafeThread
{
    private boolean running = true;
    @Override
    public void run() {

        Scanner scanner = new Scanner(System.in);
        String line = "";
        CommandManager commandManager = new CommandManager();
        while((line = scanner.nextLine()) != null && running){
            commandManager.perform(line);
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void terminate() {
        this.running = false;
    }
}
