package src.commands;

import src.*;
import src.threads.WebBridgeThread;

public class StartCommand implements Command {

    @Override
    public void onCommand(String[] args) {
        if (args.length > 0)
        {
            if (args[0].contains("live"))
            {
                WebBridgeThread webBridgeThread = new WebBridgeThread();
                Main.startThread(webBridgeThread);
            }
            else if (args[0].contains("stage"))
            {
                Stage stage = new Stage(true);
                stage.loop();
            }
            else if (args[0].contains("trainer"))
            {
                boolean trainerRunning = false;
                for(SafeThread thread : Main.getRunningThreads()){
                    if(thread.toString().contains("trainer") && thread.isRunning()){
                        trainerRunning = true;
                    }
                }
                if(trainerRunning){
                    System.out.println("-> Only one trainer at a time, otherwise you might get confused :c");
                }

                System.out.println("Starting trainer....");
                Trainer trainer = new Trainer(15, 6, 500, 10);
                if(args.length >= 2){
                    if(args[1].equalsIgnoreCase("nogui")){
                        trainer.GUI_MODE = false;
                    }
                }
                trainer.setName("trainer");
                Main.startThread(trainer);
            }
        }
    }
}
