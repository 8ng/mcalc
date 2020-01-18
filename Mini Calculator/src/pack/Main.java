package pack;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Main {
	static final String HINT = "Hint: Type in \"1+2/3\".";
	static final String NOUT = "No output!";
	static String u;
	
	static String message(String userInput) {
		try {
			if (userInput.length() == 0)
				return HINT;
			// Calculating Math Expression
			if (userInput.replaceAll("[^=><]", "").length() == 0 &&
				userInput.replaceAll("[^x]", "").length() == 0)
				return Evaluation.format(userInput);
			if (userInput.replaceAll("[^=]", "").length() == 1 && 
			    userInput.charAt(userInput.length()-1) == '=')
				return Evaluation.format(userInput);
			// Compare Values
			if (userInput.replaceAll("[^=><]", "").length() == 1 &&
					userInput.replaceAll("[^x]", "").length() == 0 && 
					userInput.charAt(0) != '=' &&
					userInput.charAt(0) != '>' &&
					userInput.charAt(0) != '<')
				return Evaluation.bool(userInput);
			// Solving Equation
			return Polynomial.format(userInput);
		} catch (Exception e) {
			return NOUT;
		}
	}
	
	public static void main(String args[]) {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		JMenu file = new JMenu("File");
		menuBar.add(file);
		JMenu view = new JMenu("View");
		menuBar.add(view);
		JCheckBoxMenuItem alwaysOnTop = new JCheckBoxMenuItem("Always On Top");
		alwaysOnTop.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					frame.setAlwaysOnTop(true);
				else frame.setAlwaysOnTop(false);
			}
		});
		view.add(alwaysOnTop);
		JMenuItem menuExit = new JMenuItem("Exit");
		menuExit.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent event) {
	            System.exit(0);
	        }
	    });
		file.add(menuExit);
		
		// userInput box
		JTextField inBox = new JTextField();
		inBox.setPreferredSize(new Dimension(180, 27));
		inBox.setFont(new Font("Arial", Font.PLAIN, 18));
		panel.add(inBox);
		
		// Create a button
		JButton button = new JButton("Enter");
		button.setFont(new Font("Arial", Font.BOLD, 12));
		panel.add(button);

		// Create an output box 
		JTextArea outBox = new JTextArea(HINT);
		outBox.setPreferredSize(new Dimension(250, 200));
		outBox.setEditable(false);
		outBox.setFont(new Font("Arial", Font.PLAIN, 18));
		panel.add(outBox);
		
		// Make button functional
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				u = inBox.getText();
				outBox.setText(message(u));
			}
		};
		inBox.addActionListener(action);
		button.addActionListener(action);
		
		frame.setSize(280, 125);
		frame.setLocationRelativeTo(null);
		frame.setTitle("Mini Calculator");
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel.setBackground(Color.WHITE);
		frame.add(panel);
	}
}
