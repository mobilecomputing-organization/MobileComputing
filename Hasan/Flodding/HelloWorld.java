
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

class HelloWorld 
{ 
    // Your program begins with a call to main(). 
    // Prints "Hello, World" to the terminal window.
    
    //byte bcast_msg[]; 
    public static void main(String args[]) 
    { 
    	try {
        DatagramSocket sock = new DatagramSocket();
		sock.setBroadcast(true);
		InetAddress inetAddr = InetAddress.getLocalHost();
		byte[] bcast_msg = {1}; 
		//byte bcast_msg[] =  {2,2,1,4};//inetAddr.getHostAddress();
		System.out.println("Addr-" + inetAddr.getHostAddress());
		DatagramPacket packet = new DatagramPacket(bcast_msg, bcast_msg.length, InetAddress.getByName("192.168.210.255"), 5012);
		sock.send(packet);
        System.out.println("Hello, World");
        } catch (Exception SocketException)
        {
        	System.out.println("Exception");
        } 
    } 
} 