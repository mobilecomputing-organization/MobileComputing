import socket
import time
import commands
import sys

UDP_IP = "192.168.210.255"
IP = commands.getoutput("ifconfig wlan0 | grep -Po  'inet \K[\d.]+'")
node = IP[-3:]
UDP_PORT = 5012
print " IP: ", IP

orig_seqNumber = -1
rcvd_seqNumber = -1
t_end = time.time() + 40
addr = None
RREP="1"
RREQ="0"
RList=[]
msg=" "
msg2=" "

sock = socket.socket(socket.AF_INET, # Internet
                     socket.SOCK_DGRAM) # UDP
sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
sock.bind((UDP_IP, UDP_PORT))
sock.settimeout(40)

if len(sys.argv) >=  2:
	orig_seqNumber = sys.argv[1]
	if len(sys.argv) is  3:
                msg = sys.argv[2]

        data = RREQ+":"+node+"#"+msg
        print   " data: ", data 
        sock.sendto(data, (UDP_IP, UDP_PORT))

while time.time() < t_end:

    try:
        fulldata,addr = sock.recvfrom(1024) # buffer size is 1024 bytes
        data = fulldata.split("#",1)[0]
        #msg2 = fulldata.split("#",1)[1]
        #data[0:123.12.12.33#hello word]
        if addr is not None and addr[0] != IP:
                print  "from: ", addr[0], " data: ", data , "msgRecv",msg2
                if data[0] == RREQ:
                        if node not in data:
                                s1 = data+":"+node
                                print "send s1 " ,s1
                                sock.sendto(data+":"+node, (UDP_IP, UDP_PORT))
                                #time.sleep(1)
                                
                                s2 = RREP+data[1:]+":"+node
                                ss= UDP_IP[:-3]+data[-3:]
                                sock.sendto(RREP+data[1:]+":"+node, (UDP_IP, UDP_PORT))
                                print "send s2" ,s2,"ip", ss
                                addr = None
                else:
                        if node in data:
                                #print "we have node"
                                if node == data[2:5]:
                                        if data[2:] not in RList:
                                                RList.append(data[2:])
                                                print "path to ",data[-3:]," is ",data[2:]
                                else:
                                        idx = data.find(node)
                                        s= UDP_IP[:-3]+data[idx-4:idx-1]
                                        print "addr[0]",addr[0],"return: ",s ," data:" ,data
                                        #sock.sendto(data, (UDP_IP[:-3]+data[idx-4:idx-1], UDP_PORT))

                                        if data[2:] not in RList:
                                                sock.sendto(data, (UDP_IP, UDP_PORT))
                                                RList.append(data[2:])
                                addr = None


    except socket.error:
        pass

sock.close()
print "finished"
print ";;;;;;",RList
print "finished"
