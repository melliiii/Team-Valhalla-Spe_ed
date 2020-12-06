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
                Trainer trainer = new Trainer(1, 3, 1000, 10);
                trainer.loop();
            }
        }
    }
}
