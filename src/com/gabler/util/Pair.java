package com.gabler.util;

import lombok.Data;

/**
 * Pair of two objects. Mimicking <code>javafx.util.Pair</code>.
 *
 * @param <TYPE_ONE> Type of first object
 * @param <TYPE_TWO> Type of second object
 * @author Andy Gabler
 */
@Data
public class Pair<TYPE_ONE, TYPE_TWO> {
    private TYPE_ONE first;
    private TYPE_TWO second;

    public Pair() {

    }

    public Pair(TYPE_ONE first, TYPE_TWO second) {
        this.first = first;
        this.second = second;
    }
}
