/*
 * Sistemas de Telecomunicacoes 
 *          2020/2021
 */
package protocol;

import terminal.Simulator;
import simulator.Frame;
import terminal.NetworkLayer;
import terminal.Terminal;

/**
 * Protocol 3 : Stop & Wait protocol
 *
 * @author 53287 (Put here your student number)
 */
public class StopWait extends Base_Protocol implements Callbacks {

    public StopWait(Simulator _sim, NetworkLayer _net) {
        super(_sim, _net);      // Calls the constructor of Base_Protocol
        next_frame_to_send = 0;
        frame_expected = 0;
        ack = 0;
        ack_expected = 0;
        // Initialize here all variables
    }

    /**
     * CALLBACK FUNCTION: handle the beginning of the simulation event
     *
     * @param time current simulation time
     */
    @Override
    public void start_simulation(long time) {
        sim.Log("\nStop&Wait Protocol\n\n");
        send_next_data_packet();
    }

    /**
     * CALLBACK FUNCTION: handle the end of Data frame transmission, start timer
     *
     * @param time current simulation time
     * @param seq sequence number of the Data frame transmitted
     */
    @Override
    public void handle_Data_end(long time, int seq) {
        sim.start_data_timer();
    }

    /**
     * CALLBACK FUNCTION: handle the timer event; retransmit failed frames
     *
     * @param time current simulation time
     */
    @Override
    public void handle_Data_Timer(long time) {
        repeat = 1;
        send_next_data_packet();
    }

    /**
     * CALLBACK FUNCTION: handle the ack timer event; send ACK frame
     *
     * @param time current simulation time
     */
    @Override
    public void handle_ack_Timer(long time) {
        send_ack();
    }

    /**
     * CALLBACK FUNCTION: handle the reception of a frame from the physical
     * layer
     *
     * @param time current simulation time
     * @param frame frame received
     */
    @Override
    public void from_physical_layer(long time, Frame frame) {
        if (frame.kind() == Frame.ACK_FRAME) {
            
            if (frame.ack() == ack_expected) {// Check the ack int number
                ack_expected = next_seq(ack_expected);
                next_frame_to_send = next_seq(next_frame_to_send);
                sim.cancel_data_timer();
                send_next_data_packet();
            }
        }
        if (frame.kind() == Frame.DATA_FRAME) {     // Check the frame kind
            if (frame.seq() == frame_expected) {    // Check the sequence number
                if (net.to_network_layer(frame.info())) {
                    frame_expected = next_seq(frame_expected);
                    sim.start_ack_timer();
                    if (frame.ack() == ack_expected) 
                    {
                        ack_expected = next_seq(ack_expected);
                        sim.cancel_data_timer();   
                        next_frame_to_send = next_seq(next_frame_to_send); 
                        send_next_data_packet();
                    }
                }
            } else {

                ack = prev_seq(ack);
                sim.start_ack_timer();
            }

        }

    }

    /**
     * CALLBACK FUNCTION: handle the end of the simulation
     *
     * @param time current simulation time
     */
    @Override
    public void end_simulation(long time) {
        sim.Log("Stopping simulation\n");
    }

    private boolean send_next_data_packet() {
        // We can only send one Data packet at a time
        //   you must wait for the DATA_END event before transmitting another one
        //   otherwise the first packet is lost in the channel

        String packet;
        if (repeat == 0) {

            packet = net.from_network_layer();
            temp = packet;

        } else {

            packet = temp;
            repeat = 0;
        }
        if (packet != null) {
            // The ACK field of the DATA frame is always the sequence number before zero, because no packets will be received
            if (sim.isactive_ack_timer()) {
                Frame frame = Frame.new_Data_Frame(next_frame_to_send, ack, null, packet);
                sim.to_physical_layer(frame);
                sim.cancel_ack_timer();
                ack = next_seq(ack);  
            } else {
                Frame frame = Frame.new_Data_Frame(next_frame_to_send,
                        prev_seq(ack) /* ack*/,
                        null /* no ackvec is used except for selective_repeat */,
                        packet);
                sim.to_physical_layer(frame);
            }
            return true;
        }
        return false;

    }

    private boolean send_ack() {   // ... to Physical Layer
        Frame frame = Frame.new_Ack_Frame(ack, null);
        sim.to_physical_layer(frame);
        ack = next_seq(ack);
        return true;
    }
    /* Variables */

    /**
     * Reference to the simulator (Terminal), to get the configuration and send
     * commands
     */
    //final Simulator sim;  -  Inherited from Base_Protocol
    private int next_frame_to_send;
    private int ack;
    private int frame_expected;
    private int ack_expected;
    private String temp;
    private int repeat = 0;
    /**
     * Reference to the network layer, to send a receive packets
     */
    //final NetworkLayer net;    -  Inherited from Base_Protocol

}
