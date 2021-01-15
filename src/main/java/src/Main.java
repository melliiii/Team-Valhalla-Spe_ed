package src;

import src.threads.PerformanceTest;
import src.threads.Stage;
import src.threads.WebBridge;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        try {
            File headerFile = new File("header.txt");
            Scanner headerScanner = null;
            headerScanner = new Scanner(headerFile);
            while(headerScanner.hasNextLine()){
                System.out.println(headerScanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        TimeSync timeSync = new TimeSync("https://msoll.de/spe_ed_time");
        try {
            System.out.println("Delay zum Server: " + timeSync.calculateDelay(LocalDateTime.now()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(args.length >= 1){
            switch(args[0]){
                case "live":
                    startInLive(args);
                    break;
                case "stage":
                    startInStage(args);
                    break;
                case "performance":
                    startInPerformance(args);
                    break;
                default:
                    System.out.println("Applicable arguments are: live, stage, performance");
                    break;
            }
        }else{
            System.out.println("You need to at least start with one argument (live, stage, performance)");
        }
    }

    private static void startInPerformance(String[] args) {
        PerformanceTest.runTest();
    }

    private static void startInStage(String[] args) {
        boolean gui = Arrays.asList(args).contains("gui");
        System.out.println("Starting with UI: " + (gui ? "yes" : "no"));
        Stage stage = new Stage("Stage", false, gui);
        stage.loop();
    }

    private static void startInLive(String[] args) {
        if(args.length < 4){
            System.out.println("Wrong Format! Use: start SERVER_URL API_KEY TIME_SERVER");
            return;
        }

        boolean gui = Arrays.asList(args).contains("gui");
        System.out.println("Starting with UI: " + (gui ? "yes" : "no") + " (add \"gui\" flag to display UI)");

        // Remove slash at the end if it exists
        if(args[1].endsWith("/")) args[1] = args[1].substring(0, args[1].length()-2);

        // Initiate WebBridge
        WebBridge webBridge = new WebBridge(false, gui);
        webBridge.setUrl(args[1] + "?key=" + args[2]);

        // Start WebBridge
        webBridge.start();
    }
}
