package com.andronikus.game.model.server;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * State of the game.
 *
 * @author Andronikus
 */
@Data
public class GameState implements Serializable {
    private long version = -1;
    private ArrayList<IMoveable> collideables = new ArrayList<>();
    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Laser> lasers = new ArrayList<>();
    private ArrayList<Asteroid> asteroids = new ArrayList<>();
    private ArrayList<Snake> snakes = new ArrayList<>();
    private ArrayList<MicroBlackHole> blackHoles = new ArrayList<>();
    private ArrayList<Portal> portals = new ArrayList<>();
    private long nextLaserId = 0;
    private long nextSpawnId = 0;
    private IBorder border;
    private boolean serverDebugMode;
    private ArrayList<CommandAcknowledgement> commandAcknowledgements = new ArrayList<>();


    // Control flags
    private boolean tickEnabled;
    private boolean collisionsEnabled;
    private boolean movementEnabled;
    private boolean spawningEnabled;
}
