from datetime import datetime
import sys
import commands

otime = str(datetime.time(datetime.utcnow()))
print ("otime is", otime)
otime = otime[-9:]
print ("otime is", otime)
data = str(sys.argv[1] + ":" + otime)
print ("Data is:", data)

datalist = data.split(":")[1]
print ("Datalist is:", datalist)

temp = float(datalist[1]) + 1
print ("otime is", temp)

IP_List = ["192.168.210.152", "192.168.210.170", "192.168.210.173","192.168.210.193", "192.168.210.199" ]
IP = commands.getoutput("ifconfig wlan0 | grep -Po  'inet \K[\d.]+'")
print (" IP: ", IP)
