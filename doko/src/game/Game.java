package game;

import java.io.IOException;
import java.util.Scanner;

import client.backend.model.classes.ConnectionSocket;
import client.backend.model.classes.StateE;

public class Game {

	private ConnectionSocket connectionSocket;

	private Lobby lobby;

	public Game() {

		connectionSocket = new ConnectionSocket();

		lobby = new Lobby(connectionSocket);

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
					System.out.println(cards);
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
