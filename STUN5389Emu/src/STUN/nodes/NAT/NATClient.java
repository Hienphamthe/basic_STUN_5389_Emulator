package STUN.nodes.NAT;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author hienpham
 */
public class NATClient extends Thread {
    private Socket socket;
    private int cPort;
    
    public void StartClient(int clientPort)
    {
        this.cPort = clientPort;
        try
        {
            // Open a socket to the server
            this.socket = new Socket("localhost", cPort);
            this.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public void StopClient()
    {
        this.interrupt();
    }
    
    @Override
    public void run()
    {
        try
        {
            System.out.println("Sending messages from port:" + socket.getLocalPort());

            byte[] bytes = new byte[16 * 1024];
            InputStream in = new ByteArrayInputStream(NATmain.messageSent.getBytes(StandardCharsets.UTF_8));
            OutputStream out = socket.getOutputStream();

            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }
            //Shutdown socket output stream, close input array stream (socket inputstream still operates)            
            socket.shutdownOutput();
            in.close();

            // Pass the socket to the RequestHandler thread for processing
            NATClient.RequestHandler requestHandler = new NATClient.RequestHandler( socket );
            requestHandler.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    class RequestHandler extends Thread
    {
        private Socket socket;
        RequestHandler( Socket socket )
        {
            this.socket = socket;
        }

        @Override
        public void run()
        {
            InputStream in = null;
            ByteArrayOutputStream out = null;
            try {
                System.out.println("Client receives response");
                in = socket.getInputStream();
                out = new ByteArrayOutputStream(); 
                
                byte[] bytes = new byte[16*1024];
                int count;
                while ((count = in.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }
                NATmain.messageReceived = new String(out.toByteArray(),"UTF-8");
                         
                // Close our connection               
                in.close();
                out.close();
                socket.close();
                System.out.println("\nConnection closed!");
                
                // Allow server side to forward message back to STUN client                
                try
                {
                    Thread.sleep( 1000 );
                }
                catch( Exception e )
                {
                    e.printStackTrace();
                }
                NATServer.waiting = false;
            
            } catch (IOException ex) {
                System.out.println("Error: Unable to read server response\n\t" + ex);
            }    
        }
    }
}
