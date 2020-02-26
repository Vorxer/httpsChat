package Clients;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;

public class Client {

    private String Name;
    private String IP;

    public String getName() {
        return Name;
    }

    public String getIP() {
        return IP;
    }

    private int Port;
    private String httpsURL = "";

    private Socket socket = null;
    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;

    public Client(String name, String IP, int port) throws Exception{
        this.Name = name;
        this.IP = IP;
        this.Port = port;

        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        System.setProperty("javax.net.ssl.trustStore", "/home/vanguard/Java/httpsChatClient/src/myKeyStore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "supermarine");

        this.httpsURL = "https://" + IP + ":" +Port+"/send";
        //this.url = new URL(httpsURL);

        System.out.println("New Client Created " + name + " " + IP+ ":" + port);
    }

    public void sendMessage(String message, String sender) throws Exception{
        if(!message.equals("")) {
            message = message.replace(" ", "+");
            System.out.println(httpsURL + "?send=" + message +"+from+" + sender);
            URL requestURL = new URL(httpsURL + "?send=" + message+ "+from+" + sender);
            HttpsURLConnection conn = (HttpsURLConnection) requestURL.openConnection();
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                System.out.println(inputLine);
            }
            br.close();
        }
    }
}
