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
t_end = time.time() + 30
addr = None
RREP="1"
RREQ="0"
RList=[]
msg="msg"
msg2="msg2"

sock = socket.socket(socket.AF_INET, # Internet
                     socket.SOCK_DGRAM) # UDP
sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
sock.bind((UDP_IP, UDP_PORT))
sock.settimeout(30)
startTime = time.time()

if len(sys.argv) >=  2:
	orig_seqNumber = sys.argv[1]
	if len(sys.argv) >  3:
                msg = sys.argv[2]
        startTime = time.time()
        data = RREQ+":"+node+"#"+msg+"#"+str(startTime)
        print   " data: ", data
        sock.sendto(data, (UDP_IP, UDP_PORT))

while time.time() < t_end:

    try:
        fulldata,addr = sock.recvfrom(1024) # buffer size is 1024 bytes
        fdata=fulldata.split("#",1)
	print "fulldata: ",fulldata
	print "fdata: ",fdata
	data = fdata[0]
        forwardData = fdata[1]
	msgData = forwardData .split("#",1)
	msg2 = msgData [0]
        startTime = msgData [1]
        #data[0:123.12.12.33#hello word#125445545time stamp in ms]
        if addr is not None and addr[0] != IP:
                print  "from: ", addr[0], " data: ", data , "msgRecv",msg2
                if data[0] == RREQ:
                        if node not in data:
                                sendReq = data+":"+node+"#"+forwardData
                                print "sendReq  " ,sendReq 
                                sock.sendto(sendReq  , (UDP_IP, UDP_PORT))
                                #time.sleep(1)

                                s2 = RREP+data[1:]+":"+node
                                ss= UDP_IP[:-3]+data[-3:]

				sendReply = RREP+data[1:]+":"+node+"#"+msg2+"#"+str(time.time())
                                print "sendReply " ,sendReq
                                sock.sendto(sendReply , (UDP_IP, UDP_PORT))
                                #print "send s2" ,s2,"ip", ss
                                addr = None
                else:
                        if node in data:
                                #print "we have node"
                                if node == data[2:5]:
					saveData=data.split("#",1)[0]
                                        if saveData[2:] not in RList:
						print "endtime",str(time.time()) ,"starttime",startTime
						latency = str(time.time() - float(startTime))
                                                #RList.append(data[2:]+"#latency="+latency)
						RList.append(saveData[2:])
                                                print "path to ",data[-3:]," is ",saveData[2:],"latency ", latency 
                                else:
                                        idx = data.find(node)
                                        s= UDP_IP[:-3]+data[idx-4:idx-1]
                                        print "from",addr[0],"to: ",s ," data:" ,data
                                        #sock.sendto(data, (UDP_IP[:-3]+data[idx-4:idx-1], UDP_PORT))

                                        if data[2:] not in RList:
						send = data+"#"+forwardData
						print "not in list"," data:" ,data
                                                sock.sendto(send , (UDP_IP, UDP_PORT))
                                                RList.append(data[2:])
                                addr = None


    except socket.error:
        pass

sock.close()
print "finished"
print "Route list ",RList
print "finished"
