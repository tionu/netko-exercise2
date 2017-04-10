package tcpeditor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Model {

	public enum Modus {
		CLIENT, SERVER
	}

	public interface ModelListener {

		public void modelChanged();

	}

	private static final int CLIENT_TIMEOUT_MS = 3000;

	private List<ModelListener> listeners;
	private Modus modus;
	private String url;
	private int port;
	private String textToSend;
	private String receivedText;
	Thread serverThread;
	private Socket socket; // Dieses Attribut benötigen Sie, wenn Sie den Modus
							// SERVER programmieren, um einen Socket,
	// der in der Methode startServer() erstellt wurde auch in der Methode
	// sendData() verwenden zu können

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
		for (ModelListener listener : listeners)
			listener.modelChanged();
	}

	public void sendData() {
		switch (modus) {
		case SERVER:
			sendDataAsServer();
			break;
		case CLIENT:
			try {
				receivedText = sendDataAsClient();
			} catch (TimeoutException e) {
				receivedText = "Error: " + e.getMessage();
			}
			break;
		}
	}

	private String sendDataAsClient() throws TimeoutException {
		try (Socket socket = new Socket(url, port)) {
			OutputStream output = socket.getOutputStream();
			PrintStream printstream = new PrintStream(output, true);
			printstream.print(textToSend);
			InputStream inputstream = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
			receivedText = "";

			Date start = new Date();
			while (!reader.ready()) {
				Thread.sleep(10);
				if (new Date().getTime() - start.getTime() >= CLIENT_TIMEOUT_MS) {
					throw new TimeoutException("timeout on waiting for response from server. No reponse within "
							+ CLIENT_TIMEOUT_MS + "ms.");
				}
			}
			while (reader.ready()) {
				receivedText += reader.readLine() + "\n";
			}
			reader.close();
			inputstream.close();
			modelChanged();
		} catch (IOException e) {
			System.err.println("IO Exception");
		} catch (NullPointerException e) {
			System.err.println("keine Nachricht");
		} catch (InterruptedException ignored) {
		}
		return receivedText;
	}

	private void sendDataAsServer() {
		try {
			OutputStream output = socket.getOutputStream();
			PrintStream printstream = new PrintStream(output, true);
			System.out.println("Send text: " + textToSend);
			printstream.print(textToSend);
		} catch (IOException e) {
			System.err.println("IO Exception" + e.getMessage());
		} catch (NullPointerException e) {
			System.err.println("keine Nachricht");
		}
	}

	public void startServer() {

		if (serverThread != null) {
			System.out.println("Server already started.");
			return;
		}

		Runnable serverTask = new Runnable() {
			@Override
			public void run() {
				try (ServerSocket serverSocket = new ServerSocket(port)) {
					System.out.println("Waiting for clients to connect...");
					while (true) {
						socket = serverSocket.accept();
						System.out.println("Client connected. Receiving text...");
						BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						receivedText = "";
						while (reader.ready()) {
							receivedText += reader.readLine() + "\n";
						}
						System.out.println("Received Test: " + receivedText);
						modelChanged();
					}
				} catch (IOException e) {
					System.err.println("Unable to process client request");
					e.printStackTrace();
				}
			}
		};
		serverThread = new Thread(serverTask);
		serverThread.start();
	}

}
