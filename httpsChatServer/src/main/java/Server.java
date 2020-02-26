import Clients.Client;
import Prototype.ServerPrototype;
import Service.Service;
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

public class Server {


    //static ServerSocket variable
    private static ServerSocket server;
    //socket server port on which it will listen
    private static int port = 9876;
    private static ArrayList<Client> clients;

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

            httpsServer.createContext("/message", new message());
            httpsServer.start();
        }catch (Exception e){
            System.out.println("======================= EXCEPTION OCCURRED ===========================");
            e.printStackTrace();
        }




        /*server = new ServerSocket(port);
        while (true) {

            System.out.println("Waiting for the client requests");

            Socket socket = server.accept();

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            String message = (String) ois.readObject();
            System.out.println("Class Server Received Message: "+ message);
            System.out.println("Class Server Received Socket: "+ socket.getInetAddress().toString());

            //Each Service Thread Services a Request
            Service s = new Service(message, socket.getInetAddress().toString().substring(1));
            Thread t = new Thread(s);
            t.start();

        }*/
    }

    public static class message implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {
            try {
                // parse request
                URI requestedUri = he.getRequestURI();
                String command = requestedUri.getRawQuery().replace("+"," ").split("=")[1];
                System.out.println(command);
                String Origin = he.getRemoteAddress().getAddress().toString().split("/")[0];
                System.out.println(Origin);

                Service s = new Service(command,Origin);
                Thread t = new Thread(s);
                t.start();

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
