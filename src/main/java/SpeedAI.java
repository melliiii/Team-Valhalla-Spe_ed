import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class SpeedAI {
    // WSS Information TODO: Move to config file
    private final static String url = "wss://msoll.de/spe_ed?key=123abc";
    private final static int expected_ping = 60;

    // Initialize Logger
    private static Logger LOGGER = null;
    static {
        InputStream stream = SpeedAI.class.getClassLoader().
                getResourceAsStream("logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(stream);
            LOGGER= Logger.getLogger(SpeedAI.class.getName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static WebSocket websocket;
    static GameStatus gameStatus;
    static Action nextAction = Action.change_nothing;
    static boolean action_changed = false;


    public static void main(String[] args) {

        try {
            // Create Websocket and connect
            websocket = new WebSocketFactory()
                    .createSocket(url)
                    .addListener(new WebSocketListener())
                    .connect();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            SpeedAI.shutdown(1);
        } catch (WebSocketException e) {
            // WebSocketException, also look into WebSocketListener!!!
            if (e.getMessage().contains("101")){
                LOGGER.log(Level.SEVERE, "Cannot connect to Server!");
                SpeedAI.shutdown(101);
            }
            LOGGER.log(Level.SEVERE, e.getMessage());
            shutdown(1);
        }
    }

    public static void handleMessage(WebSocket websocket, String message) {
        // New Gson Object
        Gson gson = new Gson();

        // Parse json string to GameStatus object
        gameStatus = gson.fromJson(message, GameStatus.class);

        // Print Map TODO Maybe Graphical Map on Endpoint?
        // System.out.println(gameStatus.getMap());

        nextAction = Action.change_nothing;
        action_changed = false;

        // TODO: Decide on which move to do
        // in synchronized thread

        try {
            Thread.sleep(gameStatus.getDeadline().getTime() - System.currentTimeMillis() - expected_ping);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }

        if(!action_changed){
            // TODO notify synchronized thread
            LOGGER.log(Level.WARNING, "No action delivered in time by scoring system!");
        }

        String actionJson = gson.toJson(nextAction);
        websocket.sendText(actionJson);
    }

    // Clean Shutdown
    public static void shutdown(int status){
        if(websocket != null){
            websocket.disconnect();
            websocket = null;
        }
        LOGGER.log(Level.INFO, "Shutting down...");
        System.exit(status);
    }
}
