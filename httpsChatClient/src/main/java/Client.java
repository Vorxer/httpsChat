import Connection.Connection;

public class Client {
    public static void main(String[] args) throws Exception{
        Connection connection = new Connection();
        connection.connect("192.168.1.10","John",8182);
        Thread.sleep(1000);
        connection.message("send HOLY SHIT IT WORKS ->Jack");

        while (true){

        }

    }
}
