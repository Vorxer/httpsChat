package Connection;

import javax.net.SocketFactory;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.sun.net.httpserver.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class Listner implements Runnable {


    private int Port;

    private static ServerSocket server;

    public Listner(int port) {
        Port = port;
    }

    public void run() {
        try {
            System.out.println("LOCAL SERVER STARTED");
            InetSocketAddress address = new InetSocketAddress(Port);

            // initialise the HTTPS server
            HttpsServer httpsServer = HttpsServer.create(address, 0);
            SSLContext sslContext = SSLContext.getInstance("TLS");

            // initialise the keystore
            char[] password = "supermarine".toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("/home/vanguard/Java/httpsChatClient/src/myKeyStore.jks");
            ks.load(fis, password);

            // setup the key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);

            // setup the trust manager factory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            // setup the HTTPS context and parameters
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                public void configure(HttpsParameters params) {
                    System.out.println("GOT HERE");
                    // initialise the SSL context
                    SSLContext context = getSSLContext();
                    SSLEngine engine = context.createSSLEngine();
                    params.setNeedClientAuth(false);
                    params.setCipherSuites(engine.getEnabledCipherSuites());
                    params.setProtocols(engine.getEnabledProtocols());

                    // Set the SSL parameters
                    SSLParameters sslParameters = context.getSupportedSSLParameters();
                    params.setSSLParameters(sslParameters);
                }
            });
            //Map th httphandlers to url

            httpsServer.createContext("/send", new send());
            httpsServer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class send implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {

            try {
                // parse request
                URI requestedUri = he.getRequestURI();
                String command = requestedUri.getRawQuery().replace("+", " ").split("=")[1];
                System.out.println(command);
                he.sendResponseHeaders(200, "Command Received".length());
                OutputStream os = he.getResponseBody();
                os.write("Command Received".toString().getBytes());
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
