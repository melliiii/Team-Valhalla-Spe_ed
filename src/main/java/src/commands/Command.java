package src.commands;

public interface Command {
    void onCommand(String[] args);
    String getDescription();
}
