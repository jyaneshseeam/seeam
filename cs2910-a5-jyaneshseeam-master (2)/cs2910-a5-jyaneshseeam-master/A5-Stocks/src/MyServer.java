import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer {
    ServerSocket ss ;
    Socket socket;
    public MyServer()  {
        try {
            //creating the connection to the port 9999
            ss= new ServerSocket(9999);
            socket = ss.accept();
            ServerConnection sc = new ServerConnection(socket);
            new Thread(sc).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static  void main(String[]args){
        new MyServer();
    }
}
