import Connection.Connection;
import java.util.Scanner;

public class ChatClient {
    public static Connection connection = new Connection();
    public static boolean connected = false;
    public static String name;

    public static void main(String[] args) throws Exception{
        Scanner input = new Scanner(System.in);


        while (true){
            if(connected){
                System.out.println("Ready:");
                String message = input.nextLine();
                connection.message(message);
            }
            else{
                System.out.println("No Connection Found \n Enter Server IP Address");
                String IP = input.nextLine();
                System.out.println("Enter you addressing name");
                name = input.nextLine();
                System.out.println("Enter the local port to receive messages from the server");
                int port = input.nextInt();

                System.out.println(IP+name+port);
                connection.connect(IP,name,port);
                connected = true;
                Thread.sleep(1000);
            }
        }
    }

}
