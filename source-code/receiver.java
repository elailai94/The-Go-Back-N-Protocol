//==============================================================================
// The Go-Back-N Protocol
//
// @description: An implementation of a receiver program in Java
// @author: Elisha Lai
// @version: 1.0 01/11/2016
//==============================================================================

import java.io.FileWriter;
import java.io.BufferedWriter;

import java.net.DatagramSocket;

public class receiver {
   public static void main(String[] args) throws Exception {
      // Checks the number and formats of the command line arguments passed
      checkCommandLineArguments(args);
 
      String emulatorAddress = args[0];
      int emulatorPort = Integer.parseInt(args[1]);
      int receiverPort = Integer.parseInt(args[2]);
      String fileName = args[3];

      // Stores the expected sequence number of the next packet to be
      // received
      int expectedSeqNum = 0;
      
      // Creates the file writer and arrival log writer
      BufferedWriter fileWriter =
         new BufferedWriter(new FileWriter(fileName));
      BufferedWriter arrivalLogWriter =
         new BufferedWriter(new FileWriter("arrival.log"));

      // Creates the receiver socket
      DatagramSocket receiverSocket = new DatagramSocket(receiverPort);

      // Receives packets from the emulator until an EOT packet is received
      while (true) {
         // Creates a packet to receive data from the emulator and reads into 
         // it from the receiver socket
         packet packetFromEmulator = packet.receiveFrom(receiverSocket);
         
         // Reads in the sequence number of the received packet
         int seqNum = packetFromEmulator.getSeqNum();
         
         if (!packetFromEmulator.isEOT()) { // Received a data packet?
            // Writes the sequence number of the received packet to the arrival
            // log
            arrivalLogWriter.write(String.valueOf(seqNum));
            arrivalLogWriter.newLine();
         } // if

         if (seqNum == expectedSeqNum) { // Expecting the sequence number of 
                                         // the received packet?
            
            if (packetFromEmulator.isEOT()) { // Received an EOT packet?
               // Creates an EOT packet with the same sequence number as the
               // received packet to send to the emulator and writes it out
               // to the receiver socket
               packet packetToEmulator = packet.createEOT(expectedSeqNum);
               packetToEmulator.sendTo(emulatorAddress, emulatorPort, receiverSocket);
               break;
            } else {
               // Reads in the data of the received packet and writes it to
               // the file
               String data = new String(packetFromEmulator.getData());
               fileWriter.write(data);

               // Creates an ACK packet with the same sequence number as the
               // received packet to send to the emulator and writes it out
               // to the receiver socket
               packet packetToEmulator = packet.createACK(expectedSeqNum);
               packetToEmulator.sendTo(emulatorAddress, emulatorPort, receiverSocket);

               // Computes the expected sequence number of the next packet
               // to be received
               expectedSeqNum = (expectedSeqNum + 1) % packet.SEQ_NUM_MODULO;
            } // if
         
         } else {
            // Computes the sequence number of the most recently received
            // in-order packet
            int lastSeqNum = (expectedSeqNum - 1) % packet.SEQ_NUM_MODULO;
            
            if (lastSeqNum < 0) { // Got a negative modulus?
               // Computes a positive modulus
               lastSeqNum = lastSeqNum + packet.SEQ_NUM_MODULO;
            } // if
            
            // Creates an ACK packet with the same sequence number as the most
            // recently received in-order packet to send to the emulator and
            // writes it out to the receiver socket
            packet packetToEmulator = packet.createACK(lastSeqNum);
            packetToEmulator.sendTo(emulatorAddress, emulatorPort, receiverSocket);
         } // if
      } // while

      // Closes the receiver socket
      receiverSocket.close();
      
      // Closes the arrival log writer and file writer
      arrivalLogWriter.close();
      fileWriter.close();
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
         System.out.println("ERROR: Expecting a receiver port which is" +
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
}
