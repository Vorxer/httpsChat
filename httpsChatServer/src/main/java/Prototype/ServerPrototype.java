package Prototype;

import com.sun.net.httpserver.*;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

public class ServerPrototype {
    public static void main(String[] args) {
        try {
            InetSocketAddress address = new InetSocketAddress(8000);

            // initialise the HTTPS server
            HttpsServer httpsServer = HttpsServer.create(address, 0);
            SSLContext sslContext = SSLContext.getInstance("TLS");

            // initialise the keystore
            char[] password = "supermarine".toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("/media/dinura/Data Disk/Java-LP/CS-Chat/httpsChatServer/myKeyStore.jks");
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

            httpsServer.createContext("/test", new getTest());
            //httpsServer.createContext("/initiate", new SimpleHTTPSServer.getMyMessage());
            //httpsServer.createContext("/list", new SimpleHTTPSServer.listUsers());
            //httpsServer.createContext("/message", new SimpleHTTPSServer.registerUser());
            httpsServer.start();
        }catch (Exception e){
            System.out.println("======================= EXCEPTION OCCURRED ===========================");
            e.printStackTrace();
        }
    }

    public static class getTest implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {
            try {
                // parse request
                //Map<String, Object> parameters = new HashMap<String, Object>();
                URI requestedUri = he.getRequestURI();
                String query = requestedUri.getRawQuery();

                //String clientName = query.substring(query.indexOf("=") + 1, query.length());
                //System.out.println(clientName);

                // userList.keySet().toString().replaceAll(clientName, clientName + "(You)")
                //String response = "no";
                // send response
                he.sendResponseHeaders(200, "TEST METHOD CALLED".length());
                OutputStream os = he.getResponseBody();
                os.write("TEST METHOD CALLED".toString().getBytes());

                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
