package com.gabler.gameclient.app;

import com.gabler.client.ClientStartException;
import com.gabler.game.model.server.GameState;
import com.gabler.gameclient.ClientCertificateUtil;
import com.gabler.gameclient.client.GameClient;
import com.gabler.gameclient.client.GameClientStartException;
import com.gabler.gameclient.engine.IClientInputSupplier;
import com.gabler.gameclient.engine.IGameStateRenderer;
import com.gabler.gameclient.engine.IRendererPresetup;
import com.gabler.gameclient.ui.GameWindow;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
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

        IGameStateRenderer renderer;
        IClientInputSupplier inputSupplier;
        IRendererPresetup presetupOperations;
        if (renderMethod.equalsIgnoreCase("UI")) {
            final GameWindow window = new GameWindow();
            renderer = window;
            inputSupplier = window;
            presetupOperations = window;
        } else if (renderMethod.equalsIgnoreCase("TXT")) {
            renderer = new IGameStateRenderer() {
                @Override
                public void setGameStateToRender(GameState toRender) {
                    System.out.println(new Gson().toJson(toRender));
                }

                @Override
                public void render() {}
            };
            inputSupplier = ArrayList::new;
            presetupOperations = () -> System.out.println("Engine renderer started...");
        } else {
            throw new IllegalArgumentException("No renderer for render option " + renderMethod);
        }

        ClientCertificateUtil.addSslToSystemProperties();
        final GameClient client = new GameClient(hostname, renderer, inputSupplier, presetupOperations);
        client.start(username, password);
    }
}
