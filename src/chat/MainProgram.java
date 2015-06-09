package chat;

import java.io.IOException;
import java.net.BindException;
import java.util.Collections;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

import chat.ClientGuiConnection.RoomNameException;
import chat.ClientGuiConnection.UserNameException;

public class MainProgram {

	public static void main(String[] args) {


		SwingUtilities.invokeLater(new Runnable(){
			public void run(){

				StartupDialog startup;
				startup = new StartupDialog();
				Vector<String> retVal;
				retVal = startup.getResult();
				if(retVal == null){
					System.out.println("NULL retval");
				}
				else{
					System.out.println(retVal.toString());

					if (retVal.get(0).equals("Client")){

						if (retVal.get(1).trim().equals("") || retVal.get(2).trim().equals("") || retVal.get(3).trim().equals("") ){

							JOptionPane.showMessageDialog(null,"Server IP address, roomname and username must be specified for client");
						}
						else{
							String ip_addr  = retVal.get(1);
							String chatroom = retVal.get(2);
							String username = retVal.get(3);
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
							catch(IOException  e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							catch(RoomNameException e){
								e.printStackTrace();
							}
							catch(UserNameException e){
								e.printStackTrace();
							}

						}

					}
					else if(retVal.get(0).equals("Server")){
						if(retVal.get(1).trim().equals("") || retVal.get(2).trim().equals("")){
							JOptionPane.showMessageDialog(null,"Error: Server IP address and chatroom names must be specified for server");
						}
						else{

							MainServer server;
							Vector<String> args = new Vector<String>();
							args.add("server"); // Technically does not matter
							args.add(retVal.get(1));
							String [] roomNames;
							roomNames = retVal.get(2).split("\\s+");// Split the roomnames string into an array by whitespace
							Collections.addAll(args, roomNames);

							try {
								server = new MainServer(args);
								server.run();
							} catch (BindException e){
								JOptionPane.showMessageDialog(null, "Error: Could not bind to "+retVal.get(1));
							} catch (IOException e){
								JOptionPane.showMessageDialog(null, "Constructor failed due to IOException ");
							} catch (Exception e){
								JOptionPane.showMessageDialog(null,"Caught a generic exception from mainserver in main method:"+e.toString());
							}
						}


					}
					else{
						System.out.println("Code should not get here in MainProgram.java");
					}

				}
			}
		});

	}
}
