import Service.Service;
import com.sun.net.httpserver.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.KeyStore;

public class Server {



    public static void main(String args[]) throws Exception {
        //create the socket server object
        try {
            InetSocketAddress address = new InetSocketAddress(8000);

            // initialise the HTTPS server
            HttpsServer httpsServer = HttpsServer.create(address, 0);
            SSLContext sslContext = SSLContext.getInstance("TLS");

            // initialise the keystore
            char[] password = "supermarine".toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("myKeyStore.jks");
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

            //Map the http handlers to url
            httpsServer.createContext("/message", new receive());
            httpsServer.start();
        }catch (Exception e){
            System.out.println("======================= EXCEPTION OCCURRED ===========================");
            e.printStackTrace();
        }

    }

    public static class receive implements HttpHandler {
        //context for handling new requests
        @Override
        public void handle(HttpExchange he){
            try {
                // parse request
                URI requestedUri = he.getRequestURI();
                String command = requestedUri.getRawQuery().replace("+"," ").split("=")[1];
                System.out.println("New command received : " + command);
                String Origin = he.getRemoteAddress().getAddress().toString().split("/")[0];
                System.out.println("From :" + Origin);

                //create a new service thread to handle the request
                Service service = new Service(command,Origin);
                Thread serviceThread = new Thread(service);
                serviceThread.start();

                //acknowledge the request
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
