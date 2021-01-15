package src;

import src.threads.WebBridge;

import java.time.LocalDateTime;

public class Main
{
    public static void main(String[] args)
    {

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
        System.out.println("Start in Performance");
    }

    private static void startInStage(String[] args) {
        System.out.println("Start in Stage");
    }

    private static void startInLive(String[] args) {
        if(args.length < 4){
            System.out.println("Wrong Format! Use: start SERVER_URL API_KEY TIME_SERVER");
            return;
        }

        // Remove slash at the end if it exists
        if(args[1].endsWith("/")) args[1] = args[1].substring(0, args[1].length()-2);

        // Initiate WebBridge
        WebBridge webBridge = new WebBridge();
        webBridge.setUrl(args[1] + "?key=" + args[2]);

        // Start WebBridge
        webBridge.start();
    }
}
