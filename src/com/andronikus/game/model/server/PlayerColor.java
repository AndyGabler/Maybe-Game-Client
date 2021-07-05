package com.andronikus.game.model.server;

import java.io.Serializable;
import java.util.Arrays;

/**
 * The color the engine will render the player as in game.
 *
 * @author Andronikus
 */
public enum PlayerColor implements Serializable {
    RED(0),
    BLUE(1),
    YELLOW(2),
    GREEN(3),
    ORANGE(4),
    VIOLET(5),
    WHITE(6),
    LIME(7);

    private int id;

    PlayerColor(int id) {
        this.id = id;
    }

    /**
     * Get the player color by its ID.
     *
     * @param id The id
     * @return The player color
     */
    public static PlayerColor getById(int id) {
        return Arrays.stream(PlayerColor.values()).filter(color -> color.id == id).findFirst().get();
    }

    /**
     * Get the ID of the color
     *
     * @return The ID
     */
    public int getId() {
        return id;
    }
}
