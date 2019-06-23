import socket
import time
import commands
import sys


print "HELLoo!!!!",str(sys.argv)
UDP_IP = "192.168.210.255"
IP = commands.getoutput("ifconfig wlan0 | grep -Po  'inet \K[\d.]+'")
node = IP[-3:]
UDP_PORT = 5012
print " IP: ", IP

orig_seqNumber = -1
rcvd_seqNumber = -1
t_end = time.time() + 60
addr = None
RREP="1"
RREQ="0"

sock = socket.socket(socket.AF_INET, # Internet
                     socket.SOCK_DGRAM) # UDP
sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
sock.bind((UDP_IP, UDP_PORT))
sock.settimeout(60)

if len(sys.argv) is  2:
	orig_seqNumber = sys.argv[1]
	data = RREQ+":"+node
	sock.sendto(data, (UDP_IP, UDP_PORT))
	

while time.time() < t_end:

    try:
        data,addr = sock.recvfrom(1024) # buffer size is 1024 bytes
        
        if addr is not None and addr[0] != IP:
                print  "from: ", addr[0], " data: ", data
                if data[0] == RREQ:
                        if node not in data:
                                s1 = data+":"+node
                                print "send s1 " ,s1
                                sock.sendto(data+":"+node, (UDP_IP, UDP_PORT))
                                time.sleep(1)
                                
                                s2 = RREP+data[1:]+":"+node
                                ss= UDP_IP[:-3]+data[-3:]
                                sock.sendto(RREP+data[1:]+":"+node, (UDP_IP, UDP_PORT))
                                print "send s2" ,s2,"ip", ss
                                addr = None
                else:
                        if node in data:
                                print "we have node"
                                if node == data[2:5]:
                                        print "path to ",data[-3:]," is ",data[2:]
                                else:
                                        idx = data.find(node)
                                        s= UDP_IP[:-3]+data[idx-4:idx-1]
                                        print "addr[0]",addr[0],"return: ",s ," data:" ,data
                                        #sock.sendto(data, (UDP_IP[:-3]+data[idx-4:idx-1], UDP_PORT))
                                        sock.sendto(data, (UDP_IP, UDP_PORT))
                                addr = None
                        

    except socket.error:
        pass

sock.close()
print "finished"
