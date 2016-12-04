##==============================================================================
## CS456 Assignment 02
##
## @description: README file for Assignment 02
## @author: Ah Hoe Lai
## @userid: ahlai
## @version: 1.0 01/11/2016
##==============================================================================

General Information

- How To Compile And Run The Programs On Three Different Machines?
1. Open up three terminal windows
2. In one terminal window, login to another machine remotely using the ssh 
   command
3. In the same terminal window, grant file execution permissions for the
   network emulator program by running:
   chmod +x nEmulator
4. In the same terminal window, execute the network emulator program by
   running:
   ./nEmulator <emulator_port_forward_direction> <receiver_address>
   <receiver_port> <emulator_port_backward_direction> <sender_address>
   <sender_port> <maximum_delay> <discard_probability> <verbose_mode>
   where <emulator_port_forward_direction> is the UDP port number used by the
                                           network emulator to receive data
                                           from the sender,
         <receiver_address> is the host address (or an IP address as an
                               alternative to host address) of the receiver,
         <receiver_port> is the UDP port number used by the receiver to
                            receive data from the network emulator,
         <emulator_port_backward_direction> is the UDP port number used by the
                                               network emulator to receive ACKs
                                               from the receiver,
         <sender_address> is the host address (or an IP address as an
                             alternative to host address) of the sender,
         <sender_port> is the UDP port number used by the sender to receive
                          ACKs from the network emulator,
         <maximum_delay> is the maximum delay of the network in units of
                            millisecond,
         <discard_probability> is the probability that a packet going through
                                  the network emulator will be discarded,
         <verbose_mode> is the boolean value (0 or 1) indicating whether the
                           network emulator will output its internal processing
5. In another terminal window, login to another machine remotely using the ssh
   command
6. In the same terminal window, compile the receiver program by running:
   make
7. In the same terminal window, execute the receiver program by running:
   java receiver <emulator_address> <emulator_port> <receiver_port> <file_name>
   where <emulator_address> is the host address (or an IP address as an
                               alternative to host address) of the network
                               emulator,
         <emulator_port> is the UDP port number used by the network emulator
                            to receive ACKs from the receiver,
         <receiver_port> is the UDP port number used by the receiver to receive
                            data from the network emulator,
         <file_name> is the name of the file into which the received data is
                        written
8. In another terminal window, login to another machine remotely using the ssh
   command
9. In the same terminal window, compile the sender program by running:
   make
10. In the same terminal window, execute the sender program by running:
    java sender <emulator_address> <emulator_port> <sender_port> <file_name>
    where <emulator_address> is the host address (or an IP address as an 
                                alternative to host address) of the network
                                emulator,
          <emulator_port> is the UDP port number used by the network emulator
                             to receive data from the sender,
          <sender_port> is the UDP port number used by the sender to receive
                           ACKs from the network emulator,
          <file_name> is the name of the file to be transferred
11. Inspect the three different log files. The seqnum.log will contain the
    sequence number of all packets sent by the sender. The ack.log will
    contain the sequence number of all packets received by the sender. The
    arrival.log will contain the sequence number of all packets received
    by the receiver.
12. Stop the execution of the network emulator program at any time by hitting
    Ctrl and C on the keyboard

- What Are The Executables And Parameters?
The executable for the sender program is sender that has the parameters
<emulator_address> <emulator_port> <sender_port> <file_name>
where <emulator_address> is the host address (or an IP address as an 
                            alternative to host address) of the network
                            emulator,
      <emulator_port> is the UDP port number used by the network emulator
                         to receive data from the sender,
      <sender_port> is the UDP port number used by the sender to receive
                       ACKs from the network emulator,
      <file_name> is the name of the file to be transferred.

The executable for the receiver program is receiver that has the parameters
<emulator_address> <emulator_port> <receiver_port> <file_name>
where <emulator_address> is the host address (or an IP address as an
                            alternative to host address) of the network
                            emulator,
      <emulator_port> is the UDP port number used by the network emulator
                         to receive ACKs from the receiver,
      <receiver_port> is the UDP port number used by the receiver to receive
                         data from the network emulator,
      <file_name> is the name of the file into which the received data is
                     written.

The executable for the network emulator program is nEmulator that has the
parameters
<emulator_port_forward_direction> <receiver_address> <receiver_port>
<emulator_port_backward_direction> <sender_address> <sender_port>
<maximum_delay> <discard_probability> <verbose_mode>
where <emulator_port_forward_direction> is the UDP port number used by the
                                           network emulator to receive data
                                           from the sender,
      <receiver_address> is the host address (or an IP address as an
                            alternative to host address) of the receiver,
      <receiver_port> is the UDP port number used by the receiver to receive
                         data from the network emulator,
      <emulator_port_backward_direction> is the UDP port number used by the
                                            network emulator to receive ACKs
                                            from the receiver,
      <sender_address> is the host address (or an IP address as an alternative
                          to host address) of the sender,
      <sender_port> is the UDP port number used by the sender to receive ACKs
                       from the network emulator,
      <maximum_delay> is the maximum delay of the network in units of millisecond,
      <discard_probability> is the probability that a packet going through the
                               network emulator will be discarded,
      <verbose_mode> is the boolean value (0 or 1) indicating whether the
                        network emulator will output its internal processing.

- How To Clean Build?
You can clean build by running: make clean

Technical Information

- Which Undergrad Machines Were Used To Built And Test The Programs?
   ubuntu1404-006.student.cs.uwaterloo.ca (ran the network emulator program)
   ubuntu1404-008.student.cs.uwaterloo.ca (ran the receiver program)
   ubuntu1404-004.student.cs.uwaterloo.ca (ran the sender program)

- What Is The Version Of make Used For The Makefile?
The version of make that used for the Makefile is 3.81.

- What Is The Version Of javac Used To Compile The Programs?
The version of javac used to compile the programs is 1.8.0_91.
