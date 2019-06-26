import socket
import time
import commands
import sys
import random
from datetime import datetime

UDP_IP = "192.168.210.255"
IP = commands.getoutput("ifconfig wlan0 | grep -Po  'inet \K[\d.]+'")
node = IP[-3:]
UDP_PORT = 5012
print " IP: ", IP

orig_seqNumber = -1
rcvd_seqNumber = -1
t_end = time.time() + 60
addr = None

sock = socket.socket(socket.AF_INET, # Internet
                     socket.SOCK_DGRAM) # UDP
sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
sock.bind((UDP_IP, UDP_PORT))
sock.settimeout(60)

txtime = str(datetime.time(datetime.utcnow()))
txtime = txtime[-9:]

if len(sys.argv) is  2:
    #Generate random sequence number
    orig_seqNumber = str(random.randint(1,101))
    
    #Prepare header for flooding -> "seq_number:current_txtime"
    data = str(orig_seqNumber + ":" + txtime + ":" + "Message sent from " + IP + ' - " ' + sys.argv[1] + ' "' + " received at the node!")

    # Send UDP Datagram
    sock.sendto(data, (UDP_IP, UDP_PORT))

while time.time() < t_end:

    try:
        data, addr = sock.recvfrom(1024) # buffer size is 1024 bytes
        
        if addr is not None and addr[0] != IP:
            rxtime = str(datetime.time(datetime.utcnow()))
            rxtime = float(rxtime[-9:])
                
            # Decode flooding header
            rcvd_seqNumber = data.split(":")[0]
            latency =  str( (rxtime - float(data.split(":")[1])) * 1000 )
            msg = data.split(":")[2]
                        
            if (rcvd_seqNumber != orig_seqNumber) or (orig_seqNumber == -1) :
                orig_seqNumber = rcvd_seqNumber

                #print " TX Address: ", IP, " TX data: ", orig_seqNumber
                print ( msg )

                ntxtime = str(datetime.time(datetime.utcnow()))
                ntxtime = ntxtime [-9:]

                #Prepare header for flooding -> "seq_number:current_txtime"
                data = str(orig_seqNumber + ":" + ntxtime + ":" + msg )

                sock.sendto(data , (UDP_IP, UDP_PORT))

                #print  "From: ", addr, "Latency:", latency, "data: ", data
                print(str("Node pair (Tx IP, Rx IP, Latency) : (" + addr[0] + ", " + IP + ", " + latency + " ms)" ))
                addr = None

    except socket.error:
        addr = None
        pass

sock.close()
print "finished"
