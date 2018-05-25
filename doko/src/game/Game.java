package game;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import client.backend.model.classes.ConnectionSocket;
import client.backend.model.classes.StateE;
import client.backend.model.classes.User;

public class Game {

	private ConnectionSocket connectionSocket;
	
	private Lobby lobby;
	private User player;
	
	private Map<User, Card> board;
	private Map<User, List<Card>> assignedCards;
	private User roundStarter;

	public Game() {

		connectionSocket = new ConnectionSocket();
		lobby = new Lobby(connectionSocket);
		player = new User("Spieler");
		nextAction();
		
		board = new HashMap<User, Card>(4);
		assignedCards = new HashMap<User, List<Card>>();

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
	
	public boolean validPlay(User p, Card card) {
		/** Check card: Ob eine Trumpfkarte gespielt wurde oder das Symbol, zu dem des Spieleröffners, passt
		 */
		if(!card.isTrumpf()	&&	card.getSymbol() !=	board.get(roundStarter).getSymbol()) {
			
			if(assignedCards.get(p).contains(board.get(roundStarter).getSymbol())) {
				System.out.println("Zug nicht zulässig. Sie besitzen noch eine spielbare Karte");
				return false;
			}
			else {
				board.put(p, card);
				return true;
			}
		}
		return true;
	}
	
	public void playedCard(User p, Card card) {
		/**
		 * 	Regel: Spieler 1 legt seine Karte und eröffnet die Runde, die anderen Spieler müssen, dasselbe Symbol legen. 
		 * 	Ansonsten einen Trumpf oder eine Fehlfarbe.
		 */
		if(board.isEmpty()) {
			roundStarter=p;
			board.put(p, card);			
		}
		else {
			while(!validPlay(p, card)) {
				System.out.println("Unzulässiger Zug!");
			}
			board.put(p, card);
		}	
	}
	
	

}
