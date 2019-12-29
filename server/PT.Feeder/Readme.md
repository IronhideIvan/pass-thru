- Sample command line args to initialize UDP server

```
  -t=vJoy -id="1" -l=Debug -a="127.0.0.1" -p=7084
```

- NOTE: the IP _127.0.0.1_ only works if you are trying to connect locally. If you are trying to establish a connection from another machine, then you have to use the address the client recognizes you as. (Ex. 192.168.0.39)

- When receiving packets from another machine, then make sure that the firewall allows inbound traffic on the designated port.
