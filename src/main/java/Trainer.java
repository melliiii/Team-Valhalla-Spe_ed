import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jneat.*;


@SuppressWarnings("SuspiciousNameCombination")
public class Trainer
{
    private final Population population;
    private final int windowRadius;
    private final int player_count;
    private final int iterations;
    private final int evaluationSteps;
    private final int inputCount;


    public boolean GUI_MODE = true;

    public Trainer(int windowRadius, int player_count, int iterations, int evaluationSteps)
    {
        Neat.initbase();
        this.windowRadius = windowRadius;
        this.player_count = player_count;
        this.iterations = iterations;
        this.evaluationSteps = evaluationSteps;
        int windowWidth = (2*windowRadius+1);

        // Inputs: Window, speed, distance ahead, min distance other player

        inputCount = windowWidth*windowWidth + 3;// + player_count * 5;

        population = new Population(
                Neat.p_pop_size /* population size */,
                inputCount /* network inputs */ ,
                5 /* network outputs */,
                30 /* max index of nodes */,
                true /* recurrent */,
                0.5 /* probability of connecting two nodes */
        );

    }

    public double[] generateInputs(Game game, Player player)
    {
        double[] result = new double[inputCount+1];
        result[inputCount] = -1;

        // Window:
        int i = 0;
        for (int x = -windowRadius; x <= windowRadius; ++x)
        {
            for (int y = -windowRadius; y <= windowRadius; ++y)
            {
                int rx = x;
                int ry = y;
                if (player.getDirection() == Direction.down)
                {
                    rx = -x;
                    ry = -y;
                }
                else if (player.getDirection() == Direction.left)
                {
                    rx = y;
                    ry = -x;
                }
                else if (player.getDirection() == Direction.right)
                {
                    rx = -y;
                    ry = x;
                }

                int wx = rx + player.getX();
                int wy = ry + player.getY();

                if (wx < 0 || wx >= game.getWidth() || wy < 0 || wy >= game.getHeight())
                {
                    result[i] = 1;
                }
                else if (game.getCells()[wy][wx] != 0)
                {
                    result[i] = 1;
                }
                else
                {
                    result[i] = 0;
                }

                ++i;
            }
        }

        // Self first
        //result[i+0] = player.getDirection() == Direction.up ? 1 : 0;
        //result[i+1] = player.getDirection() == Direction.left ? 1 : 0;
        //result[i+2] = player.getDirection() == Direction.down ? 1 : 0;
        //result[i+3] = player.getDirection() == Direction.right ? 1 : 0;
        result[i] = player.getSpeed();

        ++i;

        int counter = 0;
        while (true)
        {
            int[] dxy = Game.direction2Delta(player.getDirection());

            int wx = dxy[0] * counter + player.getX();
            int wy = dxy[1] * counter + player.getY();

            if (wx < 0 || wx >= game.getWidth() || wy < 0 || wy >= game.getHeight())
            {
                break;
            }
            else if (game.getCells()[wy][wx] != 0)
            {
                break;
            }

            ++counter;
        }

        result[i] = counter;
        ++i;
        result[i] = 1000;
        for (int p = 0; p < game.getPlayerCount(); ++p)
        {
            Player other = game.getPlayer(p);
            if (other != player && other.isActive())
            {
                double dx = player.getX() - other.getX();
                double dy = player.getY() - other.getY();
                double distance = Math.sqrt(dx*dx+dy*dy);
                result[i] = Math.min(result[i], distance);
            }
        }

        //i+=5;
        // Then others
        /*
        for (int p = 0; p < game.getPlayerCount(); ++p)
        {
            Player other = game.getPlayer(p);
            if (other != player)
            {
                result[i+0] = other.getDirection() == Direction.up ? 1 : 0;
                result[i+1] = other.getDirection() == Direction.left ? 1 : 0;
                result[i+2] = other.getDirection() == Direction.down ? 1 : 0;
                result[i+3] = other.getDirection() == Direction.right ? 1 : 0;
                result[i+4] = other.getSpeed();
                i+=5;
            }
        }*/

        return result;
    }

    public GameMove generateOutput(Network brain)
    {
        GameMove[] resultSet = new GameMove[]{
                GameMove.change_nothing,
                GameMove.slow_down,
                GameMove.speed_up,
                GameMove.turn_left,
                GameMove.turn_right
        };

        double max = 0.0f;
        int maxIndex = 0;
        for (int i = 0; i < brain.count_motor(); ++i) {
            double output = ((NNode) brain.getOutputs().elementAt(i)).getActivation();
            if(output > max)
            {
                max = output;
                maxIndex = i;
            }
        }

        return resultSet[maxIndex];
    }

    public GameMove getBrainDecision(Network brain, Player player, Game game)
    {
        double[] inputs = generateInputs(game, player);

        // Load these inputs into the neural network.
        brain.load_sensors(inputs);

        int net_depth = brain.max_depth();

        // first activate from sensor to next layer....
        brain.activate();

        // next activate each layer until the last level is reached
        for (int relax = 0; relax <= net_depth; relax++)
        {
            brain.activate();
        }

        // Retrieve outputs from the final layer.
        return generateOutput(brain);
    }

    public void loop()
    {
        /*
            1. Ein Spiel hat 6 Spieler
            2. Beispiel: Population von 60
            3. Starte 10 Spiele und tracke die Überlebenszeit
            4. Wiederhole Schritt 3 10 Mal
            5. Nun habe ich eine Fitness für jeden Organismus
            6. Evolution
        */

        // Main loop iterations
        // i is the current iteration
        double last_avg = 0;
        for (int current_iteration = 0; current_iteration < iterations; ++current_iteration)
        {
            Vector<Organism> neatOrgs = population.getOrganisms();
            int brainCount = neatOrgs.size();
            double[] fitness = new double[brainCount];
            double avg = 0;
            for (int b = 0; b < brainCount; ++b)
            {
                fitness[b] = 100000;
            }

            int currentProg = 0;

            System.out.println();

            // Evaluate all brains
            for (int b = 0; b < brainCount; ++b)
            {
                int sum = 0;
                // Evaluate population multiple times
                // Add up the fitness and divide them by evaluationSteps * gamesPerEvaluation
                // e is the current evaluation
                for (int e = 0; e < evaluationSteps; ++e)
                {
                    double prog = (double)(e+b*evaluationSteps) / (double)(evaluationSteps * brainCount);
                    int iprog = (int)(prog * 20.0);
                    if (iprog != currentProg)
                    {
                        System.out.print("#");
                        currentProg = iprog;
                    }

                    // Create game
                    List<String> names = new ArrayList<>();
                    for (int p = 0; p < player_count; ++p)
                    {
                        names.add("Player " + p);
                    }
                    Game game = Game.create(30, 30, names);

                    // Evaluation until game finishes
                    while(game.isRunning())
                    {
                        List<GameMove> moves = new ArrayList<>();
                        // p is the current player
                        for (int p = 0; p < player_count; ++p)
                        {
                            // Dead players dont move
                            if (game.getPlayer(p).isActive())
                            {
                                Network brain;
                                GameMove move;
                                if (p == 0)
                                {
                                    brain = neatOrgs.get(b).getNet();
                                    sum += game.getDeaths(); // game.getPlayer(p).getSpeed()
                                }
                                else
                                {
                                    int other = (b + p) % neatOrgs.size();
                                    brain = neatOrgs.get(other).getNet();
                                }
                                move = getBrainDecision(brain, game.getPlayer(p), game);
                                moves.add(move);
                            }
                            else
                            {
                                moves.add(GameMove.change_nothing);
                            }
                        }
                        // Apply moves
                        game.tick(moves);
                    }
                    //fitness[b] += sum; //Math.min(fitness[b], sum);
                    //sum = 0;
                }
                fitness[b] = (float) sum / evaluationSteps;
                //fitness[b] /= evaluationSteps;
                avg += fitness[b];
            }

            avg /= brainCount;

            // Evolution
            double best = 0;
            int bestbrain = 0;

            for (int b = 0; b < brainCount; ++b)
            {
                if (fitness[b] > best)
                {
                    bestbrain = b;
                    best = fitness[b];
                }
                fitness[b] -= last_avg;
                neatOrgs.get(b).setFitness(fitness[b]);
            }


            last_avg = avg;

            DecimalFormat df = new DecimalFormat("###.##");
            System.out.println("\nIteration: " + current_iteration + "; current best: " + df.format(best) + "; current avg: " + df.format(avg));

            //if (dif > avg * 2 || i % 10 == 0)
            if(GUI_MODE)
            {
                population.print_to_file_by_species("SavedPopulation.txt");

                Stage stage = new Stage("Iteration: " + current_iteration);

                List<String> names = new ArrayList<>();
                for (int p = 0; p < player_count; ++p)
                {
                    names.add("Player " + p);
                }

                int width = 30;
                if (current_iteration % 10 == 0)
                {
                    width = 50;
                }

                Game game = Game.create(width, width, names);
                stage.setGame(game);

                while (game.isRunning())
                {
                    List<GameMove> moves = new ArrayList<>();
                    // p is the current player
                    for (int p = 0; p < player_count; ++p)
                    {
                        // Dead players dont move
                        if (game.getPlayer(p).isActive())
                        {
                            Network brain;
                            GameMove move;
                            if (p == 0)
                            {
                                brain = neatOrgs.get(bestbrain).getNet();
                            }
                            else
                            {
                                int other = (bestbrain + p) % neatOrgs.size();
                                brain = neatOrgs.get(other).getNet();
                            }
                            move = getBrainDecision(brain, game.getPlayer(p), game);
                            moves.add(move);
                        }
                        else {
                            moves.add(GameMove.change_nothing);
                        }
                    }
                    // Apply moves
                    game.tick(moves);
                    if (stage.isDisplayable())
                    {
                        stage.repaint();
                        //Executors.newSingleThreadScheduledExecutor().schedule(() -> null, 100, TimeUnit.MILLISECONDS);
                    }
                }
            }

            population.epoch(current_iteration);
        }
    }
}
