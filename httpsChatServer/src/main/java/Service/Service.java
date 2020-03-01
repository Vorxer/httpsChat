package Service;

import Clients.Client;
import Clients.ClientServices;

import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Service implements Runnable {

    private String Message;
    private String Content;
    private String ClientID;
    private String IP;
    private String[] Anatomy;
    ClientServices clientServices;

    public static final String[] commands = {"connect","send","list"};
    private String Command;

    public Service(String message, String IP) {
        Message = message;
        Anatomy = Message.split(" ");
        Command = Anatomy[0];
        Content = Anatomy[1];

        if(!Command.equals(commands[0])){
            ClientID = clientServices.resolve(IP);
        }

        this.IP = IP;

        System.out.println("Class Service Constructed with Params:\nMessage: " + Message + "\nIP: "+ this.IP +"\n");
    }

    public void run() {
        clientServices = ClientServices.getInstance();
        if(Command.equals(commands[0])){
            String name = Anatomy[1];
            int port = Integer.parseInt(Anatomy[2]);
            connect(name,port);
        }
        else if (Command.equals(commands[1])){
            send(Message.split("-")[0]);
        }
        else if (Command.equals(commands[2])){
            list();
        }
        else {
            clientServices.message(ClientID,"Unrecognised Command","Server");
        }

    }

    public void list(){

        String list = "";
        List<String> names = clientServices.list();

        for (String name : names) {
            if(name == ClientID)
                list = list +name +"[YOU]\n";
            else
                list = list + name + "\n";
        }

        System.out.println("Messaging Initiated");
        clientServices = ClientServices.getInstance();
        clientServices.message(ClientID, list, "Server");

    }

    public void connect(String name, int port){
        try {
            //Client client = new Client(Params[1], IP, Integer.parseInt(Params[2]));
            Client client = new Client(name, IP, port);
            clientServices = ClientServices.getInstance();
            clientServices.add(client);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void send(String Message){
        try {
            System.out.println("Messaging Initiated");
            String Receiver = Message.split("-")[1];
            System.out.println(Receiver);
            System.out.println(Message.substring(Message.indexOf(" ") + 1));
            System.out.println(ClientID);
            clientServices = ClientServices.getInstance();
            clientServices.message(Receiver, Message.substring(Message.indexOf(" ") + 1), ClientID);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}