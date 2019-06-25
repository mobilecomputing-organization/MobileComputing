import socket
import time
import commands
import sys
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

otime = str(datetime.time(datetime.utcnow()))
otime = otime[-6:]


if len(sys.argv) is  2:
	orig_seqNumber = sys.argv[1]
	sock.sendto(sys.argv[1]+otime, (UDP_IP, UDP_PORT))
	

while time.time() < t_end:

    try:
        data,addr = sock.recvfrom(1024) # buffer size is 1024 bytes
        rcvd_seqNumber = data[0]
        Latency = 999999-int.from_bytes(date[1:],byteorder='big')
        if addr is not None and addr[0] != IP:
            print  "From ", addr, "Latency", "data: ", data
            addr = None
        
            if (rcvd_seqNumber != orig_seqNumber) or (orig_seqNumber == -1) :
                orig_seqNumber = rcvd_seqNumber
                #print " TX Address: ", IP, " TX data: ", orig_seqNumber
                sock.sendto(orig_seqNumber, (UDP_IP, UDP_PORT))

    except socket.error:
        addr = None
        pass

sock.close()
print "finished"



