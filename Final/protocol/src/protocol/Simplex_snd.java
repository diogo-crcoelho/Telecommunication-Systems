/*
 * Sistemas de Telecomunicacoes 
 *          2020/2021
 */
package protocol;

import com.sun.xml.internal.ws.api.Cancelable;
import terminal.Simulator;
import simulator.Frame;
import terminal.NetworkLayer;

/**
 * Protocol 2 : Simplex Sender protocol which does not receive frames
 * 
 * @author 53287 (Put here your student number)
 */
public class Simplex_snd extends Base_Protocol implements Callbacks {

    public Simplex_snd(Simulator _sim, NetworkLayer _net) {
        super(_sim, _net);      // Calls the constructor of Base_Protocol
        next_frame_to_send = 0;
        ack_expected=0;
        // ...
    }

    private boolean send_next_data_packet() {
        // We can only send one Data packet at a time
        //   you must wait for the DATA_END event before transmitting another one
        //   otherwise the first packet is lost in the channel
        String packet;
        if (repeat == 0){
             packet = net.from_network_layer();
             temp = packet;
             
            }
        else {
            packet = temp;
            repeat = 0;         
        }
        if (packet != null) {
            // The ACK field of the DATA frame is always the sequence number before zero, because no packets will be received
            Frame frame = Frame.new_Data_Frame(next_frame_to_send, 
                    prev_seq(0) /* the one before 0 */, 
                    null /* no ackvec is used except for selective_repeat */,
                    packet);
            sim.to_physical_layer(frame);
            return true;
        }
        return false;
        
        
    }
    /**
     * CALLBACK FUNCTION: handle the beginning of the simulation event
     * @param time current simulation time
     */
    @Override
    public void start_simulation(long time) {
        sim.Log("\nSimplex Sender Protocol\n\tOnly send data!\n\n");
        send_next_data_packet();
    }

    /**
     * CALLBACK FUNCTION: handle the end of Data frame transmission, start timer
     * @param time current simulation time
     * @param seq  sequence number of the Data frame transmitted
     */
    @Override
    public void handle_Data_end(long time, int seq) {
        sim.start_data_timer();
    }
    
    /**
     * CALLBACK FUNCTION: handle the data timer event; retransmit failed frames
     * @param time current simulation time
     */
    @Override
    public void handle_Data_Timer(long time) {
        sim.Log(time + " Data Timeout\n"); 
        repeat = 1;
        send_next_data_packet();
        
    }
    
    /**
     * CALLBACK FUNCTION: handle the reception of a frame from the physical layer
     * @param time current simulation time
     * @param frame frame received
     */
    @Override
    public void from_physical_layer(long time, Frame frame) {
        if (frame.kind() == Frame.ACK_FRAME) {// Check the frame kind
            if (frame.ack() == ack_expected) {// Check the ack int number
                ack_expected= next_seq(ack_expected);
                next_frame_to_send = next_seq(next_frame_to_send);
                sim.cancel_data_timer();
                send_next_data_packet();
            }
        }
        
    }

    /**
     * CALLBACK FUNCTION: handle the end of the simulation
     * @param time current simulation time
     */
    @Override
    public void end_simulation(long time) {
        sim.Log("Stopping simulation\n");
    }
    
    
    /* Variables */
    
    /**
     * Reference to the simulator (Terminal), to get the configuration and send commands
     */
    //final Simulator sim;  -  Inherited from Base_Protocol
    
    /**
     * Reference to the network layer, to send a receive packets
     */
    //final NetworkLayer net;    -  Inherited from Base_Protocol
    
    /**
     * Sequence number of the next data frame
     */
    private int next_frame_to_send;
    private String temp;
    private int ack_expected;
    private int repeat = 0;
    /**
     * ??????
     */
    //private ????
}
