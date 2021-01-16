package src.threads;

import src.algorithmic.AlgorithmicAI;
import src.algorithmic.Odin;
import src.algorithmic.VariantTracker;
import src.algorithmic.thor.Thor;
import src.game.Game;
import src.game.GameMove;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PerformanceTest
{
    // Calculate Win Rates of Games
    private int gameCounter;
    private double[] scores;
    private int[] wins;
    private AlgorithmicAI[] ais;
    private Random random = new Random();

    public void playGame(Game game)
    {
        for (int i = 0; i < game.getPlayerCount(); ++i)
        {
            ais[i].setGame(game);
        }

        while (game.isRunning())
        {
            List<GameMove> moves = new ArrayList<>();
            for (int i = 0; i < game.getPlayerCount(); ++i)
            {
                GameMove move = ais[i].decide();
                moves.add(move);
            }
            game.tick(moves);
            System.out.print("#");
        }

        for (int i = 0; i < game.getPlayerCount(); ++i)
        {
            scores[i] += game.getDeath(i);
            wins[i] += (game.getDeath(i) == game.getPlayerCount() - 1) ? 1 : 0;
        }
        gameCounter++;
    }

    public void setAIs(AlgorithmicAI[] ais)
    {
        this.ais = ais;
        this.scores = new double[ais.length];
        this.wins = new int[ais.length];
    }

    public double getWinRate(int aiId)
    {
        return (double) wins[aiId] / gameCounter;
    }

    public double getScore(int aiId)
    {
        return scores[aiId] / gameCounter;
    }

    public static void runTest()
    {
        int playerCount = 2;
        PerformanceTest test = new PerformanceTest();
        AlgorithmicAI[] ais = new AlgorithmicAI[playerCount];

        List<String> players = new LinkedList<>();
        for (int i = 0; i < ais.length; ++i)
        {
            players.add(String.valueOf(i));
        }
        Game game = Game.create(50, 50, players);

        for (int i = 0; i < playerCount; ++i)
        {
            if (i == 1)
            {
                Thor a = new Thor(game, i);
                a.setIterations(500);
                a.setExploration(0);
                a.setMemorizeTree(true);
                ais[i] = a;
            }
            else
            {
                Thor a = new Thor(game, i);
                a.setIterations(1000);
                a.setMemorizeTree(true);
                a.setExploration(0);
                ais[i] = a;
            }
        }

        test.setAIs(ais);

        int counter = 0;
        while (true)
        {
            test.playGame(game.cloneGame());
            counter++;
            System.out.println("\nResults after " + counter + " games: ");
            for (int i = 0; i < ais.length; ++i)
            {
                DecimalFormat format = new DecimalFormat("#.###");
                System.out.println(i + ": Score: " + format.format(test.getScore(i)) +
                        " Win rate: " + format.format(test.getWinRate(i)));
            }
        }
    }
}
