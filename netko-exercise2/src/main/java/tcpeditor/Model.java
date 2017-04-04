package tcpeditor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Model {
	
	public enum Modus {
		CLIENT, SERVER
	}
	
	public interface ModelListener {
		
		public void modelChanged();
		
	}
	
	private List<ModelListener> listeners;
	private Modus modus;
	private String url;
	private int port;
	private String textToSend;
	private String receivedText;
	private Socket socket; // Dieses Attribut benötigen Sie, wenn Sie den Modus SERVER programmieren, um einen Socket,
	// der in der Methode startServer() erstellt wurde auch in der Methode sendData() verwenden zu können
	
	public Model() {
		this.listeners = new LinkedList<>();
	}
	
	public Modus getModus() {
		return modus;
	}

	public void setModus(Modus modus) {
		this.modus = modus;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getTextToSend() {
		return textToSend;
	}

	public void setTextToSend(String textToSend) {
		this.textToSend = textToSend;
	}

	public String getReceivedText() {
		return receivedText;
	}

	public void setReceivedText(String receivedText) {
		this.receivedText = receivedText;
	}

	public void addListener(ModelListener listener) {
		listeners.add(listener);
	}

	private void modelChanged() {
		for(ModelListener listener : listeners)
			listener.modelChanged();
	}
	
	public void sendData(){
		// TODO
	}
	
	public void startServer() {
		// TODO
	}
}
