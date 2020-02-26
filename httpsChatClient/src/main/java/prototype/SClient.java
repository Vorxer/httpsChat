package prototype;


import java.net.URL;
import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;

public class SClient {
    private static PrintWriter out = null;
    private static BufferedReader in = null;
    private static String ip = "localhost", name = "ishan", port = "8000";


    //Exception in thread "main" javax.net.ssl.SSLHandshakeException: java.security.cert.CertificateException: No name matching localhost found
    static {
        //for localhost testing only to pass the security
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier() {

                    public boolean verify(String hostname,
                                          javax.net.ssl.SSLSession sslSession) {
                        if (hostname.equals("127.0.0.1")) {
                            return true;
                        }
                        return false;
                    }
                });
    }


    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        System.out.println("please use connect ip:port as name exactly to connect !");
        String command = scanner.nextLine();
        Pattern regex1 = Pattern.compile("^connect " +
                "(?<ip>((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))" +
                ":(?<port>\\d{4})" +
                " as " +
                "(?<name>\\w*)$");
        Matcher matches = regex1.matcher(command);

        if (matches.find()) {
            ip = matches.group("ip");
            name = matches.group("name");
            port = matches.group("port");

            System.out.println("ip: " + ip);
            System.out.println("name: " + name);
            System.out.println("port: " + port);


            System.setProperty("javax.net.ssl.trustStore", "src/myKeyStore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "123456");
            // System.setProperty("javax.net.debug", "ssl:record");


            String httpsURL = "https://" + ip + ":" + port + "/register?name=" + name;
            URL myUrl = new URL(httpsURL);
            HttpsURLConnection conn = (HttpsURLConnection) myUrl.openConnection();

            //get response sout
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                System.out.println(inputLine);
            }
            br.close();

            //Communication thread started
            //connect 127.0.0.1:8000 as niroshiaa
            //send  hello kohomada->ishan
            new Thread(() -> {
                Scanner send = new Scanner(System.in);
                System.out.println("Find the other users loged using <list> command ");
                System.out.println("Now you can Send messages by send <message> name");
                while (true) {
                    System.out.print(":");
                    String sendToServer = send.nextLine();

                    //get List of users
                    if (sendToServer.equals("list")) {
                        getList();
                    }
                    //send messages to user
                    if (Pattern.matches("send (?<msg>.*)->(?<name>.*)", sendToServer)) {
                        sendToServer(sendToServer);
                    }
                    //check my message box
                    if (sendToServer.equals("inbox")) {
                        checkInbox();
                    }
                }
            }).start();


            new Thread(() -> {
                while (true) {

                    try {
                        Thread.sleep(4000);
                        String httpsURL1 = "https://127.0.0.1:8000/inbox?name=" + name;
                        URL myUrl1 = new URL(httpsURL1);
                        HttpsURLConnection conn1 = (HttpsURLConnection) myUrl1.openConnection();

                        //get response sout
                        InputStream is1 = conn1.getInputStream();
                        InputStreamReader isr1 = new InputStreamReader(is1);
                        BufferedReader br1 = new BufferedReader(isr1);
                        String inputLine1;
                        while ((inputLine1 = br1.readLine()) != null) {
                            if (!inputLine1.equals("no"))
                                System.out.println(inputLine1.substring(2,inputLine1.length()));
                        }
                        br1.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } else {
            System.out.println("Wronge Command !");
        }
    }

    private static void sendToServer(String sendToServer) {
        try {
            System.out.println("send message");
            System.out.println(sendToServer);

            Matcher matcher2 = Pattern.compile("send (?<msg>.*)->(?<name>.*)").matcher(sendToServer);
            if (matcher2.find()) {
                System.out.println("message: " + matcher2.group("msg"));
                System.out.println("receiver name: " + matcher2.group("name"));
                String message = matcher2.group("msg");
                String receiver = matcher2.group("name");
                String sender = name;
                System.out.println("sender :" + name);

                String httpsURL1 = "https://127.0.0.1:8000/send?message=" + message
                        + "&receiver=" + receiver
                        + "&sender=" + sender;
                URL myUrl1 = new URL(httpsURL1);
                HttpsURLConnection conn1 = (HttpsURLConnection) myUrl1.openConnection();

                //get response sout
                InputStream is1 = conn1.getInputStream();
                InputStreamReader isr1 = new InputStreamReader(is1);
                BufferedReader br1 = new BufferedReader(isr1);
                String inputLine1;
                while ((inputLine1 = br1.readLine()) != null) {
                    System.out.println(inputLine1);
                }
                br1.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getList() {
        try {
            System.out.println("listtttttttt");
            String httpsURL1 = "https://127.0.0.1:8000/list?name=" + name;
            URL myUrl1 = new URL(httpsURL1);
            HttpsURLConnection conn1 = (HttpsURLConnection) myUrl1.openConnection();

            //get response sout
            InputStream is1 = conn1.getInputStream();
            InputStreamReader isr1 = new InputStreamReader(is1);
            BufferedReader br1 = new BufferedReader(isr1);
            String inputLine1;
            while ((inputLine1 = br1.readLine()) != null) {
                System.out.println(inputLine1);
            }
            br1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkInbox() {
        try {
            // System.out.println("inbox________________");
            String httpsURL1 = "https://127.0.0.1:8000/inbox?name=" + name;
            URL myUrl1 = new URL(httpsURL1);
            HttpsURLConnection conn1 = (HttpsURLConnection) myUrl1.openConnection();

            //get response sout
            InputStream is1 = conn1.getInputStream();
            InputStreamReader isr1 = new InputStreamReader(is1);
            BufferedReader br1 = new BufferedReader(isr1);
            String inputLine1;
            while ((inputLine1 = br1.readLine()) != null) {
                if (!inputLine1.equals("no")) {
                    System.out.println(inputLine1);
                } else {
                    System.out.println("NO messages !");
                }
            }
            br1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}