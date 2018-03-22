package STUN.nodes.NAT;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author hienpham
 */
public class NATServer extends Thread {
    private ServerSocket serverSocket;
    private int sPort;
    public static boolean waiting;
        
    public void StartServer(int serverPort)
    {
        this.sPort = serverPort;
        try
        {
            serverSocket = new ServerSocket( sPort );
            this.start();
            System.out.println("Server side started at port: " +sPort);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public void StopServer()
    {
        this.interrupt();
    }
    
    private void ForwardRequest()
    {
        NATClient client = new NATClient();
        client.StartClient(NATmain.toServerPort);
    }
    
    private void ForwardResponse(Socket clientSocket)
    {
        try
        {
            byte[] bytes = new byte[16 * 1024];
            InputStream in = new ByteArrayInputStream(NATmain.messageReceived.getBytes(StandardCharsets.UTF_8));
            OutputStream out = clientSocket.getOutputStream();

            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }
            out.close();
            in.close();
            }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }   
    
    @Override
    public void run()
    {     
        while(true)
        {
            try
            {
                // Call accept() to receive the next connection
                Socket clientSocket = serverSocket.accept();

                // Pass the socket to the RequestHandler thread for processing
                RequestHandler requestHandler = new RequestHandler( clientSocket );
                requestHandler.start();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    class RequestHandler extends Thread
    {
        private Socket clientSocket;
        RequestHandler( Socket clientSocket )
        {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run()
        {
            InputStream in = null;
            ByteArrayOutputStream out = null;
            try
            {
                // Getting the request from client 
                System.out.println("\nReceived a connection" );
                in = clientSocket.getInputStream();
                out = new ByteArrayOutputStream(); 
                
                byte[] bytes = new byte[16*1024];
                int count;
                while ((count = in.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }
                String resquestXml = new String(out.toByteArray(),"UTF-8");
//                System.out.println(resquestXml);
                clientSocket.shutdownInput();
                out.close();

                // Start cient side and forward request
                NATmain.messageSent = resquestXml;
                ForwardRequest();
                
                // Waiting for STUN server
                waiting = true;
                while(waiting)
                {
                    //waiting
                    try
                    {
                        Thread.sleep( 1000 );
                    }
                    catch( Exception e )
                    {
                        e.printStackTrace();
                    }
                    System.out.println("NAT waiting..." );
                }
                    
                // Forward response back to STUN client
                ForwardResponse(clientSocket);
              
                // Close all connection                
                in.close();
                out.close();
                clientSocket.close();
                Thread.currentThread().interrupt();
                System.out.println("\nConnection closed" );
            }
                
            catch( Exception e )
            {
                e.printStackTrace();
            }
            
            
            
            
//            try
//            {
//                System.out.println("\nReceived a connection" );
//
//                // Get input and output streams
//                BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
//                PrintWriter out = new PrintWriter( socket.getOutputStream() );
//
//                // Echo lines back to the client until the client closes the connection or we receive an empty line
//                NATmain.messageSent = in.readLine();
//                
//                if (NATmain.messageSent != null && NATmain.messageSent.length() > 0)
//                {
//                    NATClient client = new NATClient();
//                    client.startClient(NATmain.toServerPort);
//                    waiting = true;
//                    while(waiting)
//                    {
//                        //waiting
//                        try
//                        {
//                            Thread.sleep( 1000 );
//                        }
//                        catch( Exception e )
//                        {
//                            e.printStackTrace();
//                        }
//                        System.out.println("NAT waiting..." );
//                    }
//                }
//                out.println(NATmain.messageReceived);
//                out.flush();                                   
//                
//                // Close our connection
////                in.close();
////                out.close();
////                socket.close();
//                System.out.println("Server side connection closed" );
//            }
//            catch( Exception e )
//            {
//                e.printStackTrace();
//            }
        }
    }
}

