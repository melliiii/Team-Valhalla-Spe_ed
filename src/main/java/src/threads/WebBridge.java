package src.threads;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import src.*;
import src.algorithmic.AlgorithmicAI;
import src.algorithmic.Odin;
import src.algorithmic.VariantTracker;
import src.algorithmic.thor.Thor;
import src.game.Game;
import src.game.GameMove;
import src.game.GameState;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class WebBridge implements SafeThread
{
    // WSS Information TODO: Move to config file
    private final static String url = "wss://yellowphoenix18.de:554/valhalla";
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
    private AlgorithmicAI ai;
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
            //Coil a = new Coil(g, gameState.you-1);
            //a.setIterations(300);
            Odin odin = new Thor(g, gameState.you-1);
            odin.setIterations(300);
            ai = odin;
        }

        LOGGER.log(Level.INFO, "Game: " + message);

        stage.setGame(g);
        stage.repaint();

        // Reset Action
        nextGameMove = GameMove.change_nothing;
        //gameMove_changed = false;

        VariantTracker visualizeVariants = new VariantTracker(g, gameState.you-1);
        ((Odin)ai).setTracker(visualizeVariants);

        ai.setGame(g);
        nextGameMove = ai.decide();
        stage.setVisualizeVariants(visualizeVariants);

        LOGGER.log(Level.INFO, "Decision: " + nextGameMove.toString());

        // in synchronized thread

        // Wait for Action
        /*
        try {
            long timeMillisLeft =  gameState.getDeadline().getTime() - System.currentTimeMillis();
            while(timeMillisLeft >= expected_ping){
                Thread.sleep(timeMillisLeft / 4);
                timeMillisLeft =  gameState.getDeadline().getTime() - System.currentTimeMillis();
                if(gameMove_changed) break; // break if action got selected
            }
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }

        // if action wasnt changed make a log entry
        if(!gameMove_changed){
            // TODO notify synchronized thread
            LOGGER.log(Level.WARNING, "No game move delivered in time by scoring system!");
        }
        */

        // Parse actions as JSON and send it to server
        String actionJson = "{\"action\": \"" + nextGameMove.toString() + "\"}";
        LOGGER.log(Level.INFO, "Sending message: " + actionJson);
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

    @Override
    public boolean isRunning()
    {
        return websocket != null;
    }

    @Override
    public void terminate()
    {
        shutdown(1);
    }

    @Override
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
}
