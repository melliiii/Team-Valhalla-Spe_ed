public class Main
{
    public static void main(String[] args)
    {
        if (args.length > 0)
        {
            if (args[0].contains("live"))
            {
                WebBridge.run();
            }
            else if (args[0].contains("stage"))
            {
                Stage stage = new Stage();
                stage.loop();
            }
            else if (args[0].contains("trainer"))
            {

                Trainer trainer = new Trainer(5, 6, 500, 10);
                if(args.length >= 2){
                    if(args[1].equalsIgnoreCase("nogui")){
                        trainer.GUI_MODE = false;
                    }
                }
                trainer.loop();
            }
        }
    }
}
