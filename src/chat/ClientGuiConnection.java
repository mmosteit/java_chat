package chat;

import java.io.IOException;

public class ClientGuiConnection implements Runnable {

    ClientGui gui; // This connection must be aware of the gui that owns it
    UTF8Socket sock;  // Wrapper around a tcp socket that takes care of some low-level annoyances involving unicode and binary data.

    // This exception is thrown if the user selects a chatroom that does not exist
	public static class RoomNameException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String message;
		public RoomNameException(String message){
			super(message);
			this.message = message;
		}
		public String toString(){
			return this.message;
		}
	}

    // This exception is thrown if the user selects a username that is already taken.	
	public static class UserNameException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String message;
		public UserNameException(String message){
			super(message);
			this.message = message;
		}
		public String toString() {
			return this.message;
		}
	}


    public ClientGuiConnection(ClientGui newGui, String ip, String chatroom, String username) throws IOException, RoomNameException, UserNameException{
        gui  = newGui;
        sock = new UTF8Socket(ip);
  

        // Request the desired room and username from the server
        String response;

        // Send the desired chatroom to the server
        sock.SendMessage(chatroom);
        response = sock.ReceiveMessage();
        if (! response.equals("ACCEPTED")){
            throw new RoomNameException("Room "+chatroom+" does not exist. Please restart the program and choose a room that exists.");
        }

        // Request the desired username. This will return an error if the
        // Desired username already exists within the requested room.
        SendMessage(username);
        response = sock.ReceiveMessage();

        if(! response.equals("ACCEPTED")){
           throw new UserNameException("Username "+username+" is already taken. Please restart the program and choose a new name.");
        }

    }


    // Spin in a loop reading messages and display them to the client gui.
    public void run()
    {
        String message;


        // The main loop
        while(true)
        {
            try {
                message = sock.ReceiveMessage();
            } 

            catch (IOException e) {
                gui.DisplayMessage("Connection to server has been broken. "+
                        "Please make sure server is running and restart this program.");
                return;
            }
            // Give the message to the ClientGui for display
            gui.DisplayMessage(message);
        }
    }

    public void SetClientGui(ClientGui newClient){
        gui = newClient;
    }

    public void SendMessage(String message) throws IOException{
        sock.SendMessage(message);
    }

    public void close(){
        sock.close();
    }
}
