package de.uol.informaticup2021.annfennomelli;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Stage
{
    public static GameMove testAI(Game game, Player self)
    {
        // Randomly changes speed
        // Randomly changes direction if obstacle near
        GameMove result = GameMove.change_nothing;
        boolean change_dir = false;
        if (self.getDirection() == Direction.right &&
                self.getX() > game.getWidth() - self.getSpeed() - 1)
        {
            change_dir = true;
        }
        if (self.getDirection() == Direction.down &&
                self.getY() > game.getHeight() - self.getSpeed() - 1)
        {
            change_dir = true;
        }
        if (self.getDirection() == Direction.left &&
                self.getX() < self.getSpeed() + 1)
        {
            change_dir = true;
        }
        if (self.getDirection() == Direction.up &&
                self.getY() < self.getSpeed() + 1)
        {
            change_dir = true;
        }

        Random rand = new Random();
        if (rand.nextInt() % 100 > 90)
        {
            if (rand.nextBoolean())
            {
                if (self.getSpeed() < 10)
                {
                    result = GameMove.speed_up;
                }
            }
            else
            {
                if (self.getSpeed() < 10)
                {
                    result = GameMove.slow_down;
                }
            }
        }
        else if (rand.nextInt() % 100 > 95)
        {
            change_dir = true;
        }

        if (change_dir)
        {
            if(rand.nextBoolean())
            {
                result = GameMove.turn_left;
            }
            else
            {
                result = GameMove.turn_right;
            }
        }

        return result;
    }

    public static void run()
    {
        Scanner scanner = new Scanner(System.in);
        List<String> names = new ArrayList<String>();
        names.add("Frodo");
        names.add("Gandalf");
        names.add("Sam");
        names.add("Bilbo");
        names.add("Aragorn");
        names.add("Sauron");

        Game game = Game.create(40, 40, names);

        System.out.println("Stage!");
        for (int t = 0; t < 30; ++t)
        {
            scanner.nextLine();
            List<GameMove> moves = new ArrayList<GameMove>();
            for (int i = 0; i < names.size(); ++i)
            {
                GameMove move = testAI(game, game.getPlayer(i));
                moves.add(move);
            }
            game.tick(moves);
            System.out.println(game.getState().getMap());
        }
    }
}
