package chat;

import java.io.IOException;
import java.net.BindException;

import javax.swing.SwingUtilities;

public class MainProgram {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length == 0){
			printUsage();
		}
		else{
			if(args[0].equals("client")){
				final String ip_addr;
				final String username;
				final String chatroom;

				if (args.length != 4) {
					System.out.println("Error, must specify ip address of server, chatroom and username");
					System.exit(1);
				}

				ip_addr  = args[1];
				chatroom = args[2];
				username = args[3];

				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						Thread ConnectionThread;
						ClientGui client = new ClientGui(username);
						ClientGuiConnection connection;
						try {
							connection = new ClientGuiConnection(client,ip_addr, chatroom, username);
							client.SetConnection(connection);
							ConnectionThread = new Thread(connection);
							ConnectionThread.start();
							client.setTitle(username+"@"+chatroom);

						} 
						catch (IOException  e) {
							client.DisplayMessage("The connection to the server has been broken");
							client.DisplayMessage("Please verify that the server is running and on correct ip address and try again");	
						}
						catch(ClientGuiConnection.RoomNameException e){
							client.DisplayMessage(e.toString());
						}
						catch(ClientGuiConnection.UserNameException e){
							client.DisplayMessage(e.toString());
						}
					}
				});
			}
			else if(args[0].equals("server")){
			      MainServer server;

			        if(args.length < 2){
			            System.out.println("Error: must specify at least one chat room");
			            System.out.println("ChatServer: <ip_address> <room_list>");
			            System.out.println("ip_address can be external or localhost");
			            System.out.println("room_list is at least one room name");
			            System.exit(0);
			        }
			        try {
			            server = new MainServer(args);
			            server.run();
			        } catch (BindException e){
			            System.out.println("Error: Could not bind to "+args[1]);
			        } catch (IOException e){
			            System.out.println("Constructor failed due to IOException ");
			        } catch (Exception e){
			            System.out.println("Caught a generic exception from mainserver in main method:");
			            System.out.println(e.toString());
			        }
			}
			else{
				printUsage();
			}
		}
	}
	
	public static void printUsage(){
		System.out.println("Usage:");
		System.out.println("chat.jar client <ip_address> <room name> <username>");
		System.out.println("chat.jar server <ip_address> [rooms]");
	}

}
