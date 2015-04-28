package chat;

import java.io.IOException;
import java.net.BindException;

import javax.swing.SwingUtilities;

public class MainProgram {

	public static void main(String[] args) {
		

				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
				
						StartupDialog startup;
						startup = new StartupDialog();
						
					}
				});
				
			
	
	}
	
	

}
