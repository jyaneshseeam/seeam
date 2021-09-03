import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
//database xStocks is initialised with username field with data andrew as username and some tickers already for grader
public class ServerConnection implements Runnable
{
        private Socket s;
        private PrintWriter out;
    Scanner in ;
    //A set of tickers so that no duplicate tickers are to be stored
    Set<String> myTickers= new HashSet<>();
    StockPriceParser mypriceParser = new StockPriceParser();
    public ServerConnection(Socket aSocket)
        {
            s = aSocket;
        }
//run method for thread
        public void run()
        {
            try
            {
                try
                {
                    in = new Scanner(s.getInputStream());
                    out = new PrintWriter(s.getOutputStream());
                    try {
                        doService();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                finally
                {
                    s.close();
                }
            }
            catch (IOException exception)
            {
                exception.printStackTrace();
            }
        }

        /**
         Executes all commands until the QUIT command or the
         end of input.
         */
        //user interface for input and output
        public void doService() throws Exception {
            String command = in.nextLine();
            while (!command.equals("quit")) {
                if (command.contains("USER")) {
                   command=  command.replace("USER ", "");
                    command =command.replace("!", "");
                    String username = command;
                    System.out.println(username);
                    out.println("Ok!");
                    out.flush();
                    while (!command.equals("quit")) {
                        readUser(username);
                        System.out.println(myTickers);
                        command = in.nextLine();
                        if (command.equals("PORTFOLIO!")) {
                            System.out.println(myTickers);
                            out.println(portfolio());
                            out.flush();
                        } else if (command.contains("TRACK")) {//isolating the ticker from the whole command
                            command = command.replaceAll("TRACK ", "");
                            command =command.replace("!", "");
                            String ticker=command;
                            System.out.println(ticker);
                            addTicker(username, ticker);
                            out.println("OK!");
                            out.flush();

                            System.out.println(myTickers);
                        } else if (command.contains("FORGET")) {
                            command=command.replaceAll("FORGET ", "");
                            command=command.replace("!", "");
                            forget(username, command);
                            out.println("OK!");
                            out.flush();
                            System.out.println(myTickers);

                        } else {
                            out.println("Error!");
                        }
                    }

                } else {
                    out.println("Error!");
                    out.flush();
                }
                command = in.nextLine();
            }
        }
        //deleting a ticker from table
    private void forget(String username, String ticker) {
        try {
            SimpleDataSource.init("src/database.properties");
        }catch(IOException | ClassNotFoundException e ) {
            e.printStackTrace();
        }
        try (Connection conn = SimpleDataSource.getConnection()) {
            PreparedStatement stat = conn.prepareStatement("Delete from xStock where Username= ? AND Tickers=? ");
            String remove = "";
            for (String myTicker : myTickers) {
                if(myTicker.contains(ticker)){
                    remove= myTicker;
                }
            }
            myTickers.remove(remove);
            stat.setString(1,username);
            stat.setString(2,ticker);
            stat.execute();
            readUser(username);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    //adding a new ticker to table

    private void addTicker(String username, String ticker) {
        try {
            SimpleDataSource.init("src/database.properties");
        }catch(IOException | ClassNotFoundException e ) {
            e.printStackTrace();
        }
        try (Connection conn = SimpleDataSource.getConnection())  {
            Statement stat=conn.createStatement();
            //getting a unique random id
            Random index = new Random();
            PreparedStatement pstmt = conn.prepareStatement("Insert into xStock (ID,Username,Tickers) Values(?,?,?)");
            pstmt.setInt(1, index.nextInt(1000000));

            pstmt.setString(2,username);
            pstmt.setString(3,ticker);
            pstmt.execute();
            readUser(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //inputing user  ticker data of username into set of tickers
    private void readUser(String username) throws SQLException {
            //reads user tickers into mytickers.
        try {
            SimpleDataSource.init("src/database.properties");
        }catch(IOException e ) {
            e.printStackTrace();
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (Connection conn = SimpleDataSource.getConnection())  {
            //the code below was used to create the initial table and to populate it initialy
            /*Statement ps=conn.createStatement();
            String sqlString= "CREATE TABLE xStock (ID INT PRIMARY KEY,Username CHAR(12), Tickers CHAR(12) )";
            ps.execute(sqlString);
            PreparedStatement ptat =conn.prepareStatement("Insert into xStock (ID,Username,Tickers) Values(?,?,?)");
            ptat.setInt(1,1);
            ptat.setString(2,username);
            ptat.setString(3,"aapl");
            ptat.executeUpdate();*/

            PreparedStatement pstat =conn.prepareStatement("Select * FROM xStock where username=?");
              pstat.setString(1, username);
            ResultSet rs =pstat.executeQuery();
            while (rs.next()) {
               myTickers.add(rs.getString("Tickers"));
            }
        }
    }
    //returning all Stock prices of tickers stored in myTickers set
    public String portfolio() throws Exception {
            String outputString = "";
            for(String ticker: myTickers){
                outputString +=" "+ticker+" "+mypriceParser.call_me(ticker)+"\n";
            }
             outputString+="!";
            return outputString;
        }


}


