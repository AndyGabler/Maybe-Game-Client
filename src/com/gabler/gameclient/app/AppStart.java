package com.gabler.gameclient.app;

import com.gabler.client.ClientStartException;
import com.gabler.gameclient.ClientCertificateUtil;
import com.gabler.gameclient.client.GameClient;
import com.gabler.gameclient.client.GameClientStartException;
import com.gabler.gameclient.engine.IGameStateRenderer;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

/**
 * Application entry point class.
 *
 * @author Andy Gabler
 */
public class AppStart {

    /**
     * Bootstrap the client with all essential components and run.
     *
     * @param args The arguments
     * @throws ClientStartException If key client or authentication client cannot start
     * @throws GameClientStartException If the actual game client cannot start
     * @throws IOException If sending an initial UDP to the server fails
     */
    public static void main(String[] args) throws ClientStartException, GameClientStartException, IOException {
        // TODO here I just bootstrap a client, perhaps consider having this init a UI that is responsible for this logic
        final ApplicationOptions options = new ApplicationOptions(args);
        final String hostname = options.getOption("host", true, 1).get(0);
        final String username = options.getOption("username", true, 1).get(0);
        final String password = options.getOption("password", true, 1).get(0);
        final List<String> renderMethods = options.getOption("render", true, 1);
        options.checkUnusedOptions();

        String renderMethod = "UI";
        if (renderMethods != null) {
            renderMethod = renderMethods.get(0);
        }

        IGameStateRenderer renderer = null;
        if (renderMethod.equalsIgnoreCase("UI")) {
            // TODO
        } else if (renderMethod.equalsIgnoreCase("TXT")) {
            renderer = (state) -> System.out.println(new Gson().toJson(state));
        } else {
            throw new IllegalArgumentException("No renderer for render option " + renderMethod);
        }

        ClientCertificateUtil.addSslToSystemProperties();
        final GameClient client = new GameClient(hostname, renderer);
        client.start(username, password);
    }
}
