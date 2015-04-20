package chat;

import java.util.*;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// This class is used to coordinate the handling of messages
// among all the ClientThreads associated with it.
// The main variables are the NameMessageQueue and the NameThreadMap.

// As its name implies, the NameMessage queue holds messages and the 
// name of the ClientThread that sent it. The name is used for two purposes
// One, to attach the name of the client that sent a message to all clients that
// received the message. The second is to make sure that a message gets sent to 
// all clients associated with the room except the one that sent it.

// The NameThread map is keyed on the username of the client and has a value
// of that Client's thread.

public class RoomThread implements Runnable {

    private final String RoomName;

    private LinkedBlockingQueue<NameMessage> NameMessageQueue; 
    public  Condition NotEmpty; // Used to signal when there are messages in the NameMessageQueue
    public  Lock NameMessageQueueLock;

    private HashMap<String,ClientThread> NameThreadMap;
    private Lock NameThreadMapLock;

    // This class is designed for use with the NameMessageQueue declared above.
    private class NameMessage{
        public String Name;   // The name of the ClientThread sending the message
        public String Message; // The message sent by the client thread
        public NameMessage(String NewName, String NewMessage){
            Name    = NewName;
            Message = NewMessage;
        }
    }

    public RoomThread(String newName) {

        RoomName              = newName;

        NameMessageQueue      = new LinkedBlockingQueue<NameMessage>();
        NameMessageQueueLock       = new ReentrantLock();
        NotEmpty              = NameMessageQueueLock.newCondition(); 

        NameThreadMap         = new HashMap<String, ClientThread>();
        NameThreadMapLock     = new ReentrantLock();
    }



    @Override
        public void run() {

            NameMessage CurrentNameMessage;
            String message;


            Map.Entry<String, ClientThread> CurrentNameMapEntry;
            Iterator<Map.Entry<String, ClientThread>> NameThreadMapIt;

            while(true){
                NameMessageQueueLock.lock();

                try{

                    // Spin while waiting on the NameMessageQueue to get a message
                    while(NameMessageQueue.size() == 0){
                        NotEmpty.await();
                    }

                    // For each message: Go through each user and send the message
                    while(NameMessageQueue.size() != 0){
                        CurrentNameMessage = NameMessageQueue.take();
                        message = CurrentNameMessage.Message;

                        NameThreadMapLock.lock();

                        try{
                            // Send the message to each client in the ClientThreadlist
                            NameThreadMapIt = NameThreadMap.entrySet().iterator();
                            while(NameThreadMapIt.hasNext()){

                                CurrentNameMapEntry  = NameThreadMapIt.next();

                                // Don't broadcast a message back to the same client that sent it.
                                if (CurrentNameMessage.Name != CurrentNameMapEntry.getKey()){
                                    try {
                                        CurrentNameMapEntry.getValue().SendToClient(message);
                                    }

                                    // There is something wrong with the client connection.
                                    // Do nothing, the removal of the client will be taken care of via
                                    // The RemoveClient method
                                    catch (IOException e) {

                                    }
                                }
                            }
                        }

                        finally{
                            NameThreadMapLock.unlock();
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }

                finally{
                    NameMessageQueueLock.unlock();
                }
            }
        }

    // This method is called by the MainServer when a request to join 
    // a particular chatroom is made.
    public void AddClient(ClientThread user){
        NameThreadMapLock.lock();
        NameThreadMap.put(user.name, user);
        NameThreadMapLock.unlock();
    }


    // Adds a message to the MessageQueue. 'name' refers to the name of the client
    // that sent it.
    public void AddMessage(String name, String message){
        NameMessageQueueLock.lock();
        try {
            NameMessageQueue.put(new NameMessage(name, message));
            NotEmpty.signal();
        } 

        catch (InterruptedException e){
            e.printStackTrace();
        }

        finally{
            NameMessageQueueLock.unlock();
        }
    }

    // Does this room have a client with the given name?
    public boolean HasName(String name){
        boolean retval;
        NameThreadMapLock.lock();
        retval =  NameThreadMap.containsKey(name);
        NameThreadMapLock.unlock();
        return retval;
    }

    // This method is called by client threads upon receiving an IOException
    // from their socket.
    public void RemoveClient(String name){
        NameThreadMapLock.lock();
        try{
            NameThreadMap.remove(name);
        }
        finally{
            NameThreadMapLock.unlock();
        }
        AddMessage("","<"+name+" has left room "+RoomName+">");
    }
}
