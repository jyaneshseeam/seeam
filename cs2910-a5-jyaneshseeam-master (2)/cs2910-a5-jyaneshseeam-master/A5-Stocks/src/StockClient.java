import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
//client class
public class StockClient {
    public static void main(String[]args) throws IOException {
        Socket s = new Socket("127.0.0.1", 9999);
        Scanner in = new Scanner(s.getInputStream());
        PrintWriter out = new PrintWriter(s.getOutputStream());
        Scanner input = new Scanner(System.in);
        String temp =  input.nextLine();
        //user interface for INPUT AND OUTPUT of data
        while (!temp.equals("quit")) {
            out.println(temp);
            out.flush();
            String outString= "";
            while(!outString.contains("!")) {
                outString=in.nextLine();
                System.out.println(outString);
            }
            temp =  input.nextLine();
        }

    }
}
