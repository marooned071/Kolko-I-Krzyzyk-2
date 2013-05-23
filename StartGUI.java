import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class StartGUI extends JFrame{
	private JTextField nickFiled;
	private JButton startButton;
	private JLabel label;
	
	StartGUI(){
	

		this.setLayout(new BorderLayout());
		//JPanel panel = new JPanel();
		
		this.setSize(200, 100);

		label=new JLabel("Nazwa gracza: ");
		
		nickFiled=new JTextField("");
		startButton = new JButton("Start");
		
		this.add(label, BorderLayout.NORTH);
		this.add(nickFiled, BorderLayout.CENTER);
		this.add(startButton, BorderLayout.SOUTH);	
		this.setVisible(true);
	}
	
	private class ButtonAdapter implements ActionListener {
		public void actionPerformed(ActionEvent event) {				

		}
	}

}
