//==============================================================================
// The Go-Back-N Protocol
//
// @description: Module for providing functions to work with timeouttask objects
// @author: Elisha Lai
// @version: 1.0 01/11/2016
//==============================================================================

import java.util.TimerTask;
import java.util.LinkedList;

import java.net.DatagramSocket;

import java.io.BufferedWriter;

public class timeouttask extends TimerTask {
   private LinkedList<packet> unACKedPacketsSent;
   private String emulatorAddress;
   private int emulatorPort;
   private DatagramSocket senderSocket;
   private BufferedWriter seqNumLogWriter;

   public timeouttask(LinkedList<packet> unACKedPacketsSent,
      String emulatorAddress, int emulatorPort, DatagramSocket senderSocket,
      BufferedWriter seqNumLogWriter) {
      this.unACKedPacketsSent = unACKedPacketsSent;
      this.emulatorAddress = emulatorAddress;
      this.emulatorPort = emulatorPort;
      this.senderSocket = senderSocket;
      this.seqNumLogWriter = seqNumLogWriter;
   } // Constructor

   public timeouttask(timeouttask otherTimeOutTask) {
      unACKedPacketsSent = otherTimeOutTask.unACKedPacketsSent;
      emulatorAddress = otherTimeOutTask.emulatorAddress;
      emulatorPort = otherTimeOutTask.emulatorPort;
      senderSocket = otherTimeOutTask.senderSocket;
      seqNumLogWriter = otherTimeOutTask.seqNumLogWriter;
   } // Copy constructor

   // Runs the task if a timeout occurs
   public void run() {
      try {
   	 // Resends all packets that have been previously sent but that have
   	 // not yet been acknowledged 
   	 for (packet packetToEmulator : unACKedPacketsSent) {
            // Writes the packet to send to the emulator out to the sender socket
            packetToEmulator.sendTo(emulatorAddress, emulatorPort, senderSocket);
            
            // Reads in the sequence number of the sent packet
            int seqNum = packetToEmulator.getSeqNum();
            
            if (!packetToEmulator.isEOT()) { // Sent a data packet?
               // Writes the sequence number of the sent packet to the sequence
               // number log
               seqNumLogWriter.write(String.valueOf(seqNum));
               seqNumLogWriter.newLine();
            } // if
         } // for
      } catch (Exception e) {
      	 System.out.println("ERROR: " + e.toString());
      	 System.exit(-1);
      } // try
   } // run
}
