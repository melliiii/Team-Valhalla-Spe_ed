package src;

import src.commands.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {
    private ConcurrentHashMap<String, Command> commands;

    public CommandManager(){
        commands = new ConcurrentHashMap<>();

        // Add src.commands here! :3
        commands.put("help", new HelpCommand(commands));
        commands.put("exit", new ExitCommand());
        commands.put("quit", new ExitCommand());
        commands.put("start", new StartCommand());
        commands.put("stop", new StopCommand());
        commands.put("get_threads", new GetThreadsCommand());
    }

    public void perform(String line){
        String[] args = line.split(" ");
        if(args.length < 1) return;
        String command = args[0];
        args = removeTheElement(args, 0);

        Command cmd;
        if((cmd = commands.get(command.toLowerCase())) != null){
            cmd.onCommand(args);
        }
    }

    // Function to remove the element
    public static String[] removeTheElement(String[] arr, int index) {

        // If the array is empty
        // or the index is not in array range
        // return the original array
        if (arr == null
                || index < 0
                || index >= arr.length) {

            return arr;
        }

        // Create ArrayList from the array
        List<String> arrayList = new LinkedList<>(Arrays.asList(arr));

        // Remove the specified element
        arrayList.remove(arrayList.get(index));

        String[] list2 = new String[arrayList.size()];
        list2 = arrayList.toArray(list2);
        // return the resultant array
        return list2;
    }

}
