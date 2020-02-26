import Connection.Connection;

public class Client2 {
    public static void main(String[] args) throws Exception{
        Connection connection = new Connection();
        connection.connect("192.168.1.10","Jack", 8180);
    }
}
