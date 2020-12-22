package src;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import src.threads.WebBridge;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class WebSocketListener extends WebSocketAdapter {

    // Initialize Logger
    private static Logger LOGGER = null;
    private WebBridge bridge;

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

    public WebSocketListener(WebBridge bridge)
    {
        super();
        this.bridge = bridge;
    }

    @Override
    public void onTextMessage(WebSocket websocket, String message) {
        // fired when a text is received
        // lets just pass that on to the main class
        bridge.handleMessage(websocket, message);
    }

    @Override
    public void onError(WebSocket websocket, WebSocketException exception) throws WebSocketException {
        LOGGER.log(Level.SEVERE, "ERROR");
        exception.printStackTrace();


        bridge.shutdown(427);

        // Error handling on websocket
        if (exception.getMessage().contains("427")) {
            LOGGER.log(Level.SEVERE, "Already connected");
        } else {
            throw exception;
        }
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
        LOGGER.log(Level.INFO, "Connected!");
    }
}
