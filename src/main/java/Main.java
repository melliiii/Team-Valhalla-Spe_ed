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
                Stage.run();
            }
            else if (args[0].contains("trainer"))
            {
                Trainer.run();
            }
        }
    }
}
