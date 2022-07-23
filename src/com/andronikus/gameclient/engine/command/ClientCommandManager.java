package com.andronikus.gameclient.engine.command;

import com.andronikus.game.model.client.ClientCommand;
import com.andronikus.game.model.server.CommandAcknowledgement;
import com.andronikus.game.model.server.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility to manage commands for the server.
 *
 * @author Andronikus
 */
public class ClientCommandManager {

    private final String clientId;
    private int commandSequenceNumber = 0;

    // Commands that the server has not acknowledged
    private ArrayList<ClientCommand> unackedCommands = new ArrayList<>();

    // Commands server has processed that need cleaning up
    private ArrayList<ClientCommand> ackedCommands = new ArrayList<>();

    /**
     * Instantiate a utility to manage commands for the server.
     *
     * @param aClientId The ID of the client
     */
    public ClientCommandManager(String aClientId) {
        clientId = aClientId;
    }

    /**
     * Add a command to the manager.
     *
     * @param commandCode The command
     */
    public void addCommand(String commandCode) {
        final ClientCommand command = new ClientCommand();
        command.setCommandNumber(commandSequenceNumber);
        commandSequenceNumber++;
        command.setCode(commandCode);
        unackedCommands.add(command);
    }

    /**
     * Process the current game state, mainly scanning for acks.
     *
     * @param gameState The game state
     */
    public void processGameState(GameState gameState) {
        if (gameState == null || gameState.isServerDebugMode()) {
            return;
        }

        final List<CommandAcknowledgement> commandAcknowledgements = gameState.getCommandAcknowledgements()
            .stream()
            .filter(commandAcknowledgement -> commandAcknowledgement.getSessionId().equalsIgnoreCase(clientId))
            .collect(Collectors.toList());

        unackedCommands.removeIf((unackedCommand) -> {
            if (commandAcknowledgements.stream().anyMatch(commandAcknowledgement -> commandAcknowledgement.getCommandId() == unackedCommand.getCommandNumber())) {
                ackedCommands.add(unackedCommand);
                return true;
            }
            return false;
        });

        // If server is no longer acking this command, remove the deletion from the server command tray
        ackedCommands
            .removeIf(ackedCommand ->
                commandAcknowledgements
                    .stream()
                    .noneMatch(commandAcknowledgement -> commandAcknowledgement.getCommandId() == ackedCommand.getCommandNumber())
            );
    }

    /**
     * Get commands that are unacknowledged.
     *
     * @return Unacknowledged commands
     */
    public List<ClientCommand> getUnackedCommands() {
        return unackedCommands;
    }

    /**
     * Get commands that are known to be acknowledged by the server
     *
     * @return Acknowledged commands
     */
    public List<ClientCommand> getAckedCommands() {
        return ackedCommands;
    }
}
