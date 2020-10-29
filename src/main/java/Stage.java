import java.util.ArrayList;
import java.util.List;

public class Stage
{
    public static void run()
    {
        List<String> names = new ArrayList<String>();
        names.add("Frodo");
        names.add("Gandalf");
        names.add("Sam");
        names.add("Bilbo");
        names.add("Aragorn");
        names.add("Sauron");

        Game game = Game.create(40, 40, names);
    }
}
