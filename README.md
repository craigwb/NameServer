# NameServer

Craig Butler
Chris Bywaletz
Adrian Popescu

This project was done in its entirety by:

- Craig Butler
- Chris Bywaletz
- Adrian Popescu

We hereby state that we have not received unauthorized help of any form.

Compiliation and Run Instructions:
- javac NameServer.java
- java NameServer BootstrapConfig.txt
- java NameServer NameServerConfig.txt


Description of function and assumptions:
- Namservers store this range of keys inclusivles [Predescor ID, ID of Nameserver - 1]
- The bootserver will always store key 1023.
- The bootserver must know every nameserver in the system due to the enter command and config file format for non bootserver nameservers.
- All lookups, inserts, deletes, enters, and exits facilitate the bootserver being used to communicate with other nameservers.
- All lookups are done by mathmatically determining the location of the key given the IDs of the nameserver that are currently entered in the system, and contacting that server for they key, then returning the key to the bootstrap server.
- All functions of the name server employ the the same functionality of lookup as stated immediately above.
