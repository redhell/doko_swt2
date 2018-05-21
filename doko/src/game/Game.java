package game;

import java.io.IOException;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import client.backend.model.classes.ConnectionSocket;
import client.backend.model.classes.Player;
import client.backend.model.classes.StateE;

public class Game {

	private ConnectionSocket connectionSocket;
	private Lobby lobby;
	private Player player;

	public Game() {

		connectionSocket = new ConnectionSocket();
		lobby = new Lobby(connectionSocket);
		player = new Player("Spieler");
		nextAction();

	}

	private void nextAction() {
		System.out.println("press 1 to queue for game");
		Scanner scanner = new Scanner(System.in);
		String input = scanner.next();
		while (!input.equals("1")) {
			System.out.println("press 1 to queue for game");
			input = scanner.next();
		}
		boolean connected = connectToServer();
		if(connected){
			StateE state = queueForGame();			
			if (state == StateE.GAMESTARTED) {
				try {
					System.out.println("game started");
					String cards = connectionSocket.getReader().readLine();
					JSONArray jarr = new JSONObject(cards).getJSONArray("cards");
					System.out.println(jarr.toString());
					player.setKarten(jarr);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	

	public boolean connectToServer() {
		return lobby.connectToServer();
	}

	public StateE queueForGame() {
		return lobby.queueForGame();
	}

}
