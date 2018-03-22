package STUN.nodes;

import STUN.STUNemulator.BadRequest;
import java.io.IOException;
import java.net.Socket;

import STUN.STUNemulator.BindingRequest;
import STUN.STUNemulator.Indication;
import STUN.STUNrfc.Constants;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
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
public class STUNclient extends Thread {
    private Socket socket;
    private int oldPort;
    private static int toServerPort = 8080;
    private String bindingRequestMessage;
    private String indicationMessage;
    private String badRequestMessage;
    private String currentStunID;

    public void StartClient()
    {
        try
        {
            // Open a socket to the server
            this.socket = new Socket("localhost", toServerPort);
            this.oldPort = socket.getPort();
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
    
    // Send request/indication
    private void SendRequest(String message) 
    {
        try
        {            
            byte[] bytes = new byte[16 * 1024];
            InputStream in = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
            OutputStream out = socket.getOutputStream();

            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }
            //Shutdown socket output stream, close input array stream (socket inputstream still operates)            
            socket.shutdownOutput();
            in.close();            
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    // Process Response
    private void ProcessResponse(String responseXml) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException 
    {
        int oldPort = this.oldPort;
        // Extract from xmlrequest
        responseXml = responseXml.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
        
        InputSource source = new InputSource(new StringReader(responseXml));
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(source);
    
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        
        String reflexiveTransportAddress = xpath.evaluate("/stun/attribute/XOR-MAPPED-ADDRESS/IP", doc);
        String reflexiveTransportPort = xpath.evaluate("/stun/attribute/XOR-MAPPED-ADDRESS/Port", doc);
        
        String messageType = xpath.evaluate("/stun/header/Message-Type", doc);
        String transactionID = xpath.evaluate("/stun/header/Transaction-ID", doc);
        String magicCookies = xpath.evaluate("/stun/header/Magic-Cookie", doc);
        String errorClass = xpath.evaluate("/stun/attribute/ERROR-CODE/Error-Class", doc);
        String errorCode = xpath.evaluate("/stun/attribute/ERROR-CODE/Error-Code", doc);
 
        while(true)
        {            
            if((!magicCookies.equalsIgnoreCase(Constants.MAGIC_COOKIE))||(!messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_BINDING_RESPONSE_SUCCESS)&&
                !messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_BINDING_RESPONSE_ERROR)))
            {
                System.out.println("Wrong magic cookies or message type. Discard!");
                break;
            }           
            else if(transactionID.equalsIgnoreCase(currentStunID))
            {
                if(messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_BINDING_RESPONSE_SUCCESS))
                {
                    System.out.println("My Public IP Address is " + reflexiveTransportAddress + " on port " +reflexiveTransportPort);
                    break;
                }
                else if(messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_BINDING_RESPONSE_ERROR))
                {
                    if(errorClass.equalsIgnoreCase("4")&&errorCode.equalsIgnoreCase("0"))
                    {
                        System.out.println("Bad request! Sending request again:");
                        BindingRequest bindingRequest = new BindingRequest();
                        //Resending the corrected form request
                        this.bindingRequestMessage =  bindingRequest.BindingRequest();
                        this.currentStunID = bindingRequest.StunID;
                        //Send binding request   
                        this.socket = new Socket("localhost", toServerPort);
                        SendRequest(bindingRequestMessage);
                        // Pass the socket to the RequestHandler thread for processing
                        STUNclient.RequestHandler requestHandler = new STUNclient.RequestHandler( socket );
                        requestHandler.start();
                        Thread.currentThread().interrupt();
                        break;
                    }
                    else break;
                }
                break;
            }
            else 
                System.out.println("Wrong syntax!");  
                break;
        }
    }
    
    @Override
    public void run()
    {        
        //Select use-case
        System.out.print("Choose the client use case: \n" );	
        System.out.print("1-Sending binding request \n");
        System.out.print("2-Sending indication\n");
        System.out.print("3-Sending binding request (error)\n");
        while(true)
        {
            System.out.println();
            System.out.print("$ ");
            String selection = readCommand(); 
            if (selection.equals("1")){
                BindingRequest bindingRequest = new BindingRequest();
                this.bindingRequestMessage =  bindingRequest.BindingRequest();
                this.currentStunID = bindingRequest.StunID;
                //Send binding request   
                SendRequest(bindingRequestMessage);
                // Pass the socket to the RequestHandler thread for processing
                STUNclient.RequestHandler requestHandler = new STUNclient.RequestHandler( socket );
                requestHandler.start();
                break;
            }
            else if (selection.equals("2")){
                Indication indication = new Indication();
                this.indicationMessage =  indication.Indication();
                // No need to save currentStunID
                // Send indication
                SendRequest(indicationMessage);
                Thread.currentThread().interrupt();
                break;
            }
            else if (selection.equals("3")){
                BadRequest badRequest = new BadRequest();
                this.badRequestMessage =  badRequest.BadRequest();
                this.currentStunID = badRequest.StunID;
                //Send binding request   
                SendRequest(badRequestMessage);
                // Pass the socket to the RequestHandler thread for processing
                STUNclient.RequestHandler requestHandler = new STUNclient.RequestHandler( socket );
                requestHandler.start();
                break;
            }
            else {
            System.out.println("Wrong Command, Retry!");
            }
            break;
        }  
        System.out.println( "Sending messages from port:" + socket.getLocalPort());
        // Do not close this thread, since it keep client port alive
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
            // Getting the response from server            
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
                String responseXml = new String(out.toByteArray(),"UTF-8");
                // System.out.println(responseXml); 
                ProcessResponse(responseXml);
                // Close our connection               
                in.close();
                out.close();
                socket.close();
                System.out.println("\nConnection closed!");
            
            
            } catch (IOException ex) {
                System.out.println("Error: Unable to read server response\n\t" + ex);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }      
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
