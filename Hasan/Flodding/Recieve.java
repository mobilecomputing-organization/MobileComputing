/**
 * 
 */
/**
 * @author johnj
 *
 */
import java.io.IOException; 
import java.net.DatagramPacket; 
import java.net.DatagramSocket; 
import java.util.Arrays;
import java.net.SocketException;

class PortReceive{

	public static void main(String[] args) {
		int team_number = 12;
		byte[] buf = {0};
		int i = 0;
		try{
			DatagramSocket sock = new DatagramSocket (5012);
		DatagramPacket packet = new DatagramPacket (buf , 1);
		
		while (i < 5) {
  			sock.receive(packet);
			if (packet!=null) {
				System.out.println("Received packet data : " +  Arrays.toString(packet.getData())); 
				break;
			}
		}	
		}
		catch (IOException ex)
		{
			System.out.println("Exception");
		}
		}
}