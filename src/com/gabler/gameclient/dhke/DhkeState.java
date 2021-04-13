package com.gabler.gameclient.dhke;

import lombok.Getter;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.function.Supplier;

/**
 * State of a DHKE transaction.
 *
 * @author Andy Gabler
 */
public class DhkeState {

    private final Supplier<BigInteger> privateKeySupplier;

    private BigInteger modularSpace;
    private BigInteger g;
    private BigInteger privateKey;

    /**
     * The key resulting from the DHKE exchange.
     */
    @Getter
    private byte[] key;

    /**
     * Initialize new state of a DHKE transaction.
     */
    public DhkeState() {
        this(() -> new BigInteger(128, new SecureRandom()));
    }

    /**
     * Initialize new state of a DHKE transaction.
     *
     * @param aPrivateKeySupplier Supplier of the private key
     */
    public DhkeState(Supplier<BigInteger> aPrivateKeySupplier) {
        privateKeySupplier = aPrivateKeySupplier;
    }

    /**
     * Take and accept the next integer in the DHKE transaction.
     *
     * @param nextInteger The next integer to take
     * @return The next integer to go across the wire
     */
    public BigInteger takeNextInteger(BigInteger nextInteger) {
        BigInteger toSendServer = null;
        if (modularSpace == null) {
            modularSpace = nextInteger;
        } else if (g == null) {
            g = nextInteger;
            privateKey = privateKeySupplier.get();
            toSendServer = g.modPow(privateKey, modularSpace);
        } else {
            final BigInteger sharedSecret = nextInteger.modPow(privateKey, modularSpace);
            final byte[] secretBytes = sharedSecret.toByteArray();

            key = new byte[16];
            for (int index = 0; index < key.length; index++) {
                // Ensure if key is longer than 16 bytes, we actually record 16, but 16 if it's unsigned
                key[index] = secretBytes[index + (secretBytes.length - 16)];
            }

            return null;
        }

        return toSendServer;
    }

    /**
     * Is the DHKE complete with a key ready to go?
     *
     * @return If the key is ready to be used
     */
    public boolean isComplete() {
        return key != null;
    }
}
