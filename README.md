# The Go-Back-N Protocol
### About
This repository contains client and server programs to transfer a text file between themselves across an unreliable network. They are written entirely in Java. 


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
./server.sh <req_code>
./client.sh <server_address> <n_port> <req_code> '<msg>'
```

### License
This repository is licensed under the [MIT license](https://github.com/elailai94/The-Go-Back-N-Protocol/blob/master/LICENSE.md).
