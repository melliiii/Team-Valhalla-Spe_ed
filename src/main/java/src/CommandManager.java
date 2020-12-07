package src;

import src.commands.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {
    private final ConcurrentHashMap<String, Command> commands;

    public CommandManager(){
        this.commands = new ConcurrentHashMap<>();

        // Add src.commands here! :3
        this.commands.put("exit", new ExitCommand());
        this.commands.put("quit", new ExitCommand());
        this.commands.put("start", new StartCommand());
        this.commands.put("stop", new StopCommand());
        this.commands.put("get_threads", new GetThreadsCommand());
    }

    public boolean perform(String line){
        String[] args = line.split(" ");
        if(args.length < 1) return false;
        String command = args[0];
        args = (String[]) removeTheElement(args, 0);

        Command cmd;
        if((cmd = this.commands.get(command.toLowerCase())) != null){
            cmd.onCommand(args);
            return true;
        }
        return false;
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
        List<String> arrayList = new LinkedList<String>(Arrays.asList(arr));

        // Remove the specified element
        arrayList.remove(arrayList.get(index));

        String[] list2 = new String[arrayList.size()];
        list2 = arrayList.toArray(list2);
        // return the resultant array
        return list2;
    }
}
