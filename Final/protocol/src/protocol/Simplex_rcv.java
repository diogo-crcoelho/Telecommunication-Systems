/*
 * Sistemas de Telecomunicacoes 
 *          2020/2021
 */
package protocol;

import terminal.Simulator;
import simulator.Frame;
import terminal.NetworkLayer;

/**
 * Protocol 2 : Simplex Receiver protocol which does not transmit frames
 *
 * @author 53287 (Put here your student number)
 */
public class Simplex_rcv extends Base_Protocol implements Callbacks {

    public Simplex_rcv(Simulator _sim, NetworkLayer _net) {
        super(_sim, _net);      // Calls the constructor of Base_Protocol
        frame_expected = 0;
        ack = 0;
    }

    /**
     * CALLBACK FUNCTION: handle the beginning of the simulation event
     *
     * @param time current simulation time
     */
    @Override
    public void start_simulation(long time) {
        sim.Log("\nSimplex Receiver Protocol\n\tOnly receive data!\n\n");

    }

    @Override
    public void handle_Data_Timer(long time) {
        sim.Log(time + " Data Timeout not expected\n");
    }

    /**
     * CALLBACK FUNCTION: handle the ack timer event; ignores it in this case
     *
     * @param time current simulation time
     */
    @Override
    public void handle_ack_Timer(long time) {
        sim.Log(time + " ACK Timeout not expected\n");
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
        if (frame.kind() == Frame.DATA_FRAME) {     // Check the frame kind
            if (frame.seq() == frame_expected) {    // Check the sequence number
                if (net.to_network_layer(frame.info())) {
                    frame_expected = next_seq(frame_expected);
                    send_ack();
                }
            } else {

                ack = prev_seq(ack);
                send_ack();
            }

        }
    }

    private boolean send_ack() {   // ... to Physical Layer
        Frame frame = Frame.new_Ack_Frame(ack, null);
        sim.to_physical_layer(frame);
        ack = next_seq(ack);
        return true;
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

    /* Variables */
    /**
     * Reference to the simulator (Terminal), to get the configuration and send
     * commands
     */
    //final Simulator sim;  -  Inherited from Base_Protocol
    /**
     * Reference to the network layer, to send a receive packets
     */
    //final NetworkLayer net;    -  Inherited from Base_Protocol
    /**
     * Expected sequence number of the next data frame received
     */
    private int frame_expected;
    private int ack;

}
