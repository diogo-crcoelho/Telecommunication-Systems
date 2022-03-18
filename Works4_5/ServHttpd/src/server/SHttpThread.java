/**
 * Sistemas de Telecomunicacoes
 *          2020/2021
 *
 * SHttpThread.java
 *
 * Thread class that handles the (over)simplified HTTP protocol message exchange
 *
 * @author Luis Bernardo / Paulo Pinto
 */
package server;

import com.sun.corba.se.impl.naming.cosnaming.NamingContextImpl;
import java.io.*;
import java.net.*;
import java.util.*;

public class SHttpThread extends Thread {

    /**
     * Reference to the GUI object
     */
    ServHttpd root;
    /**
     * Server socket where connects are accepted
     */
    ServerSocket ss;
    /**
     * Connection socket, to communicate with the browser
     */
    Socket client;
    /**
     * Sequence number of a sent AUTH_REQ waiting for an answer, or -1  
     */
    int seq;

    
    /**
     * Create a new instance of HttpThread
     *
     * @param root GUI object
     * @param ss Server socket
     * @param client Connections socket
     */
    public SHttpThread(ServHttpd root, ServerSocket ss, Socket client) {
        this.root = root;
        this.ss = ss;
        this.client = client;
        this.seq = -1;      // No pending query
    }

    
    /**
     * Parse the lines with properties following the main HTTP header and 
     *      return the Authorization line value
     *
     * @param in Input stream from browser
     * @return Properties with properties defined
     * @throws java.io.IOException
     */
    public String parse_Authorization(BufferedReader in) throws IOException {
        String req, authorization= null;
        int ix;

        // Get all header parameters
        // Get all header parameters
        while (((req = in.readLine()) != null) && (req.length() != 0)) {
            if (req.contains("Authorization")) {
                root.Log("hdr(" + req + ")\n"); // Writes the Authorization header line in the GUI
                if ((ix = req.indexOf(':')) != -1) { // If it is a valid structure
                    req= req.substring(ix + 1).trim(); // Remove spaces before and after                    
                    if ((ix = req.indexOf(" ")) != -1) // If there is a space in the middle       
                        req = req.substring(ix + 1).trim();     // Remove the preceeding 'Basic' word
                    authorization = new String(java.util.Base64.getDecoder().decode(req));   // Decode Base64 format                   
                }
            } else {
                System.out.println("hdr(" + req + ")");
            }
        }
        return authorization;
    }

    
    /**
     * Send a "401 Unauthorized" error message to the brower
     *
     * @param pout Stream object associated to the client's socket

     */
    public void return_unauthorized(PrintStream pout) {
        /********************************
         * Lab work 5 - TASK 2: 
         *      Modify this function to return an unauthorized header error message. 
         */
        // ...
        if (pout == null) {
            return;
        }
        try{
            pout.print("HTTP/1.0 401 Unauthorized\r\n");
            pout.print("WWW-Authenticate: Basic realm = \"ST 2020/2021 by 53287 domain\" \r\n");
            pout.print("\r\n");
            
        }catch(Exception e){
             System.out.println("Error " + e);
        }
        return;
    }

    
    /**
     * Sends a "404 Not Found" error message to the brower, with a sample HTML web page
     *
     * @param pout Stream object associated to the client's socket
     */
    public void return_error(PrintStream pout) {
        if (pout == null) {
            return;
        }
        pout.print("HTTP/1.0 404 Not Found\r\nServer: " + ServHttpd.SERVER_NAME + "\r\n\r\n");
        // Prepares a web page with an error description
        pout.print("<HTML>\r\n");
        pout.print("<HEAD><TITLE>Not Found</TITLE></HEAD>\r\n");
        pout.print("<H1> Page not found </H1>\r\n");
        pout.print("</HTML>\r\n");
    }

    
    /**
     * Threads code that waits for a request and send the answer
     */
    @Override
    public void run() {
        PrintStream pout = null;
        DatagramSocket ds; // Datagram socket, to communicate with the authentication server

        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream(), "8859_1"));
            OutputStream out = client.getOutputStream();
            pout = new PrintStream(out, false, "8859_1");
            ds = new DatagramSocket();

            String request = in.readLine(); // Read the first line 
            if (request == null) // End of connection
            {
                return;
            }

            // The first request line has the format GET /file.html HTTP/1.1â€?
            // This code uses the â€œStringTokenizer class to separate the 3 components
            root.Log("\nRequest= '" + request + '\n');
            StringTokenizer st = new StringTokenizer(request);
            if (st.countTokens() != 3) // If it does not have the 3 components isn't valid
            {
                return; // Invalid request
            }
            st.nextToken(); // Jumps the first token
            String file = st.nextToken(); // Gets the second token and ignores the third one
            
            /********************************
             * Lab work 5 - TASK 1: 
             *   Modify this function to check if the browser is sending the Authorization
             *   header with a valid user:password. 
             *      To implement this task, you will need to use:
             *          - function parse_Authorization of this class
             *          - function validate_cached_passwd of class ServHttpd
             *          - function return_unauthorized of this class
             *   Suggestion: the function parse_Authorization returns a string with the format
             *       user:pass. Create code that decomposes "user:pass" into "user" and "pass"
             *       You will find more information on how to do it in section 2.7.1 of the Java tutorial.
             *   
             */
            String auth = parse_Authorization(in);
            root.Log("Debug: "+ auth +"\n");
            if (auth == null || auth.equals(":")){
                return_unauthorized(pout);
            }
            else{
                String user = auth.split(":")[0], pass = auth.split(":")[1];
                
                
                if (root.validate_cached_passwd(user,pass)){
                    
                }
                else{
                    return_unauthorized(pout);
                }
            }
            // Send file
            String filename = root.getRaizHtml() + file + (file.equals("/") ? "index.htm" : "");
            System.out.println("Filename= " + filename);
            FileInputStream fis = new FileInputStream(filename);
            byte[] data = new byte[fis.available()]; // Alocate an array with the size of the file 
            fis.read(data); // Read the entire file to memory

            // Writes the HTTP "200 OK" header to the browser
            pout.print("HTTP/1.0 200 OK\r\nServer: " + ServHttpd.SERVER_NAME + "\r\n\r\n");
            out.write(data); // Writes the file contents to the socket
            out.flush(); // Forces the sending
            fis.close(); // Closes the file 

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            // Writes a "file not found" error to the socket
            return_error(pout);
        } catch (SocketException e) {
            System.out.println("Error creating datagram socket: " + e + "\n");
        } catch (IOException e) {
            System.out.println("I/O error " + e);
        } catch (Exception e) {
            System.out.println("Error " + e);
        } finally {
            // This code is always run, even when there are exceptions
            try {
                client.close(); // Closes the socket and all associated streams
            } catch (Exception e) { /* Ignore everything */ }
        }
    }
}
