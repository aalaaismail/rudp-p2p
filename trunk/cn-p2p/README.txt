P2P Downloading Software README v1.0
====================================

INSTALLING, RUNNING
-------------------

- Extract the provided .tgz file to any destination
- for every client to start on the same machine create/edit
  a property file (samples found in ~/resources) and change
  BasePort, DownloadFolder, SharedFolder according to your needs
- TODO: there is still a problem on how to obtain the property file.
  Till' now it is impossible to get it from a relative path.
  Therefore you have to modify src/at/cn/p2p/Util.java => change 
  line 43 to the actual, absolute directory of the resources dir.
- Start the client by either using the command line:
  	~/> java -cp classes;lib/commons-logging-1.1.jar;lib/commons-lang-2.2.jar 
  		at.cn.p2p.TextGui <property-file> <uri>
  or use the provided ant buid file; 3 targets are defined:
  - ant gui1	(which uses property file resources/p2p_1.properties and uri p2p://127.0.0.1:2323)
  - ant gui2	(which uses property file resources/p2p_2.properties and uri p2p://127.0.0.1:2323)
  - ant gui3	(which uses property file resources/p2p_3.properties and uri p2p://127.0.0.1:2323)  
- The second argument (<uri>) needed by by TextGui is a URI of a contact node of an already
  established p2p network. If there is no node yet or you want to build a new network, then
  the URI must to point to itself.
- cn-p2p uri syntax: p2p://x.x.x.x:port/ where 
	x.x.x.x is a valid ip address and 
	port must be the BasePort defined in the property file on that host.


Software Architecture
---------------------

TODO
  