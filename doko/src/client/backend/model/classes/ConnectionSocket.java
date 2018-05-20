package client.backend.model.classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONObject;

public class ConnectionSocket {

	private User user;

	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;

	private boolean connected = false;
	
	private StateE state;

	public ConnectionSocket() {
		user = new User("user1");
	}


	public boolean connectToServer() {
		try {
			socket = new Socket("localhost", 25000);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			return connected = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return connected = false;
	}

	public StateE queueForGame() {
		String toSend = new JSONObject().put("event", "queueForGame").toString();
		out.println(toSend);
		try {
			String stringResponse = in.readLine();
			JSONObject json = new JSONObject(stringResponse);
			String parsedResponse = json.getString("event");
			while(!(parsedResponse.equals("gameStarted"))) {
			    stringResponse = in.readLine();
				json = new JSONObject(stringResponse);
				parsedResponse = json.getString("event");
			}
			
			return StateE.GAMESTARTED;
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}
	
	public PrintWriter getWriter(){
		return out;
	}
	
	public BufferedReader getReader(){
		return in;
	}
	
	public boolean isConnected() {
		return connected;
	}


	
}
