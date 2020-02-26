package Connection;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;

public class Connection{

    private String IP;
    private String ID;
    private String httpsURL = "";
    private URL url = null;

    Integer ServerPort = 8000;
    public void connect(String IP, String ID, int port) throws Exception{

        this.IP = IP;

        try {
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

            this.httpsURL = "https://" + IP + ":" +ServerPort+"/message";
            //this.url = new URL(httpsURL);

        } catch (Exception e){

            e.printStackTrace();

        }

        this.ID = ID;
        this.IP = IP;

        receive(port);
        Thread.sleep(3000);
        message("connect " + ID + " " + port);
    }

    public void receive(int port) throws Exception{

        Listner listner = new Listner(port);
        Thread thread = new Thread(listner);
        thread.start();

    }


    public void message(String command) throws Exception{
        System.out.println("Message Received");
        if(!command.equals("")) {
            command = command.replace(" ", "+");
            System.out.println(httpsURL + "?message=" + command);
            URL requestURL = new URL(httpsURL + "?message=" + command);
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
