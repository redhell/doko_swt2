package backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONObject;

import backend.enums.JSONActionsE;
import backend.enums.JSONAttributesE;
import backend.enums.JSONEventsE;

public class ConnectionSocket {

	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;

	private static ConnectionSocket instance;

	private boolean connected = false;

	private ConnectionSocket() {

	}

	public static synchronized ConnectionSocket getInstance() {
		if (ConnectionSocket.instance == null) {
			ConnectionSocket.instance = new ConnectionSocket();
		}
		return ConnectionSocket.instance;
	}

	public boolean login(String json_credentials) {
		if (!connected) {
			try {
				socket = new Socket("localhost", 25000);
				writer = new PrintWriter(socket.getOutputStream(), true);
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				sendMessage(json_credentials);

				String response = readMessage();

				JSONObject json_response = new JSONObject(response);
				if (json_response.getString(JSONActionsE.EVENT.name()).equals(JSONEventsE.LOGIN.name())) {
					if (json_response.getString(JSONEventsE.LOGIN.name())
							.equals(JSONAttributesE.LOGINVERIFIED.name())) {
						connected = true;
					} else
						connected = false;
				}

			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return connected;
	}

	public void sendMessage(String message) {
		writer.println(message);
		writer.flush();
	}

	public String readMessage() {
		try {
			return reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "lol";
	}

	public boolean isConnected() {
		return connected;
	}

}
