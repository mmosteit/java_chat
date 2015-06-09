package chat;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.lang.Thread;
import java.util.Vector;

public class MainServer {

    public  Socket connection;
    public  ServerSocket MainSock;
    private HashMap<String,RoomThread> RoomMap;
    private RoomThread room;

    public MainServer(Vector<String> args) throws IOException {
        Thread RoomThread_Thread;
        RoomMap  = new HashMap<String,RoomThread>();
        MainSock = new ServerSocket();
        MainSock.bind(new InetSocketAddress(args.get(1),9001));


        for(int i = 2; i < args.size(); i++){

            if(RoomMap.containsKey(args.get(i))){
                System.out.println("Skipping redundant room "+args.get(i));
            }
            else{
                room = new RoomThread(args.get(i));
                RoomMap.put(args.get(i), room);
                System.out.println("Just created room "+args.get(i));
            }
            RoomThread_Thread = new Thread(room);
            RoomThread_Thread.start();
        }
    }


    public void run() {
        String UserName; // The username requested by incomming connections
        String RoomName; // The chatroom name requested by incoming connections
        ClientThread client;
        Thread thread;
        UTF8Socket sock;
        System.out.println("Now waiting for connections. Press Ctrl-C to stop.");
        while(true){
            try {

                connection = MainSock.accept();
                sock = new UTF8Socket(connection);

                // Get the desired room name
                RoomName = sock.ReceiveMessage();

                // Find the room if it exists
                room = RoomMap.get(RoomName);

                if (room == null){
                    sock.SendMessage("REJECTED");
                    sock.close();
                    continue;
                }
                else{
                    sock.SendMessage("ACCEPTED");
                }

                // Get the desired username
                UserName = sock.ReceiveMessage();


                // Add the user to the room if it does not already contain the user
                if(room.HasName(UserName)){
                    sock.SendMessage("REJECTED");
                    sock.close();
                    continue;
                }

                else{
                    sock.SendMessage("ACCEPTED");
                    client = new ClientThread(room, sock, UserName);
                    room.AddClient(client);
                    thread = new Thread(client);
                    thread.start();
                    room.AddMessage("server", "<"+UserName+" has entered the room>");
                }

                // If an IOException occurs anywhere in the above code, then the connection
                // is bad and no operations should continue. Keep going and accept another 
                // connection.
            } 
            catch (IOException e) {
                System.out.println("MainSock.accept() threw an IOException");
                e.printStackTrace();
            }
        }
    }
}
