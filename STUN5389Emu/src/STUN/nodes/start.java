package STUN.nodes;

/**
 *
 * @author hienpham
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import STUN.nodes.NAT.NATServer;
import STUN.nodes.NAT.NATmain;

public class start {
    
     public static void main(String[] args) {
        System.out.print("Choose the node function: \n" );	
        System.out.print("1-STUN Client \n");
        System.out.print("2-Router with NAT functionality\n");
        System.out.print("3-STUN Server\n");

        System.out.println();
        System.out.print("$ ");
        String nodes = readCommand();        
        
        if (nodes.equals("1")){
            System.out.println("Chosen node: STUN Client");
            STUNclient client = new STUNclient();
            client.StartClient();
        }
        else if (nodes.equals("2")){
            System.out.println("Chosen node: Router with NAT functionality");
            NATServer server = new NATServer();
            server.StartServer(NATmain.serverPort);
        }
        else if (nodes.equals("3")){
            System.out.println("Chosen node: STUN Server");
            STUNserver server = new STUNserver();
            server.StartServer(STUNserver.serverPort);     
            System.out.println("Server side started at port: " +STUNserver.serverPort);
        }
        else {
        System.out.println("Wrong Command, Retry!");
        System.exit(0);
        }
    }
    
    private static String readCommand() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String input = bufferedReader.readLine();
            while (input.isEmpty())
                    input = bufferedReader.readLine();
            return input;
        } catch (IOException e) {
            System.out.println("IO error trying to read command!");
            System.exit(1);
        }
        return "";
    }
}
