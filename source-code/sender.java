//==============================================================================
// The Go-Back-N Protocol
//
// @description: An implementation of a sender program in Java
// @author: Elisha Lai
// @version: 1.0 01/11/2016
//==============================================================================

import java.util.LinkedList;
import java.util.Iterator;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public class sender {
   public static void main(String[] args) throws Exception {
   // Checks the number and formats of the command line arguments passed
   checkCommandLineArguments(args);

   String emulatorAddress = args[0];
   int emulatorPort = Integer.parseInt(args[1]);
   int senderPort = Integer.parseInt(args[2]);
   String fileName = args[3];

   // Stores the sequence number of the packet that has been previously
   // sent but has not yet been acknowledged
   int base = 0;

   // Stores the sequence number of the next packet to be sent
   int nextSeqNum = 0;

   // Stores whether the EOT packet is sent yet 
   boolean isEOTPacketSent = false;

   // Stores all the packets that have been previously sent but that have
   // not yet been acknowledged in a buffer queue
   LinkedList<packet> unACKedPacketsSent = new LinkedList<packet>();

   // Creates the file reader, sequence number log writer and ACK log
   // writer
   BufferedReader fileReader =
      new BufferedReader(new FileReader(fileName));
   BufferedWriter seqNumLogWriter =
      new BufferedWriter(new FileWriter("seqnum.log"));
   BufferedWriter ACKLogWriter =
      new BufferedWriter(new FileWriter("ack.log"));

   // Creates the sender socket
   DatagramSocket senderSocket = new DatagramSocket(senderPort);

   // Creates the task to resend all packets that have been previously
   // sent but have not yet been acknowledged if a timeout occurs, which
   // is attached to the new timer 
   timeouttask timeOutTask = new timeouttask(unACKedPacketsSent,
      emulatorAddress, emulatorPort, senderSocket, seqNumLogWriter);
   timer senderTimer = new timer(timeOutTask);

   // Sends packets to the emulator until an EOT packet is received
   while (true) {
      if (!isWindowFull(base, nextSeqNum) &&
         !isEOTPacketSent) { // Is the window not full and the EOT packet
                             // not sent yet?
         // Stores the characters read in from the file
         char[] charsRead = new char[packet.MAX_DATA_LENGTH];
            
         // Stores the number of characters read in from the file
         int numOfCharsRead =
            fileReader.read(charsRead, 0, packet.MAX_DATA_LENGTH);
            
         // Stores the packet to send to the emulator
         packet packetToEmulator = null;
 
         if (numOfCharsRead == -1) { // Got no more characters to read from
                                     // the file?
            // Creates an EOT packet with the next sequence number to send
            // to the emulator
            packetToEmulator = packet.createEOT(nextSeqNum);
   
            // Indicates that the EOT packet is sent
            isEOTPacketSent = true;
         } else {
            // Creates a data packet with the next sequence number and the
            // characters read in from the file to send to the emulator
            packetToEmulator =
               packet.createData(nextSeqNum, new String(charsRead, 0, numOfCharsRead));
         } // if

         // Adds the packet to send to the emulator to the buffer queue
         // for all the packets that have been previously sent but that
         // have not yet been acknowledged
         unACKedPacketsSent.offer(packetToEmulator);

         // Writes the packet to send to the emulator out to the sender
         // socket
         packetToEmulator.sendTo(emulatorAddress, emulatorPort, senderSocket);

         if (base == nextSeqNum) { // Is this packet the oldest packet
                                   // that has been previously sent but
                                   // has not yet been acknowledged?
            // Starts the sender timer for the oldest packet that has
            // been previously sent but has not yet been acknowledged
            senderTimer.start(); 
         } // if

         if (!packetToEmulator.isEOT()) { // Sent a data packet?
            // Writes the sequence number of the sent packet to the sequence
            // number log
            seqNumLogWriter.write(String.valueOf(nextSeqNum));
            seqNumLogWriter.newLine();
         } // if

         // Computes the sequence number of the next packet to be sent
         nextSeqNum = (nextSeqNum + 1) % packet.SEQ_NUM_MODULO;
      } else {
         // Creates a packet to receive data from the emulator and reads 
         // into it from the sender socket
         packet packetFromEmulator = packet.receiveFrom(senderSocket);

         // Reads in the sequence number of the received packet
         int seqNum = packetFromEmulator.getSeqNum();

         if (!packetFromEmulator.isEOT()) { // Received an ACK packet?
            // Writes the sequence number of the received packet to the ACK
            // log 
            ACKLogWriter.write(String.valueOf(seqNum));
            ACKLogWriter.newLine();
         } // if

         // Computes the sequence number of the packet that has been
         // previously sent but has not yet been acknowledged
         base = (seqNum + 1) % packet.SEQ_NUM_MODULO;

         // Removes all the acknowledged packets sent to the emulator
         // from the buffer queue storing all the packets that has been
         // previously sent but that have not yet been acknowledged
         removeACKedPacketsSent(unACKedPacketsSent, base);

         if (base == nextSeqNum) { // Got no more packets that have been
                                   // previously sent but that have not
                                   // yet been acknowledged?
            // Stops the sender timer since all the packets that have
            // been previously sent have been acknowledged
            senderTimer.stop();
                              
            if (packetFromEmulator.isEOT()) { // Received an EOT packet?
               break;
            } // if
         } else {
            // Restarts the sender timer for the oldest packet that has
            // been previously sent but that has not yet been acknowledged
            senderTimer.restart();
         } // if
      } // if
   } // while

   // Closes the sender socket
   senderSocket.close();
      
   // Closes the ACK log writer, sequence number log writer and file
   // reader
   ACKLogWriter.close();
   seqNumLogWriter.close();
   fileReader.close();
   } // main

   // Checks the number and formats of the command line arguments passed
   private static void checkCommandLineArguments(String[] args) throws Exception {
      if (args.length != 4) {
         System.out.println("ERROR: Expecting 4 command line arguments," +
            " but got " + args.length + " arguments");
         System.exit(-1);
      } // if

      if ((!isValidIPAddress(args[0])) && (!isValidHostName(args[0]))) {
         System.out.println("ERROR: Expecting an emulator address which is" +
            " a valid IP address or host name, but got " + args[0]);
         System.exit(-1);
      } // if

      try {
         int emulatorPort = Integer.parseInt(args[1]);
      } catch (NumberFormatException e) {
         System.out.println("ERROR: Expecting an emulator port which is" +
            " an integer, but got " + args[1]);
         System.exit(-1);
      } // try

      try {
         int senderPort = Integer.parseInt(args[2]);
      } catch (NumberFormatException e) {
         System.out.println("ERROR: Expecting a sender port which is" +
            " an integer, but got " + args[2]);
      } // try
   } // checkCommandLineArguments

   // Checks if a string is a valid IP address, which ranges from 0.0.0.0 to
   // 255.255.255.255
   private static boolean isValidIPAddress(String string) throws Exception {
      String regex = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                     "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                     "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                     "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
      boolean isRegexMatch = string.matches(regex);
      return isRegexMatch;
   } // isValidIPAddress

   // Checks if a string is a valid host name, which complies with RFC 1912
   private static boolean isValidHostName(String string) throws Exception {
      String regex = "^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])" +
                     "(\\.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*$";
      boolean isRegexMatch = string.matches(regex);
      
      boolean isCorrectLength = (string.length() <= 255);
      
      String[] labels = string.split("\\.");
      boolean isLabelsNotAllNumeric = true;
      for (String label : labels) {
         if (label.matches("^[0-9]+$")) {
            isLabelsNotAllNumeric = false;
            break;
         } // if
      } // for

      if (isRegexMatch && isCorrectLength && isLabelsNotAllNumeric) {
         return true;
      } else {
         return false;
      } // if
   } // isValidHostName

   // Checks if the window is full, that is, whether there are ten
   // outstanding, unacknowledged packets
   private static boolean isWindowFull(int base, int nextSeqNum) {
      final int WINDOW_SIZE = 10;
      if ((base + WINDOW_SIZE) >= packet.SEQ_NUM_MODULO) {
         
         if ((nextSeqNum >= base) && (nextSeqNum < packet.SEQ_NUM_MODULO)) {
            return false;
         } else if ((nextSeqNum >= 0) &&
            (nextSeqNum < ((base + WINDOW_SIZE) % packet.SEQ_NUM_MODULO))) {
            return false;
         } else {
            return true;
         } // if
      
      } else {
         
         if ((nextSeqNum >= base) && (nextSeqNum < (base + WINDOW_SIZE))) {
            return false;
         } else {
            return true;
         } // if
      
      } // if
   } // isWindowFull

   // Removes all the acknowledged packets sent to the emulator from the
   // buffer queue storing all the packets that has been previously sent
   // but that have not yet been acknowledged
   private static void removeACKedPacketsSent(LinkedList<packet> unACKedPacketsSent,
      int base) {
      Iterator<packet> it = unACKedPacketsSent.iterator();
      
      while (it.hasNext()) {
         packet packetToEmulator = it.next();
         
         if (packetToEmulator.getSeqNum() == base) {
            break;
         } else {
            it.remove();
         } // if
      } // while
   } // removeACKedPacketsSent
}
