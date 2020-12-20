package src;

public class Siegfried extends AlgorithmicAI
{
    public Siegfried(Game game, int playerId)
    {
        super(game, playerId);
    }

    private GameMove coil(Player player, int[] dxy)
    {
        GameMove result = GameMove.change_nothing;
        return result;
    }

    @Override
    public GameMove decide(int depth)
    {
        Player player = game.getPlayer(playerId);
        if (!shared)
        {
            finder.findAreas();
        }
        GameMove result = GameMove.change_nothing;

        // Collect some data to decide upon
        int[] dxy = Game.direction2Delta(player.getDirection());
        double nextPlayer = nextPlayerDistance(player);

        int max = -1;

        int playerDir = Game.direction2Int(player.getDirection());
        int offset = playerDir;

        if (random.nextFloat() > 0.5)
        {
            offset = random.nextInt(100);
        }

        for (int i = 0; i < 4; ++i)
        {
            int head = (i+offset) % 4;
            int[] idxy = Game.direction2Delta(Game.int2Direction(head));
            if (game.positionExists(player.getX() + idxy[0], player.getY() + idxy[1]))
            {
                int ahead = lookAhead(player.getX(), player.getY(), Game.int2Direction(head));
                int speed = player.getSpeed();
                int space = finder.getAreaAt(player.getX() + idxy[0], player.getY() + idxy[1]);

                if (space > max)
                {
                    max = space;
                    result = GameMove.change_nothing;
                    if (speed * speed >= ahead)
                    {
                        continue;
                    }
                    if(playerDir == (head+3) % 4)
                    {
                        result = GameMove.turn_left;
                    }
                    if(playerDir == (head+1) % 4)
                    {
                        result = GameMove.turn_right;
                    }
                }
            }
        }

        double ahead = lookAhead(player.getX(), player.getY(), player.getDirection());
        double speed = Math.sqrt(ahead / 2.0);
        int maxspeed = (int)speed;
        if (player.getSpeed() < maxspeed && player.getSpeed() < 10)
        {
            if (random.nextDouble() > nextPlayer * nextPlayer / 100.0)
            {
                result = GameMove.speed_up;
            }
        }
        if (player.getSpeed() > maxspeed && player.getSpeed() > 1)
        {
            result = GameMove.slow_down;
        }

        return result;
    }
}
