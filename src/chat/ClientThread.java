package chat;

import java.io.IOException;


public class ClientThread implements Runnable {

	private UTF8Socket     sock; // The connection to the client
	private RoomThread  room; // The room that owns this thread.
	public final String name; // The name of the client this threads connects to
	
	// This constructor is called by the main server thread.
	public ClientThread(RoomThread newroom, UTF8Socket newSock, String newName){
		sock  = newSock;
		room  = newroom;
		name  = newName;
	}

	@Override
	public void run() {
		String message;
		
		// spin while waiting on a message
		while(true){

            try {
                // Read in a message and send it to the chatroom's message
                // queue

				message = sock.ReceiveMessage();
				
                // Name is required as the first parameter so that the server
                // can know not to relay the message back to the client that
                // said it.

				room.AddMessage(name, name+": "+message);
			}
			
			catch(IOException e){
				room.RemoveClient(name);
				return;
			}
		}
	}

	// This method is called by the room thread
	public void SendToClient(String message) throws IOException{
		sock.SendMessage(message);

	}
}
