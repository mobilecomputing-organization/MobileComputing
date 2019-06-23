import socket
import time
import commands

UDP_IP = "192.168.210.255"
IP = commands.getoutput("ifconfig wlan0 | grep -Po  'inet \K[\d.]+'")
UDP_PORT = 5012
print " IP: ", IP

seqNumber = 0
#print "UDP target IP:", UDP_IP
#print "UDP target port:", UDP_PORT
#print "message:", MESSAGE

sock = socket.socket(socket.AF_INET, # Internet
                     socket.SOCK_DGRAM) # UDP
sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
t_end = time.time() + 30
#print " while 1111111"
#while time.time() < t_end:
    #print " while inside"
    #seqNumber = seqNumber+1
    #print " TX Address: ", IP, " TX data: ", seqNumber
sock.sendto(str(1), (UDP_IP, UDP_PORT))
    #time.sleep(5)


sock.close()
#print " while 2222222"

