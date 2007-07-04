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

Feautures:
- distributed tcp downloads
- peer-to-peer filetransfer program
- distributed file search from different peers
- distributed downloading from different peers (same file)
- pause and resume started downloads

Architecture:

To provide functionality for peer availbility, searching and downloading three 
coresponding tcp clients and servers are used (at.cn.p2p.server.* and at.cn.p2p.client.*).
Every server listens on its own port for incoming requests:
- availability port  = BasePort+0
- file search port   = BasePort+1
- file download port = BasePort+2

at.cn.p2p.support.* provides support classes for both server and client, they all have
inner datastructures (with proper access methods):
- Hostlist: a Set of URIs, that define known peers.
- SearchResult: Map (Key=File,Value=A Set of URIs where the searched file was found)
- SharedFiles: indexes the SharedFolder (List of Files) and a Size Map of the Files;
	also provides the "local" search method
- Download is a POJO Bean that stores download infos such as the remoteFile, the downloaded
	localFile, a list of URIs of where to download the file and a bytream array with the actual
	content of the file.
	
The main part of the application - where all strings come together - is at.cn.p2p.TextGui
and at.cn.p2p.Application. TextGui provides a simple TextGui and primarly delegates the user 
inputes to Application.