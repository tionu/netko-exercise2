package tcpeditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import tcpeditor.Model.ModelListener;
import tcpeditor.Model.Modus;

public class View implements ModelListener{
	
	private Model model;
	private JFrame frame;
	private JPanel framePanel;
	private JComboBox<Model.Modus> clientServerComboBox;
	private JPanel controlPanel;
	private JSplitPane inputOutputSplitPane;
	private JTextField portTextField;
	private JTextField urlTextField;
	private JTextPane receiveTextPane;
	private JTextPane sendTextPane;
	private JButton sendButton;
	private JButton startConnectButton;
	
	public View(Model model) {
		this.model = model;
		frame = new JFrame("TCP/IP-Viewer");
		framePanel = new JPanel(new BorderLayout());
		clientServerComboBox = new JComboBox<>(Model.Modus.values());
		controlPanel = new JPanel(new FlowLayout());
		portTextField = new JTextField();
		urlTextField = new JTextField();
		sendButton = new JButton();
		startConnectButton = new JButton();
		inputOutputSplitPane = new JSplitPane();
		receiveTextPane = new JTextPane();
		sendTextPane = new JTextPane();
	}
	
	public void show() {
		readFromModel();

		receiveTextPane.setEditable(false);
		
		inputOutputSplitPane.setLeftComponent(receiveTextPane);
		inputOutputSplitPane.setRightComponent(sendTextPane);
		
//		clientServerComboBox.setSelectedItem(Model.Modus.CLIENT); // Diese beiden Zeile einkommentieren, falls Sie nur ...
//		clientServerComboBox.setEditable(false); // ... einen Client oder nur einen Server programmieren möchten
		
		portTextField.setSize(100, 40);
		urlTextField.setSize(400, 40);
		sendButton.setText("Senden");
		sendButton.setActionCommand("SEND");
		startConnectButton.setText("Start");		
		startConnectButton.setActionCommand("START");
		
		controlPanel.add(clientServerComboBox);
		controlPanel.add(urlTextField);
		controlPanel.add(portTextField);
		controlPanel.add(sendButton);
		controlPanel.add(startConnectButton);
		
		framePanel.add(controlPanel, BorderLayout.NORTH);
		framePanel.add(inputOutputSplitPane, BorderLayout.CENTER);
		
		frame.add(framePanel);
		frame.setSize(1000, 800);
		frame.setVisible(true);

		inputOutputSplitPane.setDividerLocation(0.5);
		
	}
	
	@Override
	public void modelChanged() {
		readFromModel();
	}

	// angelehnt an Rheinwerk-Computung "Java ist auch eine Insel" - http://openbook.rheinwerk-verlag.de/javainsel9/javainsel_19_011.htm
	static void addComponent(Container container,
			GridBagLayout grigbagLayout,
			Component component,
			int x,// int y,
			//int width, int height,
			double weightx){//, double weighty ){
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = x; gbc.gridy = 0;// gbc.gridy = y;
//		gbc.gridwidth = width; gbc.gridheight = height;
		gbc.gridwidth = 1; gbc.weighty = 1;
		gbc.weightx = weightx; gbc.weighty = 0.0;// gbc.weighty = weighty;
		grigbagLayout.setConstraints(component, gbc);
		container.add(component);
	}
	
	public void readFromModel() {
		receiveTextPane.setText(model.getReceivedText());
		sendTextPane.setText(model.getTextToSend());
		urlTextField.setText(model.getUrl());
		portTextField.setText(String.valueOf(model.getPort()));
		clientServerComboBox.setSelectedItem(model.getModus());
	}
	
	public void writeToModel() {
		model.setReceivedText(receiveTextPane.getText());
		model.setTextToSend(sendTextPane.getText());
		model.setUrl(urlTextField.getText());
		model.setPort(Integer.valueOf(portTextField.getText()));
		model.setModus((Modus)clientServerComboBox.getSelectedItem());
	}
	
	public void addActionListener(ActionListener listener) {
		sendButton.addActionListener(listener);
		startConnectButton.addActionListener(listener);
	}
	
}
