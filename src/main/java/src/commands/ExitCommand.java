package src.commands;

import src.Main;

public class ExitCommand implements Command {
    @Override
    public void onCommand(String[] args) {
        // TODO Shutdown
        Main.stopThreads();
        System.exit(0);
    }
}
