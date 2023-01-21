package com.andronikus.util;

import lombok.Data;

/**
 * Wrapper class to be used for immutable types in a {@link com.gabler.udpmanager.ResourceLock}.
 *
 * @param <DATUM_TYPE> The type of the data in the cell
 * @author Andronikus
 */
@Data
public class ResourceCell<DATUM_TYPE> {
    private DATUM_TYPE datum;

    public ResourceCell() {
        this(null);
    }

    public ResourceCell(DATUM_TYPE aDatum) {
        datum = aDatum;
    }
}
