package src.threads;

import src.algorithmic.AlgorithmicAI;
import src.algorithmic.thor.Thor;
import src.game.Game;
import src.game.GameMove;

import java.text.DecimalFormat;
import java.util.*;

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
            List<GameMove> moves = new ArrayList<GameMove>();
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
        int playerCount = 4;
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
            Thor a = new Thor(game, i);
            a.setIterations(200);
            switch (i)
            {
                case 0:
                case 1:
                    a.setMemorizeTree(false);
                    break;
                case 2:
                case 3:
                    a.setMemorizeTree(true);
                    break;
            }
            a.setExploration(Math.sqrt(2));
            ais[i] = a;
            players.set(i, a.getRandomMethod().toString());
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