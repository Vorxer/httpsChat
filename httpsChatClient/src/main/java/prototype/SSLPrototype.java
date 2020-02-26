package prototype;


import java.net.URL;
import java.io.*;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.*;

public class SSLPrototype {
    public static void main(String[] args) {
        try {

// Create a trust manager that does not validate certificate chains
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
            // System.setProperty("javax.net.debug", "ssl:record");

            //System.out.println(System.getProperty("javax.net.ssl.trustStore"));
            //System.out.println(System.getProperty("javax.net.ssl.trustStorePassword"));;


            String httpsURL = "https://192.168.1.10:8000/test";
            URL myUrl = new URL(httpsURL);
            HttpsURLConnection conn = (HttpsURLConnection) myUrl.openConnection();

            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                System.out.println(inputLine);
            }
            br.close();
        }catch (Exception e){
            System.out.println("EXCEPTION OCCURED");
            e.printStackTrace();
        }
    }
}
