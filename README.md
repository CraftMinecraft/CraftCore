CraftSuite
==========

The server-to-server bridging plugin suite. 

## Networking Protocole Specification ##
### Permission Sets ###
There are several "permission sets" :  
* Unauthenticated : servers which aren't registered by the CMC authority. They can still participate as "watchers" E.G. reading the messages, but any message they send to the network will be ignored.
* Verified : Verified servers have a certificate issued from the CMC authority. They can send and recieve certain "safe" messages (player logged-in). Any server proven to be creating trouble to the network will see their certificate revoked and an immediate ban from the network.
* Paired : Paired servers are server that has authenticated eachother without going through the CMC authority. It is up to the server owners to decide which information transit between the servers. For instance, they may only enable ban sharing, or share the economy entirely. Paired server also syncs eachother based on timestamp, because of the lack of a masterserver. When a server goes offline, the other sever will cache the information to send to the server when it goes back up, coupled with a timestamp. It is up to the server owner to decide which "features" are to talk through this shared server.
* CMC Network : CMC Network : full permissions between eachother.

### Messaging System ###
The server sends a json object that can be read to any valid JSON parser. The parser used by CraftSuite is json-simple. The JSON object is like so :   
{plugin: "nameofyourplug", opcode: "nameoffunction", arg1name: "arg1content",...}   
So for instance, the "PlayerJoined" object would look like :    
{plugin: "craftsuite", opcode: "playerJoined", player: "roblabla"}   

### Core Packets ###
Those are the packets that are used for server-server auth. Client is the connecting server and Server is the server the client connects to.

* Core/connect : First packet sent when a server connects to another.    
{plugin: "Core", opcode: "connect", settings: {name: "nameofserver", mode: "public", listening:["craftsuite/playerjoined", "nameoftheplug/nameofffunction"]}}   
listening can contain wildcards, such as "craftsuite/\*" or "\*/playerjoined".   
Direction : client -> server

* Core/listOfServers : Show the servers connected and in public mode.   
{plugin: "Core", opcode: "listOfServers", servers: [{name: "name", host: "hostname"}]}   
Direction : bidirectional.   

* Core/changeSettings : Changes the settings.   
{plugin: "Core", opcode: "changeServers", listening: [], name: "better"}    

## How to use the plugin ##
### Sending messages ###
Sample code :    
        ServerManager netman = Bukkit.getServer.getPluginManager().getPlugin("CraftSuite").getCore().getManage(plugin);
        netman.send("playerJoined", "roblabla");

I know this line looks extremely bloated, I am going to make the ServerManager either static or a Singleton to make it easier to access it.

## Default plugins included ##
By default, the suite comes with several included features :   
* A global warning/banning system
* A global friends list system
* More to come.

### Global Warning/banning System ###
#### Packets ####
{plugin: "CraftWarns", opcode: "warn", player: "roblabla", reason: "being awesome", warner: "fireball1726"}   
{plugin: "CraftWarns", opcode: "ban", player: "FireBall1725", reason: "being TOO awesome", banner: "roblabla"}   
{plugin: "CraftWarns", opcode: "unban", player: "FireBall1725", unbanner: "someone"}   
{plugin: "CraftWarns", opcode: "unwarn", player: "roblabla", warningId: 2, unwarner: "roblabla"}   

#### Details ####
Note that the bans are NOT shared accross the network by default, for fairly obvious reasons. They will only show up to admin when a player joins. It is possible to make a player with too many bans/warns to be locked off.

### Global Friends List/Messaging System ###
#### Packets ####
{plugin: "CraftFriends", opcode: "addFriend", source: "roblabla", target: "fireball1725"} : Allows roblabla to recieve messages from fireball1725, from whatever server. NOTE THAT THIS DOES NOT ALLOW ROBLABLA TO SEND MESSAGES TO FIREBALL.   
{plugin: "CraftFriends", opcode: "remFriend", source: "roblabla", target: "fireball1725"} : Removes fireball from the friend list.   
{plugin: "CraftFriends", opcode: "sendMsg", source: "roblabla", target: "fireball1725"} : sends a message to fireball1725. NOTE THAT FOR SECURITY, ANY 3RD-PARTY PLUGINS SHOULD FIRST CHECK IF FIREBALL IS INDEED FRIENDS WITH ROBLABLA.   

#### Details ####
