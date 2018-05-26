package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import backend.enums.JSONIngameAttributes;
import entities.Card;
import gui.GameScreen;
import javafx.application.Platform;
import javafx.scene.Node;

public class GameScreenSync {

	private GameScreen gameScreen;

	private TopPlayerField topPlayer;
	private RightPlayerField rightPlayer;
	private LeftPlayerField leftPlayer;
	
	private CardDeck cardDeck;

	private String username;

	public GameScreenSync(GameScreen gameScreen, String username) {
		this.gameScreen = gameScreen;
		this.username = username;
	}

	public void buildScreen(JSONObject json) {

		Map<String, String> orderMap = new HashMap<String, String>();

		JSONObject orders = (JSONObject) json.get(JSONIngameAttributes.ORDER.name());
		for (int i = 0; i < orders.length(); i++) {
			// key:order value:username
			orderMap.put((i + 1) + "", orders.getString((i + 1) + ""));
		}

		int clientIndex = -1;

		List<String> keys = new ArrayList<String>(orderMap.keySet());
		Collections.sort(keys);

		for (String s : keys) {
			if (orderMap.get(s).equals(username)) {
				clientIndex = Integer.parseInt(s);
				break;
			}
		}

		if (clientIndex == 1) {

			leftPlayer = new LeftPlayerField(orderMap.get(keys.get(1)));
			topPlayer = new TopPlayerField(orderMap.get(keys.get(2)));
			rightPlayer = new RightPlayerField(orderMap.get(keys.get(3)));

		} else if (clientIndex == 2) {

			leftPlayer = new LeftPlayerField(orderMap.get(keys.get(2)));
			topPlayer = new TopPlayerField(orderMap.get(keys.get(3)));
			rightPlayer = new RightPlayerField(orderMap.get(keys.get(0)));

		} else if (clientIndex == 3) {

			leftPlayer = new LeftPlayerField(orderMap.get(keys.get(3)));
			topPlayer = new TopPlayerField(orderMap.get(keys.get(0)));
			rightPlayer = new RightPlayerField(orderMap.get(keys.get(1)));

		} else if (clientIndex == 4) {

			leftPlayer = new LeftPlayerField(orderMap.get(keys.get(0)));
			topPlayer = new TopPlayerField(orderMap.get(keys.get(1)));
			rightPlayer = new RightPlayerField(orderMap.get(keys.get(2)));

		}
		
		cardDeck = new CardDeck();

	}

	public Node getTopPlayer() {
		return topPlayer.getPane();
	}

	public Node getRightPlayer() {
		return rightPlayer.getPane();
	}

	public Node getLeftPlayer() {
		return leftPlayer.getPane();
	}
	
	public Node getCardDeck(){
		return cardDeck.getPane();
	}

	public void updateField(Card card, String playedBy) {
		
		//delete card from enemy player
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				if(leftPlayer.getUsername().equals(playedBy)){
					leftPlayer.removeCard();
				}else if(topPlayer.getUsername().equals(playedBy)){
					topPlayer.removeCard();
				}else if(rightPlayer.getUsername().equals(playedBy)){
					rightPlayer.removeCard();
				} 
				
			}
		});
		
		
		//update deck
		cardDeck.addCard(card);
	}

	public void updateRoundWinner(String roundWinner) {
		
		cardDeck.updateRoundWinner(roundWinner);
		
	}

}
