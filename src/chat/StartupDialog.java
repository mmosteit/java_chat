package chat;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class StartupDialog extends JDialog {

	/**
	 * 
	 */
	protected final static String CLIENT = "Client";
	protected final static String SERVER = "Server";
	public Vector<String> retVal;
	
	private static final long serialVersionUID = 6496614560674564776L;

	protected Box          MainHBox;
	protected JRadioButton ClientButton;
	protected JRadioButton ServerButton;
	protected JButton      OKButton;
	protected ButtonGroup  group;
	protected Box          RadioVBox;
	protected JPanel       cards;
	
	// Client Configuration
	protected JPanel       ClientCard;
	protected Box          ClientHBox;
	protected Box          ClientVBox;
	protected JPanel       ClientPanel;
	protected JLabel       ClientAddressLabel;
	protected JTextField   ClientAddressInput;
	protected JLabel       ClientRoomLabel;
	protected JTextField   ClientRoomInput;
	protected JLabel       UsernameLabel;
	protected JTextField   UsernameInput;
	protected EtchedBorder ClientBorder;
	
	// Server Configuration
	protected JPanel       ServerCard;
	protected Box          ServerVBox;
	protected JLabel       ServerAddressLabel;
	protected JTextField   ServerAddressInput;
	protected JLabel       ServerRoomnamesLabel;
	protected JTextField   ServerRoomnamesInput;
	protected EtchedBorder ServerBorder;
	
	
	// This class listens to the client and server radio buttons and switches the layout
	// according to which one is selected
	private class RadioListener implements ActionListener{
		
		public void actionPerformed(ActionEvent e){
			JRadioButton button;
			CardLayout cl = (CardLayout) cards.getLayout();
			
			button = (JRadioButton) e.getSource();
			
			cl.show(cards, button.getText());
		}
	}
	
	private class OKButtonListener implements ActionListener{
		StartupDialog parent;
		
		public OKButtonListener(StartupDialog newParent){
			parent = newParent;
		}
		
		public void actionPerformed(ActionEvent e){
			
			if( ClientButton.isSelected()){
				retVal = new Vector<String>();
				retVal.add("Client");
				retVal.add(ClientAddressInput.getText());
				retVal.add(ClientRoomInput.getText());
				retVal.add(UsernameInput.getText());
			}
			else if( ServerButton.isSelected()){
				retVal = new Vector<String>();
				retVal.add("Server");
				retVal.add(ServerAddressInput.getText());
				retVal.add(ServerRoomnamesInput.getText());
			}
			// This should not happen
			else{
				System.out.println("Error, code should not get here in the OK Button listener");
			}
			parent.setVisible(false);
		}
	}
	
	private RadioListener listener;
	
	public StartupDialog(){
		
		MainHBox = Box.createHorizontalBox();
		add(MainHBox);
		
		
		listener = new RadioListener();
		
		// Set up the Radio and OK Button
		ClientButton = new JRadioButton(CLIENT);
		ClientButton.addActionListener(listener);
		
		ServerButton = new JRadioButton(SERVER);
		ServerButton.addActionListener(listener);
		group        = new ButtonGroup();
		OKButton     = new JButton("OK");
		OKButtonListener oklistener = new OKButtonListener(this);
		OKButton.addActionListener(oklistener);
		
		RadioVBox = Box.createVerticalBox();
		RadioVBox.add(ClientButton);
		RadioVBox.add(ServerButton);
		RadioVBox.add(OKButton);
		
		group.add(ClientButton);
		group.add(ServerButton);
		MainHBox.add(RadioVBox);
		
		// Set up the client panel
		ClientCard    = new JPanel();
		ClientVBox    = Box.createVerticalBox();
		ClientCard.add(ClientVBox);
		ClientBorder  = new EtchedBorder(EtchedBorder.LOWERED);
		ClientVBox.setBorder(ClientBorder);
		ClientAddressLabel  = new JLabel("Server IP");
		ClientVBox.add(ClientAddressLabel);
		ClientAddressInput  = new JTextField();	
		ClientVBox.add(ClientAddressInput);
		ClientRoomLabel     = new JLabel("Room Name");
		ClientVBox.add(ClientRoomLabel);	
		ClientRoomInput     = new JTextField();
		ClientVBox.add(ClientRoomInput);
		UsernameLabel       = new JLabel("Username");
		ClientVBox.add(UsernameLabel);	
		UsernameInput       = new JTextField();
		ClientVBox.add(UsernameInput);
	
	
		// Set up the server panel
		ServerCard = new JPanel();
		ServerVBox = Box.createVerticalBox();
		ServerCard.add(ServerVBox);
		ServerBorder         = new EtchedBorder(EtchedBorder.LOWERED);
		ServerVBox.setBorder(ServerBorder);
		ServerAddressLabel   = new JLabel("IP Address");
		ServerVBox.add(ServerAddressLabel);
		ServerAddressInput   = new JTextField();
		ServerVBox.add(ServerAddressInput);
		ServerRoomnamesLabel = new JLabel("Room Names");
		ServerVBox.add(ServerRoomnamesLabel);
		ServerRoomnamesInput = new JTextField();
		ServerVBox.add(ServerRoomnamesInput);
		
	
		// Put all of the cards together
		cards = new JPanel(new CardLayout());
		cards.add(ClientCard,CLIENT);
		cards.add(ServerCard,SERVER);
		MainHBox.add(cards);
		
		ClientButton.setSelected(true);
		
		pack();
		
	
	}
	
	public Vector<String> getResult(){
		setModal(true);
		setVisible(true);
		
		return retVal;
	}
	
}
