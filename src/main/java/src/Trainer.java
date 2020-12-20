package src;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import src.jneat.*;


@SuppressWarnings("SuspiciousNameCombination")
public class Trainer extends Thread implements SafeThread
{
    private final Population population;
    private final int windowRadius;
    private final int player_count;
    private final int iterations;
    private final int evaluationSteps;
    private final int inputCount;

    private boolean running;

    public boolean GUI_MODE = true;

    public Trainer(int windowRadius, int player_count, int iterations, int evaluationSteps)
    {
        running = true;
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
                40 /* max index of nodes */,
                true /* recurrent */,
                0.24 /* probability of connecting two nodes */
        );

        //population = new Population(
        //        50 /* population size */,
        //        inputCount /* network inputs */ ,
        //        5 /* network outputs */,
        //        20 /* max index of nodes */,
        //        true /* recurrent */,
        //        1 /* probability of connecting two nodes */
        //);
    }

    public double[] generateInputs(Game game, Player player)
    {
        double[] result = new double[inputCount+1];
        result[inputCount] = -1;

        // Window:
        int i = 0;
        for (int x = -windowRadius; x <= windowRadius && running; ++x)
        {
            for (int y = -windowRadius; y <= windowRadius && running; ++y)
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
        //result[i+0] = player.getDirection() == src.Direction.up ? 1 : 0;
        //result[i+1] = player.getDirection() == src.Direction.left ? 1 : 0;
        //result[i+2] = player.getDirection() == src.Direction.down ? 1 : 0;
        //result[i+3] = player.getDirection() == src.Direction.right ? 1 : 0;
        result[i] = player.getSpeed();

        ++i;

        int counter = 0;
        while (running)
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
        for (int p = 0; p < game.getPlayerCount() && running; ++p)
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
        for (int i = 0; i < brain.count_motor() && running; ++i) {
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
        for (int relax = 0; relax <= net_depth && running; relax++)
        {
            brain.activate();
        }

        // Retrieve outputs from the final layer.
        return generateOutput(brain);
    }

    @Override
    public void run()
    {
        // src.Main loop iterations
        // i is the current iteration
        double last_avg = 0;
        double last_best = 0;
        Stage stage = new Stage(true);
        for (int i = 0; i < iterations; ++i)
        {
            Vector<Organism> neatOrgs = population.getOrganisms();
            int brainCount = neatOrgs.size();
            double[] fitness = new double[brainCount];
            double avg = 0;
            double algo_avg = 0;
            for (int b = 0; b < brainCount; ++b)
            {
                fitness[b] = 100000;
            }

            int currentProg = 0;

            System.out.println();

            // Evaluate all brains
            for (int b = 0; b < brainCount && running; ++b)
            {
                int sum = 0;
                // Evaluate population multiple times
                // Add up the fitness and divide them by evaluationSteps * gamesPerEvaluation
                // e is the current evaluation
                for (int e = 0; e < evaluationSteps && running; ++e)
                {
                    double prog = (double)(e+b*evaluationSteps) / (double)(evaluationSteps * brainCount);
                    int iprog = (int)(prog * 20.0);
                    if (iprog != currentProg)
                    {
                        System.out.print("#");
                        currentProg = iprog;
                    }

                    // Create game
                    List<Integer> brains = new ArrayList<>();
                    List<String> names = new ArrayList<>();
                    for (int p = 0; p < player_count; ++p)
                    {
                        names.add("Player " + String.valueOf(p));
                        if (p > 0)
                        {
                            brains.add(random.nextInt(brainCount));
                        }
                    }
                    Game game = Game.create(50, 50, names);
                    Siegfried sigi = new Siegfried(game, 5);

                    // Evaluation until game finishes
                    while(game.isRunning() && running)
                    {
                        List<GameMove> moves = new ArrayList<>();
                        // p is the current player
                        for (int p = 0; p < player_count && running; ++p)
                        {
                            // Dead players dont move
                            if (game.getPlayer(p).isActive())
                            {
                                Network brain;
                                GameMove move;
                                if (p == 0)
                                {
                                    brain = ((Organism)neatOrgs.get(b)).getNet();
                                    sum += Math.sqrt(game.getDeaths()); // game.getPlayer(p).getSpeed()
                                    move = getBrainDecision(brain, game.getPlayer(p), game);
                                    if (move == GameMove.turn_left || move == GameMove.turn_right)
                                    {
                                        sum+=Math.sqrt(game.getDeaths());
                                    }
                                }
                                else if (p == 5)
                                {
                                    move = sigi.decide(0);
                                    algo_avg+=Math.sqrt(game.getDeaths());
                                }
                                else
                                {
                                    int other = brains.get(p-1);
                                    brain = ((Organism)neatOrgs.get(other)).getNet();
                                    move = getBrainDecision(brain, game.getPlayer(p), game);
                                }
                                moves.add(move);
                            }
                            else
                            {
                                moves.add(GameMove.change_nothing);
                            }
                        }
                        if(!running) return;
                        // Apply moves
                        game.tick(moves);
                    }
                    if(!running) return;
                    //fitness[b] += sum; //Math.min(fitness[b], sum);
                    //sum = 0;
                }
                fitness[b] = (float) sum / evaluationSteps;
                //fitness[b] /= evaluationSteps;
                avg += fitness[b];
            }

            avg /= brainCount;
            algo_avg /= (float)(brainCount * evaluationSteps);

            // Evolution
            double best = 0;
            int bestbrain = 0;

            for (int b = 0; b < brainCount && running; ++b)
            {
                if (fitness[b] > best)
                {
                    bestbrain = b;
                    best = fitness[b];
                }
                fitness[b] -= last_avg;
                neatOrgs.get(b).setFitness(fitness[b]);
            }
            if(!running) return;


            last_avg = avg;

            DecimalFormat df = new DecimalFormat("###.##");
            System.out.println("\nIteration: " + i + "; current best: " + best + "; current avg: " + avg + "; sigis avg: " + algo_avg);

            if(GUI_MODE && current_iteration % 10 == 0)
            {
                population.print_to_file_by_species("SavedPopulation.txt");

                // Create game
                List<Integer> brains = new ArrayList<Integer>();
                List<String> names = new ArrayList<String>();
                for (int p = 0; p < player_count; ++p)
                {
                    names.add("Player " + String.valueOf(p));
                    if (p > 0)
                    {
                        brains.add(random.nextInt(brainCount));
                    }
                }

                int width = 50;
                if(!running) return;

                Game game = Game.create(width, width, names);
                Siegfried sigi = new Siegfried(game, 5);
                stage.setGame(game);

                while (game.isRunning() && running) {
                    List<GameMove> moves = new ArrayList<>();
                    // p is the current player
                    for (int p = 0; p < player_count && running; ++p) {
                        // Dead players dont move
                        if (game.getPlayer(p).isActive()) {
                            Network brain;
                            GameMove move;
                            if (p == 0)
                            {
                                brain = ((Organism)neatOrgs.get(bestbrain)).getNet();
                                move = getBrainDecision(brain, game.getPlayer(p), game);
                            }
                            else if (p == 5)
                            {
                                move = sigi.decide(0);
                            }
                            else
                            {
                                int other = brains.get(p-1);
                                brain = ((Organism)neatOrgs.get(other)).getNet();
                                move = getBrainDecision(brain, game.getPlayer(p), game);
                            }
                            moves.add(move);
                        }
                        else {
                            moves.add(GameMove.change_nothing);
                        }
                    }
                    // Apply moves
                    if(running)
                        game.tick(moves);
                    else return;

                    if (stage.isDisplayable() && running)
                    {
                        stage.repaint();
                        //Executors.newSingleThreadScheduledExecutor().schedule(() -> null, 100, TimeUnit.MILLISECONDS);
                    }
                    else
                    {
                        stage = new Stage(true);
                    }
                }
            }

            if(running) population.epoch(current_iteration);
            else return;
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void terminate() {
        running = false;
    }
}
