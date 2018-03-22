package STUN.nodes;

import STUN.STUNemulator.BadRequestResponse;
import STUN.STUNemulator.BindingSuccessResponse;
import STUN.STUNrfc.Constants;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 *
 * @author hienpham
 */
import java.net.InetAddress;
public class STUNserver extends Thread {
    private ServerSocket serverSocket;
    public static int serverPort = 9090;
    public String bindingRequestResponse;
    public String badRequestResponse;
    
    public void StartServer(int serverPort)
    {
        try
        {
            serverSocket = new ServerSocket( serverPort );
            this.start();
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
        
    private void ProcessRequest(String resquestXml, Socket clientSocket) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException     
    {
        int clientPort = clientSocket.getPort();
        String clientAddress = null;
        
        if (clientSocket.getInetAddress() instanceof Inet4Address)
        {
            InetAddress address = clientSocket.getLocalAddress(); 
            clientAddress = address.getHostAddress();
        }
            
        // Extract from xmlrequest
        resquestXml = resquestXml.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
        
        InputSource source = new InputSource(new StringReader(resquestXml));
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(source);
    
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        
        String messageType = xpath.evaluate("/stun/header/Message-Type", doc);
        String trainsactionID = xpath.evaluate("/stun/header/Transaction-ID", doc);
        String magicCookies = xpath.evaluate("/stun/header/Magic-Cookie", doc);
        while(true)
        {
            if((!magicCookies.equalsIgnoreCase(Constants.MAGIC_COOKIE))||(!messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_INDICATION)&&
                !messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_BINDING_REQUEST)))
            {
                System.out.println("Wrong magic cookies. Return bad request!");
                BadRequestResponse badRequestResponse = new BadRequestResponse();
                this.badRequestResponse =  badRequestResponse.BadRequestResponse(trainsactionID); 
                // Sending response
                SendResponse(clientSocket, this.badRequestResponse);
                break;
            }
            else if(messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_BINDING_REQUEST))
            {
                BindingSuccessResponse successResponse = new BindingSuccessResponse();
                this.bindingRequestResponse =  successResponse.SuccessResponse(trainsactionID, clientPort, clientAddress); 
                // Sending response
                SendResponse(clientSocket, bindingRequestResponse);
                break;
            }
            else if(messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_INDICATION))
            {
                System.out.println("Received Indication");
                break;
            }
            break;
        }   
    }
    
    private void SendResponse (Socket clientSocket, String message)
    {
        try
        {
            byte[] bytes = new byte[16 * 1024];
            InputStream in = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
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
                RequestHandler requestHandler = new RequestHandler(clientSocket );
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
        private boolean doneSuccessResponse=false;
        RequestHandler(Socket clientSocket)
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
                //System.out.println(resquestXml);
                clientSocket.shutdownInput();
                out.close();

                // Processing request                
                ProcessRequest(resquestXml, clientSocket);               
              
                // Close all connection                
                in.close();
                out.close();
                clientSocket.close();
                System.out.println("\nConnection closed" );
                Thread.currentThread().interrupt();
            }
                
            catch( Exception e )
            {
                e.printStackTrace();
            }
        }
    }
}
