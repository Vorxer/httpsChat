package Connection;

import Listener.Listener;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.cert.X509Certificate;

public class Connection{

    private String IP;
    private String ID;
    private String httpsURL = "";

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
        request("connect " + ID + " " + port);
    }

    public void receive(int port){
        Listener listener = new Listener(port);
        Thread ServerThread = new Thread(listener);
        ServerThread.start();
    }


    public void request(String command) throws Exception{
        if(!command.equals("")) {
            command = command.replace(" ", "+");

            URL requestURL = new URL(httpsURL + "?message=" + command);


            HttpsURLConnection conn = (HttpsURLConnection) requestURL.openConnection();

            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String inputLine;

            while ((inputLine = bufferedReader.readLine()) != null) {
                System.out.println(inputLine);
            }

            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        }
    }

}
