package src;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;

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


    @Override
    public void onTextMessage(WebSocket websocket, String message) {
        // fired when a text is received
        // lets just pass that on to the main class
        WebBridge.handleMessage(websocket, message);
    }

    @Override
    public void onError(WebSocket websocket, WebSocketException exception) throws WebSocketException {
        // Error handling on websocket
        if (exception.getMessage().contains("427")) {
            LOGGER.log(Level.SEVERE, "Already connected");
            WebBridge.shutdown(427);
        } else {
            throw exception;
        }
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
        // TODO Handle Connect??? MAYBE??? Works either way so prob. not :P
    }
}
