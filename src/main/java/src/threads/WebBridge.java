package src.threads;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import src.WebSocketListener;
import src.algorithmic.VariantTracker;
import src.algorithmic.thor.Thor;
import src.game.Game;
import src.game.GameMove;
import src.game.GameState;

import javax.sound.sampled.Line;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class WebBridge
{
    // WSS Information TODO: Move to config file
    private String url = "wss://yellowphoenix18.de:554/valhalla";
    //private final static String url = "wss://msoll.de/spe_ed?key=A534MVSB3KOJLTSSJ6JRVYEA5JMOQ366VIV6E7EJWJ7DE3SMMASOKIQM";
    private final static int expected_ping = 60;

    // Initialize Logger
    private static Logger LOGGER = null;
    static {
        InputStream stream = WebBridge.class.getClassLoader().
                getResourceAsStream("logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(stream);
            LOGGER= Logger.getLogger(WebBridge.class.getName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private WebSocket websocket;
    private GameState gameState;
    private GameMove nextGameMove = GameMove.change_nothing;
    private boolean gameMove_changed = false;
    private Stage stage = new Stage(false);
    private Thor ai;
    private int ticks = 0;

    public void handleMessage(WebSocket websocket, String message) {
        // New Gson Object
        Gson gson = new Gson();

        // Parse json string to GameStatus object
        try {
            gameState = gson.fromJson(message, GameState.class);
        } catch (JsonSyntaxException e){
            // message is not a GameStatus Object
            LOGGER.log(Level.INFO, "Server >> " + message);
            return;
        }

        Game g = new Game(gameState, ticks);
        ++ticks;
        if (ai == null)
        {
            LOGGER.log(Level.INFO, "Game started");
            Thor thor = new Thor(g, gameState.you-1);
            ai = thor;
        }

        stage.setGame(g);
        stage.repaint();

        // Reset Action
        nextGameMove = GameMove.change_nothing;
        //gameMove_changed = false;

        VariantTracker visualizeVariants = new VariantTracker(g, gameState.you-1);
        ai.setTracker(visualizeVariants);

        ai.setGame(g);
        ai.beginTurn();

        // Replace this with deadline check
        LOGGER.log(Level.INFO, gameState.getDeadline().toString());

        for (int i = 0; i < 300; ++i)
        {
            ai.treeSearchIteration();
        }
        nextGameMove = ai.endTurn();
        stage.setVisualizeVariants(visualizeVariants);

        LOGGER.log(Level.INFO, "Decision: " + nextGameMove.toString());

        // Parse actions as JSON and send it to server
        String actionJson = "{\"action\": \"" + nextGameMove.toString() + "\"}";
        websocket.sendText(actionJson);
    }

    // Clean Shutdown
    public void shutdown(int status){
        if(websocket != null){
            // If Websocket is still connected, disconnect and destroy object
            websocket.disconnect();
            websocket = null;
        }
        LOGGER.log(Level.INFO, "Shutting down...");
        System.exit(status);
    }

    public boolean isRunning()
    {
        return websocket != null;
    }

    public void terminate()
    {
        shutdown(1);
    }

    public void start()
    {
        try
        {
            // Create Websocket and connect
            websocket = new WebSocketFactory()
                    .createSocket(url)
                    .addListener(new WebSocketListener(this))
                    .connect();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            shutdown(1);
        } catch (WebSocketException e) {
            // WebSocketException, also look into src.WebSocketListener!!!
            if (e.getMessage().contains("101")){
                LOGGER.log(Level.SEVERE, "Cannot connect to Server!");
                shutdown(101);
            }
            LOGGER.log(Level.SEVERE, e.getMessage());
            shutdown(1);
        }
    }

    public void setUrl(String url){
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }
}
