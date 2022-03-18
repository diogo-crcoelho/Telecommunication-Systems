/**
 * Sistemas de Telecomunicacoes 
 *          2020/2021
 *
 * ServHttpd.java
 *
 * Main class with graphical interface and server control logic
 *
 * @author Luis Bernardo / Paulo Pinto
 */

package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

public class ServHttpd extends javax.swing.JFrame {
    /**
     * Server name
     */
    public final static String SERVER_NAME = "HTTP Serv by 53287";

    /**
     * Maximum packet size for AUTH_REQ and AUTH_REP
     */
    public final static int MAX_PACKET_LENGTH = 120;
    /**
     * Default file name when browser sends "/"
     */
    public final static String HOMEFILENAME = "index.htm";
    /**
     * Accepts up to 10 pending TCP connections
     */
    public final static int MAX_ACCEPT_LOG = 10;
    /**
     * Maximum timeout waiting for answer from Authentication server [ms]
     */
    public final static int AUTH_TIMEOUT = 5000;
    /**
     * Maximum time a password can be stored in cache [ms]
     */
    public final static int MAX_PASSW_STORE_TIME = 5000;
    /**
     * Server socket where new connections are accepted
     */
    public ServerSocket ssock;
    /**
     * Main thread that accepts new connections
     */
    Daemon_tcp main_thread = null;
    /**
     * Datagram socket used to send REGIST messages to the proxy
     */
    public DatagramSocket ds;
    /**
     * Number of the student
     */
    public final int number= 0;
    /**
     * Sequence number for AUTH_REQ messages
     */
    private static int AUTH_Req_seq= 0;
    /**
     * HashMap with password stored
     */
    private final HashMap<String,Date> stored_passwd;
    
    
    /**
     * Constructor
     * @param type  type format to be used
     */
    public ServHttpd(byte type) {
        initComponents();
        super.setTitle(SERVER_NAME);
        stored_passwd= new HashMap<String,Date>();
        ds = null;
        ssock = null;
        if ((type > 0) && (type<9)) {
            this.jTextType.setText(Byte.toString(type));
        }
        System.out.println("type= "+type);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jLabel5 = new javax.swing.JLabel();
        jTextLocalIP = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextLocalPort = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jTextRaizHtml = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextUser = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextPass = new javax.swing.JTextField();
        jButtonTest = new javax.swing.JButton();
        jButtonClear = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextType = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextAuthIP = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextAuthPort = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setTitle("Defined in constructor");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setMaximumSize(new java.awt.Dimension(450, 35));
        jPanel1.setMinimumSize(new java.awt.Dimension(450, 35));
        jPanel1.setName("Estado"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(450, 35));

        jToggleButton1.setText("Active");
        jToggleButton1.setMaximumSize(new java.awt.Dimension(85, 29));
        jToggleButton1.setPreferredSize(new java.awt.Dimension(85, 29));
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jToggleButton1);

        jLabel5.setText("IP");
        jPanel1.add(jLabel5);

        jTextLocalIP.setEditable(false);
        jTextLocalIP.setPreferredSize(new java.awt.Dimension(200, 20));
        jPanel1.add(jTextLocalIP);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setText("Port");
        jLabel4.setMaximumSize(new java.awt.Dimension(35, 17));
        jLabel4.setPreferredSize(new java.awt.Dimension(35, 17));
        jPanel1.add(jLabel4);

        jTextLocalPort.setText("20000");
        jPanel1.add(jTextLocalPort);

        getContentPane().add(jPanel1);

        jPanel3.setMaximumSize(new java.awt.Dimension(390, 37));
        jPanel3.setMinimumSize(new java.awt.Dimension(60, 33));
        jPanel3.setPreferredSize(new java.awt.Dimension(480, 33));

        jLabel8.setText("Html:");
        jPanel3.add(jLabel8);

        jTextRaizHtml.setText("/Users/diogo/Desktop/html");
        jTextRaizHtml.setPreferredSize(new java.awt.Dimension(330, 24));
        jTextRaizHtml.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextRaizHtmlFocusLost(evt);
            }
        });
        jTextRaizHtml.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextRaizHtmlActionPerformed(evt);
            }
        });
        jPanel3.add(jTextRaizHtml);

        getContentPane().add(jPanel3);

        jPanel4.setMaximumSize(new java.awt.Dimension(32767, 39));

        jLabel2.setText("User: ");
        jPanel4.add(jLabel2);

        jTextUser.setText("user");
        jTextUser.setPreferredSize(new java.awt.Dimension(50, 28));
        jPanel4.add(jTextUser);

        jLabel9.setText("Pass:");
        jPanel4.add(jLabel9);

        jTextPass.setText("pass");
        jTextPass.setPreferredSize(new java.awt.Dimension(60, 26));
        jPanel4.add(jTextPass);

        jButtonTest.setBackground(new java.awt.Color(255, 255, 0));
        jButtonTest.setForeground(new java.awt.Color(255, 0, 51));
        jButtonTest.setText("Test Auth server");
        jButtonTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTestActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonTest);

        jButtonClear.setText("Clear");
        jButtonClear.setMaximumSize(new java.awt.Dimension(75, 29));
        jButtonClear.setPreferredSize(new java.awt.Dimension(75, 29));
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonClear);

        getContentPane().add(jPanel4);

        jPanel2.setToolTipText("configura��o");
        jPanel2.setMaximumSize(new java.awt.Dimension(440, 35));
        jPanel2.setMinimumSize(new java.awt.Dimension(420, 35));
        jPanel2.setName("configuracao"); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(440, 35));

        jLabel1.setText("  Security:");
        jPanel2.add(jLabel1);

        jLabel7.setText("Type");
        jPanel2.add(jLabel7);

        jTextType.setText("1");
        jTextType.setPreferredSize(new java.awt.Dimension(25, 19));
        jPanel2.add(jTextType);

        jLabel3.setText("  IP ");
        jPanel2.add(jLabel3);

        jTextAuthIP.setEditable(false);
        jTextAuthIP.setText("127.0.0.1");
        jTextAuthIP.setMinimumSize(new java.awt.Dimension(14, 27));
        jTextAuthIP.setPreferredSize(new java.awt.Dimension(130, 24));
        jPanel2.add(jTextAuthIP);

        jLabel6.setText("Port ");
        jPanel2.add(jLabel6);

        jTextAuthPort.setText("20001");
        jTextAuthPort.setPreferredSize(new java.awt.Dimension(60, 24));
        jPanel2.add(jTextAuthPort);

        getContentPane().add(jPanel2);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(360, 180));

        jTextArea1.setLineWrap(true);
        jTextArea1.setPreferredSize(new java.awt.Dimension(200, 2000));
        jTextArea1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextArea1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTextArea1);

        getContentPane().add(jScrollPane1);

        getAccessibleContext().setAccessibleName("HTTP GUI - RIT2 2011/2012 by ?????/?????/?????");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    /**
     * Validates the html root directory name where the files are read
     * @param f
     */
    public void validate_name(javax.swing.JTextField f) {
        String str = f.getText();
        if (str.length() == 0) {
            return;
        }
        str= str.trim();
        if (str.charAt(str.length() - 1) != File.separatorChar) {
            str = str + File.separatorChar;
        } else {
            while ((str.length() > 1) && (str.charAt(str.length() - 2) == File.separatorChar)) {
                str = str.substring(0, str.length() - 1);
            }
        }
        f.setText(str);
    }

    
    /**
     * Automatically validates the html directory name
     */
    private void jTextRaizHtmlFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextRaizHtmlFocusLost
        validate_name(jTextRaizHtml);
    }//GEN-LAST:event_jTextRaizHtmlFocusLost

    
    /**
     * Clear the text area
     */
    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        jTextArea1.setText("");
    }//GEN-LAST:event_jButtonClearActionPerformed

    
    /**
     * Clear the text area
     */
    private void jTextArea1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextArea1KeyPressed
        if (evt.getKeyChar() == ' ') {
            jTextArea1.setText("");
        }
    }//GEN-LAST:event_jTextArea1KeyPressed

    
    /**
     * Handles the button that starts and stops the Server 
     */
    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        if (jToggleButton1.isSelected()) {
            // Parse GUI parameters            
            int tcp_port;
            try {
                tcp_port = Integer.parseInt(jTextLocalPort.getText());
            } catch (NumberFormatException e) {
                Log("Invalid port number\n");
                jToggleButton1.setSelected(false);
                return;
            }
            // Starts the server socket
            int cnt = 0;
            do {
                try {
                    ssock = new ServerSocket(tcp_port+cnt, MAX_ACCEPT_LOG);
                } catch (java.io.IOException e) {
                    // If the tcp_port is already used, try the next one until reaching a free one
                    if (cnt>100) {
                        Log("Web server start failure: " + e + "\n");
                        jToggleButton1.setSelected(false);
                        return;
                    }
                    cnt++;
                }
            } while (ssock == null);
            jTextLocalPort.setText(Long.toString(tcp_port+cnt)); // Writes the port used.
            
            // Gets the local IP
            try {
                jTextLocalIP.setText(InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                Log("Failed to get local IP: " + e + "\n");
            }
            
            // Start the UDP socket
            try{
                ds = new DatagramSocket(0);     // 0 means, use a random free port
            } catch (SocketException ex) {
                Log("Failed creation of UDP socket: " + ex + "\n");
                try {
                    ssock.close();
                } catch (IOException ex1) {
                    Log("Error closing TCP socket: " + ex1 + "\n");
                    ssock = null;
                }
                jToggleButton1.setSelected(false);
                return;
            }
            try {
                ds.setSoTimeout(2000); // 2 segundos P=3
            } catch (SocketException e) {
                System.err.println("No packet within 2 seconds");
            }
            // starts the main thread (that receives connections)
            main_thread = new Daemon_tcp(this, ssock);
            main_thread.start();
            
            setEditable_jText(false);
            Log("Web server active\n");
        } else {
            // Stop the server socket thread and the sockets
            try {
                if (ds != null) {
                    ds.close();
                    ds = null;
                }               
                if (main_thread != null) {
                    main_thread.stop_thread();
                    main_thread = null;
                }
                if (ssock != null) {
                    ssock.close();
                    ssock = null;
                }
            } catch (IOException e) {
                Log("Exception closing server: " + e + "\n");
            }
            stored_passwd.clear();
            setEditable_jText(true);
            Log("Web server stopped\n");
        }
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    
    /**
     * Exit the Application
     */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm

    
    /**
     * Test the communication with the Authentication server
     * @param evt 
     */
    private void jButtonTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTestActionPerformed
        if (!jToggleButton1.isSelected()) {
            Log("Server is not active\n");
            return;
        }
        try {
            boolean ok= validate_cached_passwd(this.jTextUser.getText(),
                this.jTextPass.getText());
            Log("Validate password result= "+ok+"\n");
        }
        catch(Exception e) {
            Log("Error testing AuthServer: "+e+"\n");
        }
        
    }//GEN-LAST:event_jButtonTestActionPerformed

    private void jTextRaizHtmlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextRaizHtmlActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextRaizHtmlActionPerformed

    
    /**
     * Authentication server's IP address
     * @return IP address
     */
    public InetAddress get_AuthServer_IP() {
        String str= jTextAuthIP.getText();
        try {
            return InetAddress.getByName(str);
        } catch (UnknownHostException e) {
            Log("Invalid AuthServer IP address '"+str+"': "+e+"\n");
            return null;
        }
    }

    
    /**
     * Authentication server's port number
     * @return port number
     */
    public int get_AuthServer_Port() {
        String str= this.jTextAuthPort.getText();
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            Log("Invalid AuthServer port number '"+str+"': "+e+"\n");
            return 0;
        }
    }
     
    
    /**
     * Controls editability of jTexts
     *
     * @param editable
     */
    public void setEditable_jText(boolean editable) {
        jTextLocalPort.setEditable(editable);
        jTextRaizHtml.setEditable(editable);
    }

    
    /**
     * Logs a message on the command line and on the text area
     *
     * @param s string to be written
     */
    public void Log(String s) {
        jTextArea1.append(s);
        System.out.print(s);
    }

    
    /**
     * Returns the port number in a JTextField
     */
    private int getPort(JTextField jt) {
        try {
            int port = Integer.parseInt(jt.getText());
            if ((port <= 0) || (port > 65535)) {
                return 0;
            }
            return port;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    
    /**
     * Returns the local TCP port number
     *
     * @return local port number
     */
    public int getLocalPort() {
        return getPort(jTextLocalPort);
    }

    
    /**
     * Returns the Proxy's UDP port number
     *
     * @return proxy port number
     */
    public int getProxyPort() {
        return getPort(jTextAuthPort);
    }

    
    /**
     * Returns the root html directory
     *
     * @return string with the root pathname
     */
    public String getRaizHtml() {
        return jTextRaizHtml.getText();
    }
 
    
    /**
     * Increments the AUTH_REQ sequence number and returns it
     * 
     * @return sequence number
     */
    public int get_NextSEQ() {
        return ++AUTH_Req_seq;
    }


    /**
     * Auxiliary function to read a String from a DataInputStream
     * @param dis   DataInputStream associated to a socket or a file
     * @return  String received
     * @throws IOException 
     */
    public static String read_String(DataInputStream dis) throws IOException {
        short len=  dis.readShort();
        if (len > 0) {
            byte[] buf= new byte[len];
            dis.read(buf);
            return new String(buf);
        } else
            return "";
    }
    

    /**
     * Send a AUTH_REQ message to the Authentication server
     * @param type      Type requested for AUTH_REP
     * @param servIP    Authentication server's IP address
     * @param servPort  Authentication server's port number
     * @param number    Student's number
     * @param user      Username
     * @param pass      Password
     * @param seq       Unique sequence number
     * @return          true is user+password are valid, false otherwise
     */
    private boolean send_AUTH_REQ(byte type, InetAddress servIP, int servPort, int number,
            String user, String pass, int seq) {
        
        // Create and send packet
        ByteArrayOutputStream os = new ByteArrayOutputStream(); // Prepares a message 
        DataOutputStream dos = new DataOutputStream(os);        //   writting object
        try {
            //Protocolo3
            /********************************
             * Lab work 4 - TASK 2: 
             *      Write all the fields of AUTH_REQ according to your type number
             *          using the dos object
             */
            
            /*
            byte type=3;
            int number;
            short len_user;
            byte[] user;
            short len_pass;
            byte[] pass;
            int seq;
            */
            dos.writeByte(type);
            dos.writeInt(number);
            dos.writeShort(user.length());
            dos.writeBytes(user);
            dos.writeShort(pass.length());
            dos.writeBytes(pass);       
            dos.writeInt(seq); //tipo p=3

            // Prepare message
            byte[] buffer = os.toByteArray();       // Convert to byte array   
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length, servIP, servPort); // Create packet
            ds.send(dp);    // Send the datagram packet
                     
            return true;
        } catch (Exception e) {
            // Catches all exceptions
            Log("Error preparing or sending AUTH_REQ message: "+e+"\n");
            return false;
        }
    }

    
    /**
     * Function that sends AUTH_REQ and receives AUTH_REP messages
     * @param user  Username
     * @param pass  Password 
     * @return true if the password is validated
     */
    public boolean validate_passwd(String user, String pass) {
        if ((user == null) || (pass == null)) {
            Log("null url, user or pass in validate_passwd\n");
            return false;
        }
        
        byte[] buf=new byte[ServHttpd.MAX_PACKET_LENGTH];
        DatagramPacket rdp= new DatagramPacket(buf,buf.length);
        // Parameters received in AUTH_REP
        byte type, rtype;
        
        int rnumber= 0;
        int seq, seqcheck= 0;
        String ruser= "";
        boolean raccepted= false;
        
        seq= get_NextSEQ();
        try {
            type= Byte.parseByte(this.jTextType.getText());
        } catch (NumberFormatException e) {
            Log("Invalid Type number '"+this.jTextType.getText()+"': "+e+"\n");
            return false;
        }

        try {
            send_AUTH_REQ(type, get_AuthServer_IP(), 
                    get_AuthServer_Port(), number, user, pass, seq);

            
            Log("The web server will block in ServHttpd.validate_password if the"
                    + " authentication server does not answer to the AUTH_REQ with an AUTH_REP\n");
            /********************************
             * Lab work 4 - TASK 1: 
             *      Place code after the creation of the datagram socket (ds)
             *      in function jToggleButton1ActionPerformed
             *      to define a maximum waiting time in the 
             *          read operation that follows this line
             * Suggestion: Read section 5.2.3 of the tutorial!
             */
            // ...

            ds.receive(rdp);
            ByteArrayInputStream BAis =
                    new ByteArrayInputStream(buf, 0, rdp.getLength());
            DataInputStream dis = new DataInputStream(BAis);
            
            // Parse AUTH_REP message
            rtype= dis.readByte();  // Read the type field
           
            /********************************
             * Lab work 4 - TASK 3: 
             *      Read all the fields of AUTH_REP according to your type number
             *          using the dis object to the local variables declared above
             *      Suggestion: understand how read_String function can be used
             */
            // ...
            
            /*
            byte type=3;
            boolean accepted;
            int seqcheck;
            short len_user;
            byte[] user;
            int number; 
            */
            
            raccepted = dis.readBoolean();
            seqcheck = dis.readInt();
            ruser = read_String(dis);
            rnumber = dis.readInt();
            
            Log("Received AUTH_REP ("+raccepted+";" +seqcheck+";t="+rtype+";"+ruser+") from "+rnumber+" at "+
                    rdp.getAddress().getHostAddress()+":"+rdp.getPort()+"\n");
            
            /********************************
             * Lab work 4 - TASK 3: 
             *      Test the values received
             */
            
            if (user.equals(ruser)) {
                if (seqcheck == (seq + 6)) {
                    
                    return raccepted;
                }
                else{
                    Log("Valor seqcheck incorreto!\n");
                    return false;
                }
            }
            else{
                Log("Users nao coincidem\n");
                return false;
            }
        } catch (SocketTimeoutException ex) {
            // No answer from authentication server
            Log("Timeout waiting for AUTH_REP\n");
            return false;
        } catch (IOException ex) {
            // Error in reading from buffer - packet too short
            Log("Error in communication with authentication server:\n "+ex+"\n");
            return false;
        } catch (Exception ex) {
            // Error in communication
            Log("Exception in function validate_passwd:\n "+ex+"\n");
            return false;
        }  
    }
    
    
    /**
     * Function that sends validates and manages the passwords' cache
     * @param user  Username
     * @param pass  Password 
     * @return 
     */
    public boolean validate_cached_passwd(String user, String pass) {
        
        if ((user == null) || (pass==null)) {
            Log("null user or pass in validate_cached_passwd");
            return false;
        }
        /********************************
         * Lab work 5 - TASK 3: 
         *      Program here the algorithm to test if user+pass is stored in the cache,
         *          and if it was stored for less than the validity assign to your group.
         *      If it isn't or the value in cache is too old, use the
         *          function validate_passwd to test the password.
         *      Store the password in cache, if it is valid.
         * 
         *      Suggestion: You should declare and use a variable HashMap<String,Date> cache; 
         *          to store the saved passwords, where the key should be the string with
         *          user:pass
         */
        String key = user+":"+pass;
        Date Date= new Date();
        long saved, now;
        
        if(stored_passwd.containsKey(key)){
            saved = stored_passwd.get(key).getTime();
            now = Date.getTime();
            long elapsed = now - saved;
            if (elapsed <= 2000){  /*P3*/
                return true;
            }
            else{
                stored_passwd.remove(key);
                if (validate_passwd(user, pass)==true){
                    stored_passwd.put(key,Date);
                    return true; 
                }
            }
        }
        else{
            if (validate_passwd(user, pass)==true){
                stored_passwd.put(key,Date);
                return true;    
            }
        }
        
        
        // ...
        return validate_passwd(user, pass);
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        byte type= 1;
        if (args.length > 0) {
            try {
                type= Byte.parseByte(args[0]);
            } catch (NumberFormatException e) {
                type= 1;
            }
        }
        new ServHttpd(type).setVisible(true);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonTest;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextAuthIP;
    private javax.swing.JTextField jTextAuthPort;
    private javax.swing.JTextField jTextLocalIP;
    private javax.swing.JTextField jTextLocalPort;
    private javax.swing.JTextField jTextPass;
    private javax.swing.JTextField jTextRaizHtml;
    private javax.swing.JTextField jTextType;
    private javax.swing.JTextField jTextUser;
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration//GEN-END:variables
}