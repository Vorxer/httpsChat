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
    private String ClientID;
    private String IP;
    ClientServices clientServices;
    public Service(String message, String IP) {
        Message = message;
        clientServices = ClientServices.getInstance();
        String command = Message.split(" ")[0];
        System.out.println("COMMAND = " + command);
        if(!command.equals("connect")){
            ClientID = clientServices.resolve(IP);
        }
        this.IP = IP;

        System.out.println("Class Service Constructed with Params: \nMessage: " + Message + "\nIP: "+ this.IP +"\n");
    }

    public void run() {
        System.out.println("Service Thread started");
        String[] Parts = Message.split(" ");
        System.out.println("Run Anatomy: "+ Arrays.toString(Parts));
        System.out.println("CIR" + Parts[0]);
        if(Parts[0].equals("connect")){
            connect(Parts);
        }
        if (Parts[0].equals("send")){
            send(Message);
        }
        if (Parts[0].equals("list")){
            list();
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
        clientServices.message(ClientID, list, "");

    }

    public void connect(String[] Params){
        System.out.println("Service Method Connect Fired");
        System.out.println("Service Method Connect Creating class with params : \nName: " + Params[1] +"\nIP: "+IP +"\nPort: " + Integer.parseInt(Params[2]));
        try {

            Client c = new Client(Params[1], IP, Integer.parseInt(Params[2]));
            clientServices = ClientServices.getInstance();
            clientServices.add(c);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void send(String Message){

        //Send the message to the client
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