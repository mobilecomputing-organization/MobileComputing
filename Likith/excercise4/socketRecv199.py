import socket
import time

UDP_IP = "192.168.210.255"
IP = "192.168.210.199"
UDP_PORT = 5012
print " IP: ", IP
orig_seqNumber = -1
rcvd_seqNumber = -1
addr = None
sock = socket.socket(socket.AF_INET, # Internet
                     socket.SOCK_DGRAM) # UDP
sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
sock.bind((UDP_IP, UDP_PORT))

sock.settimeout(60)
t_end = time.time() + 60


while time.time() < t_end:

    try:
        data,addr = sock.recvfrom(1024) # buffer size is 1024 bytes
        rcvd_seqNumber = data

    except socket.error:
        addr = None
        pass

    if addr is not None:
        print  "RX( ", addr, IP, " ) ", "Latency", " RX data: ", data
        addr = None
        
        if (rcvd_seqNumber != orig_seqNumber) or (orig_seqNumber == -1) :
            orig_seqNumber = rcvd_seqNumber
            #print " TX Address: ", IP, " TX data: ", orig_seqNumber
            sock.sendto(orig_seqNumber, (UDP_IP, UDP_PORT))
            
    
sock.close()
print " while 2222222"



