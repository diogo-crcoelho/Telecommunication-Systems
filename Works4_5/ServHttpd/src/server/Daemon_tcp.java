/**
 * Sistemas de Telecomunicacoes 
 *          2020/2021
 *
 * Daemon_tcp.java
 *
 * Thread class that handles accepting a new connection on the server socket
 *
 * @author Luis Bernardo / Paulo Pinto
 */
package server;

import java.net.ServerSocket;
import java.net.Socket;

class Daemon_tcp extends Thread {
    /**
     * GUI object
     */
    ServHttpd root;
    /**
     * Server socket object
     */
    ServerSocket ss;
    /**
     * Active flag
     */
    volatile boolean active;

    /**
     * Constructor
     * @param root  ServHttpd object with the GUI
     * @param ss    Server socker where connections are received
     */
    Daemon_tcp(ServHttpd root, ServerSocket ss) {
        this.root = root;
        this.ss = ss;
    }

    /**
     * Interrupt the thread
     */
    public void wake_up() {
        this.interrupt();
    }

    /**
     * Stop the thread
     */
    public void stop_thread() {
        active = false;
        this.interrupt();
    }

    @Override
    public void run() {
        System.out.println(
                "\n******************** " + ServHttpd.SERVER_NAME + " started ********************\n");
        active = true;
        while (active) {
            try {
                Socket s= ss.accept();      // Receives a new web browser connection 
                // Starts a thread to handle the communication with the browser
                SHttpThread conn= new SHttpThread(root, ss, s); 
                conn.start();
            } catch (java.io.IOException e) {
                if (active)
                    root.Log("IO exception: " + e + "\n");
                active = false;
            }
        }
    }
} // end of class Daemon_tcp
