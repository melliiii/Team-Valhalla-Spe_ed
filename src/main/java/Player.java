public class Player
{
    private PlayerState state;

    public PlayerState getState() {
        return state;
    }

    public int getID() {
        return state.ID;
    }

    public void setID(int ID) {
        state.ID = ID;
    }

    public int getX() {
        return state.x;
    }

    public void setX(int x) {
        state.x = x;
    }

    public int getY() {
        return state.y;
    }

    public void setY(int y) {
        state.y = y;
    }

    public Direction getDirection() {
        return state.direction;
    }

    public void setDirection(Direction direction) {
        state.direction = direction;
    }

    public int getSpeed() {
        return state.speed;
    }

    public void setSpeed(int speed) {
        state.speed = speed;
    }

    public boolean isActive() {
        return state.active;
    }

    public void setActive(boolean active) {
        state.active = active;
    }

    public String getName() {
        return state.name;
    }

    public void setName(String name) {
        state.name = name;
    }

    public Player clone()
    {
        PlayerState s = new PlayerState();
        Player result = new Player(s);
        result.setActive(isActive());
        result.setDirection(getDirection());
        result.setID(getID());
        result.setName(getName());
        result.setSpeed(getSpeed());
        result.setX(getX());
        result.setY(getY());
        return result;
    }

    public Player(PlayerState state)
    {
        this.state = state;
    }
}
