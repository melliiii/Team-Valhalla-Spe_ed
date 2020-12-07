package src.commands;

import src.CommandManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HelpCommand implements Command{
    ConcurrentHashMap<String, Command> commands;
    public HelpCommand(ConcurrentHashMap<String, Command> commands) {
        this.commands = commands;
    }

    @Override
    public void onCommand(String[] args) {
        System.out.println("List of commands:");
        for(Map.Entry<String, Command> entry : commands.entrySet()){
            System.out.println(entry.getKey() + " - " + entry.getValue().getDescription());
        }
    }

    @Override
    public String getDescription() {
        return "this help page :3; USAGE: help";
    }
}
