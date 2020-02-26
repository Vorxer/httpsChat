package Clients;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClientServices {

    private static ClientServices instance;

    private ArrayList<Client> clients = new ArrayList<Client>();

    private ClientServices(){}

    public static synchronized ClientServices getInstance(){
        if(instance == null){
            instance = new ClientServices();
        }
        return instance;
    }

    public List<String> list(){

        List<String> names =
                clients.stream()
                        .map(Client::getName)
                        .collect(Collectors.toList());
        return names;
    }

    public String resolve(String IP){

        Client c = clients.stream().filter(client -> IP.equals(client.getIP())).findAny().orElse(null);
        System.out.println(c.getName());
        return c.getName();

    }

    public boolean verify(String Name){
        System.out.println("Method verify initiated");
        List<Client> c = clients.stream().filter(client -> Name.equals(client.getName())).collect(Collectors.toList());
        if(c.isEmpty()){
            return false;
        }
        System.out.println("Existing client found, IP: " + c.get(0).getIP());

        return true;
    }

    public void add(Client c){

        System.out.println("ClientServices Method add initiated");

        if(!verify(c.getName())) {
            System.out.println("Name is unique, adding to list");
            this.clients.add(c);
        }

    }

    public void message(String Name, String Message, String Sender){

        Client c = clients.stream().filter(client -> Name.equals(client.getName())).findAny().orElse(null);
        try {
            c.sendMessage(Message, Sender);
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }
    }

}
