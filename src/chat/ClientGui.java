package chat;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;



public class ClientGui extends JFrame {


	private static final long serialVersionUID = 1L;
	protected JButton SendButton;
	protected Container mainPane;
	protected Box mainBox;
	protected JScrollPane mainScroll; // Scroll to View Conversation history
	protected JTextArea readText; // The text that shows up in the scrolled window
	protected JTextField writeText; // The input box that the user types into
	protected ClientGuiConnection connection; // The connection to the server
	protected String messageText; // This string holds the message to be sent to the server

	protected Box HBox;
	public    String username;
	public    String chatroom;

	public ClientGui(String newusername) throws HeadlessException {

		super("Client gui");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		username = newusername;

		mainPane = getContentPane();
		mainBox = Box.createVerticalBox();
		mainPane.add(mainBox, BorderLayout.CENTER);
		mainBox.setVisible(true);

		// Set up the output field
		readText = new JTextArea(20, 40);
		readText.setEditable(false);
		readText.setLineWrap(true);
		readText.setVisible(true);

		// This is where the conversation output is shown.
		mainScroll = new JScrollPane(readText);
		mainScroll.setVisible(true);
		mainBox.add(mainScroll);

		// Maintain visual separation between input and output fields.
		mainBox.add(new JSeparator(SwingConstants.VERTICAL));

		HBox = Box.createHorizontalBox();

		// Set up the main input field
		writeText = new JTextField(20);
		writeText.setVisible(true);
		writeText.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				try {
					SendMessage();
				} catch (Exception ex) {
					System.out.println("An error affecting SendMessage has occurred");
					System.out.println(ex.toString());
				}
			}
		});

		
		HBox.add(writeText);

		// Set up the send button
		SendButton = new JButton("Send");
		SendButton.setPreferredSize(new Dimension(100, 30));
		SendButton.setVisible(true);
		SendButton.addActionListener(new ActionListener() {
	
			public void actionPerformed(ActionEvent e) {
				try {
					SendMessage();
				} 
				catch (Exception ex) {
					System.out.println("An error affecting SendMessage has occurred");
					System.out.println(ex.toString());
				}
			}
		});

		HBox.add(SendButton);
		mainBox.add(HBox);
		mainPane.setVisible(true);

		pack();
		setVisible(true);


	}

	public void SetTitle(String title){
		this.setTitle(title);
	}

	// Send a text string to the ReadText widget
	public void DisplayMessage(String text) {
		final String finalText = text;
		assert (readText != null);
		if(javax.swing.SwingUtilities.isEventDispatchThread()){
			readText.append(text+'\n');
		}
		else{

			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					readText.append(finalText+'\n');
				}
			});
		}

	}

	// Make the gui aware of the connection to the server
	protected void SetConnection(ClientGuiConnection newClient) {
		connection = newClient;
	}

	private class SendMessageWorker extends SwingWorker<Void, Void>{
		public String message;
		public ClientGuiConnection connection;
		SendMessageWorker(String newMessage, ClientGuiConnection newConnection){
			message = newMessage;
			connection = newConnection;
		}

		protected Void doInBackground() throws Exception {

			connection.SendMessage(message);
			return null;
		}

	}

	// This method grabs text from the input field and
	// gives it to the ClientGuiConnection to be sent to the server
	protected void SendMessage() {
		String messageText;

		messageText = writeText.getText();
		writeText.setText(""); // Clear out the input box for the next message
		if (messageText.trim().length() == 0) {
			return;
		}

		DisplayMessage("Me: " + messageText);

		new SendMessageWorker(messageText, connection).execute();
	}
}
