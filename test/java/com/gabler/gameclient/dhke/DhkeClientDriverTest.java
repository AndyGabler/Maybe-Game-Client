package com.gabler.gameclient.dhke;

import com.gabler.client.ClientStartException;

import java.math.BigInteger;

/**
 * Bootstrap a DHKE client.
 *
 * @author Andy Gabler
 */
public class DhkeClientDriverTest {

    public static void main(String[] args) throws ClientStartException {
        DhkeClientCertificateUtil.addSslToSystemProperties();
        final DhkeClient client = new DhkeClient(DhkeClientDriverTest::printKeyBytesAndId);
        client.start("localhost");
        client.requestNewKey();
    }

    private static void printKeyBytesAndId(byte[] bytes, String keyId) {
        System.out.println("DHKE Byte array: ");
        for (byte b : bytes) {
            System.out.print(String.format("0x%02X ", b));
        }

        System.out.println("\nDHKE Integer Form " + new BigInteger(bytes).toString());
        System.out.println("Key ID: " + keyId);
    }
}
