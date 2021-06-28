package com.andronikus.gameclient;

import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;
import java.util.Scanner;

/**
 * Setup configuration for a socket.
 *
 * @author Andronikus
 */
public class ClientCertificateUtil {

    /**
     * Add SSL system properties.
     */
    @SneakyThrows
    public static void addSslToSystemProperties() {
        final URL trustStoreUrl = ClientCertificateUtil.class.getClassLoader().getResource("gamesslstore-server.store");
        final String sslTrustPath = trustStoreUrl.getFile();

        System.setProperty("javax.net.ssl.trustStore", sslTrustPath);

        final URL keyStoreUrl = ClientCertificateUtil.class.getClassLoader().getResource("gamesslstore-client.store");
        final String sslStorePath = keyStoreUrl.getFile();

        final URL sslClientPasswordResource = ClientCertificateUtil.class.getClassLoader().getResource("ssl-client-password.txt");
        final String sslClientPassword = new Scanner(new File(sslClientPasswordResource.getFile())).nextLine();

        System.setProperty("javax.net.ssl.keyStore", sslStorePath);
        System.setProperty("javax.net.ssl.keyStorePassword", sslClientPassword);

        /*
         * Without this, handshakes fail. This is because a DSA certificate is not supported in TLS 1.3. Certificate
         * supported in TLS 1.2.
         *
         * https://stackoverflow.com/questions/55854904/javax-net-ssl-sslhandshakeexception-no-available-authentication-scheme
         * https://bugs.openjdk.java.net/browse/JDK-8211426?focusedCommentId=14218233&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#comment-14218233
         *
         * TODO Upgrade Certificate instead of downgrading TLS
         */
        System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
    }
}
