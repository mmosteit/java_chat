package chat;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

// This class represents a wrapper around a TCP socket.  It converts UTF-8
// network data to Java's native String type.

public class UTF8Socket{
	Socket sock; // The underlying socket
	private DataInputStream  in;
	private DataOutputStream out;

	
	public UTF8Socket(String ip) throws UnknownHostException, IOException{
		sock = new Socket(ip, 9001);
		in   = new DataInputStream(sock.getInputStream());
		out  = new DataOutputStream(sock.getOutputStream());
	}

	// Note: The socket should be unused upon passing it
	// to this constructor, as any buffered data will
	// cause problems.
	public UTF8Socket(Socket newSock) throws IOException{
		sock = newSock;
		in   = new DataInputStream(sock.getInputStream());
		out  = new DataOutputStream(sock.getOutputStream());
	}

	// Send 4 bytes denoting the size of the string, followed
	// by the string itself (converted to utf-8)
	public void SendMessage(String message) throws IOException{

		// Send message through socket
		int length;
		byte [] buffer = message.getBytes(Charset.forName("UTF-8"));

		length = buffer.length;

        out.writeInt(length);
        out.write(buffer,0,length);
	} 

    // Read in 4 bytes denoting the size of the string, followed by the string
    // itself. The 'size' parameter denotes the total number of bytes.

	public String ReceiveMessage() throws IOException{
		int size;
		byte [] buffer;

		size = in.readInt();

		buffer = new byte[size];
		
		in.readFully(buffer, 0, size);

		return new String(buffer, "UTF-8");
	}
	
	public void close(){
		try {
			sock.shutdownOutput();
			sock.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
