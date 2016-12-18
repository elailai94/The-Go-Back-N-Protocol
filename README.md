# The Go-Back-N Protocol
### About
This repository contains sender and receiver programs to transfer a text file between themselves across an unreliable network. They are written entirely in Java. 


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
