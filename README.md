# The Go-Back-N Protocol
### About
This repository contains sender and receiver programs to transfer a text file between themselves across an unreliable network using the Go-Back-N protocol. They are written entirely in Java. The protocol will be able to handle network errors such as packet loss and duplicate packets. For simplicity, the protocol is unidirectional, i.e.: data will flow in one direction (from the sender to the receiver) and the acknowledgements (ACKs) in the opposite direction. The network link will be emulated using a network emulator program. When the sender needs to send packets to the receiver, it sends them to the network emulator instead of sending them directly to the receiver. The network emulator then forwards the received packets to the receiver. However, it may randomly discard and/or delay received packets. The same scenario happens when the receiver sends ACKs to the sender.

### How Does It Work?
![the_go_back_n_protocol_diagram](https://cloud.githubusercontent.com/assets/7763904/23889873/942b2a88-0865-11e7-8b7d-0be4360198dc.jpg)

### Compilation
```Bash
make
```

### Clean Build
```Bash
make clean
```

### Execution
```Bash
./nEmulator <emulator_port_forward_direction> <receiver_address> <receiver_port> <emulator_port_backward_direction> <sender_address> <sender_port> <maximum_delay> <discard_probability> <verbose_mode>
java receiver <emulator_address> <emulator_port> <receiver_port> <file_name>
java sender <emulator_address> <emulator_port> <sender_port> <file_name>
```

### License
This repository is licensed under the [MIT license](https://github.com/elailai94/The-Go-Back-N-Protocol/blob/master/LICENSE.md).
