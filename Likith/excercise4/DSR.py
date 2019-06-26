import socket
import time
import commands
import sys
import random
from datetime import datetime

UDP_IP = "192.168.210.255"

# Routing Table - Initialise all the paths/routes to none 
Path_Dict = {
"192.168.210.152" : None, 
"192.168.210.170" : None, 
"192.168.210.173" : None,
"192.168.210.193" : None, 
"192.168.210.199" : None 
}

# Routing Table - Initialise all the paths/routes to none 
SeqNo_Dict = {
'192.168.210.152' : -1, 
'192.168.210.170' : -1, 
'192.168.210.173' : -1,
'192.168.210.193' : -1, 
'192.168.210.199' : -1 
}

IP = commands.getoutput("ifconfig wlan0 | grep -Po  'inet \K[\d.]+'")
#IP = IP.rstrip()
node = IP[-3:]
UDP_PORT = 5012
print " IP:", IP

# For sending data to all the nodes
IP_List = ["192.168.210.152" , "192.168.210.170", "192.168.210.173","192.168.210.193", "192.168.210.199" ]
    
orig_seqNumber = -1
rcvd_seqNumber = -1
t_end = time.time() + 30
DATA = "2"
RREP = "1"
RREQ = "0"
RList =[]
msg = " "
msg2 = " "

def getRoute(orig_seqNumber, strtxtime, tmpip):
    RREQ_Data = str(orig_seqNumber + ":" + RREQ + ":" + strtxtime + ":" + tmpip + ":" + IP + ":" )
    print ("#################   Start initiation ###############")
    print (str("RREQ forward: "+IP+" : "+tmpip))
    
    #Broadcast RREQ
    sock.sendto(RREQ_Data, (UDP_IP, UDP_PORT))
    
    loopvar = False
    routefound = True
    route = None
    
    while loopvar is False:
        try:
            routedata, addr = sock.recvfrom(1024) # buffer size is 1024 bytes
            routedata = routedata.split(":")
            if routedata[1] is RREP:
                # If the destination for RREP
                if routedata[3] == IP:
                    loopvar = True
                    route = str(routedata[3]+":"+":".join(routedata[5].split("#"))+routedata[4])
                    print(str("RREP at source, route is: "+route))
                
        except socket.error:
            loopvar = True
            routefound = False
            print("Timeout out of getroute")
            
    return routefound, route; 
    
sock = socket.socket(socket.AF_INET, # Internet
                     socket.SOCK_DGRAM) # UDP
sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
sock.bind((UDP_IP, UDP_PORT))
sock.settimeout(30)

if len(sys.argv) is  2:
    try:
        IP_List.remove(IP)
        
    except ValueError:
        print ("IP address not found in routing table. Exception!!!")
        pass
    
    #Generate random Sequence Number
    orig_seqNumber = str(random.randint(1,101))
    
    for tmpip in IP_List:
        if Path_Dict[tmpip] is None:
        
            # Record time
            txtime = str(datetime.time(datetime.utcnow()))
            txtime = float(txtime[-9:])
            
            # Find Route
            routeFlag, route = getRoute(orig_seqNumber, str(txtime), tmpip)
            
            rxtime = str(datetime.time(datetime.utcnow()))
            rxtime = float(rxtime[-9:])
            
            latency = (rxtime - txtime) * 1000
            
            
            if routeFlag is True:
            
                RList = route.split(":")
                print (" Route is ", RList)
                print (str("Time taken for route discovery is - " + str(latency) + " ms"))
                
                # Update path in dict
                Path_Dict[tmpip] = route
                
                #Prepare DATA frame
                data = str(orig_seqNumber + ":" + DATA + ":" + str(txtime) + ":" + tmpip + ":" + IP + ":" + Path_Dict[tmpip] + ":" + "Message sent from " + IP + ' - " ' + sys.argv[1] + ' "' + " received at the node!")
                
                print ("Sending Data packet")
                #Send Data frame
                sock.sendto(data, (UDP_IP, UDP_PORT))
                
            else:
                print ("No Route found to the node") 
            
        else:
                #Prepare header for flooding -> "seq_number:current_txtime"
                data = str(orig_seqNumber + ":" + DATA +":" + str(txtime) + ":" + tmpip + ":" + IP + ":" + Path_Dict[tmpip] + ":" + "Message sent from " + IP + ' - " ' + sys.argv[1] + ' "' + " received at the node!")
                
                #Send Data frame
                sock.sendto(data, (UDP_IP, UDP_PORT))       
    
while time.time() < t_end:

    try:
        rxData,addr = sock.recvfrom(1024) # buffer size is 1024 bytes
        rxData = rxData.split(":")
        if addr is not None and addr[0] != IP:
            if rxData[1] == RREQ:
                
                # If node is not the destination node or source node and no reception of frame already received
                if (rxData[3] != IP) and (rxData[4] != IP) and (IP not in rxData[5]):
                    
                    # Append node IP with a # 
                    txData = ":".join(rxData)+IP+"#"
                    print (str("RREQ forward: "+rxData[4]+" : "+" : ".join(txData.split(":")[5].split("#"))+rxData[3]))
                    
                    # Broadcast RREQ frame after append
                    sock.sendto(txData, (UDP_IP, UDP_PORT))
                
                # If destination node and for first frame arrival
                if (rxData[3] == IP):# and (SeqNo_Dict[str(rxData[4]] != int(rxData[0])):
                    # Append last destination node IP, swap the destination IP and source IP address
                    #print (int(rxData[0]))
                    #print (int(SeqNo_Dict[str(rxData[4])]))
                    
                    SeqNo_Dict = rxData[0]
                    rxData[1] = RREP
                    rxData[3] = rxData[4]
                    rxData[4] = IP
                    
                    #Prepare Tx frame
                    #txData = rxData.join(":")+node
                    txData = ":".join(rxData)
                    print (str("RREQ Destination: "+rxData[4]+" :" +" : ".join(txData.split(":")[5].split("#"))+rxData[3]))
                    
                    # Broadcast RREQ frame after append
                    sock.sendto(txData, (UDP_IP, UDP_PORT))
                    
            elif rxData[1] is RREP:
                # If node is not the destination node and the current node is a part of the route
                if rxData[3] != IP and IP in rxData[5]:
                    print (str("RREP forward : "+rxData[4]+" : "+" : ".join(rxData[5].split("#"))+rxData[3]))
                    # Broadcast RREP frame after append
                    sock.sendto(":".join(rxData), (UDP_IP, UDP_PORT))
                
                # The function will never hit for destination node as this while loop is never entered for the same        
                                
            elif rxData[1] is DATA:
                # If node is not the destination node and the current node is a part of the route
                if rxData[3] != IP and IP in rxData[5]:
                    print (str("DATA forward : "+rxData[4]+" : "+" : ".join(rxData[5].split("#"))+rxData[3]))
                    # Broadcast DATA frame after append
                    sock.sendto(":".join(rxData), (UDP_IP, UDP_PORT))
                    
                # If destination node
                if rxData[3] == IP:
                    print (str("Message sent from " + rxData[4] + ' - " ' + rxData[6] + ' "' + " received at the node!"))
            
            else:
                print ("Incorrect frame type received. Exception!!!!!!")

        addr = None
            
            
    except socket.error:
        addr = None
        pass

sock.close()
print "finished"
