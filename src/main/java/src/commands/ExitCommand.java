package src.commands;

import src.Main;

public class ExitCommand implements Command {
    @Override
    public void onCommand(String[] args) {
        Main.stopThreads();
        System.exit(0);
    }

    @Override
    public String getDescription() {
        return "exit the program; USAGE: exit/quit";
    }
}
