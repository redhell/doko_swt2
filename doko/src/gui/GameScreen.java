package gui;

import org.json.JSONObject;

import backend.ConnectionSocket;
import backend.enums.JSONActionsE;
import backend.enums.JSONEventsE;
import backend.enums.JSONIngameAttributes;
import entities.Card;
import entities.CardE;
import game.GameScreenSync;
import game.PlayerField;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class GameScreen implements GuiScreen, Runnable {

	private Gui gui;
	private Pane pane;

	private ConnectionSocket connectionSocket;

	private PlayerField playerField;
	private GameScreenSync gameScreenSync;

	public GameScreen(Gui gui) {
		this.gui = gui;

		connectionSocket = ConnectionSocket.getInstance();

		playerField = new PlayerField(this, connectionSocket.getUsername());
		gameScreenSync = new GameScreenSync(this, connectionSocket.getUsername());

		pane = new BorderPane();
	}

	@Override
	public void run() {

		flushSocket();
		getCards();
		getOrder();
		buildScreen();
		while (true)
			nextAction();

	}

	private void flushSocket() {
		
		JSONObject json = new JSONObject();
		json.put(JSONActionsE.EVENT.name(), JSONEventsE.FLUSH.name());
		
		for(int i=0;i<3;i++){
			connectionSocket.sendMessage(json.toString());			
		}
		
	}

	private void nextAction() {

		String jsonString = connectionSocket.readMessage();

		if (jsonString == null)
			return;

		JSONObject json = new JSONObject(jsonString);

		if (json.getString(JSONActionsE.EVENT.name()).equals(JSONEventsE.MAKEMOVE.name())) {

			if (json.getString(JSONEventsE.MAKEMOVE.name()).equals(JSONIngameAttributes.TIMELEFT.name())) {

				playerField.updateTime(json.getString(JSONIngameAttributes.TIMELEFT.name()));

			} else {

				System.out.println("make move");
				playerField.makeMove();

			}

			return;
		}

	}
	
	public void makeMove(Card card){
		JSONObject json = new JSONObject();
		json.put(JSONActionsE.EVENT.name(), JSONEventsE.MAKEMOVE.name());
		JSONObject jsonCard = new JSONObject();
		jsonCard.put(CardE.WERTIGKEIT.name(), card.getWertigkeit());
		jsonCard.put(CardE.SYMBOL.name(), card.getSymbol());
		json.put(JSONIngameAttributes.CARD.name(), jsonCard);

		connectionSocket.sendMessage(json.toString());

	}

	private void getCards() {
		String cards = connectionSocket.readMessage();
		if (cards == null)
			return;

		JSONObject json = new JSONObject(cards);
		if (json.getString(JSONActionsE.EVENT.name()).equals(JSONEventsE.SHUFFLE.name())) {
			playerField.setCards(json);
		}

	}

	private void getOrder() {
		String order = connectionSocket.readMessage();
		if (order == null)
			return;

		JSONObject json = new JSONObject(order);
		if (json.getString(JSONActionsE.EVENT.name()).equals(JSONEventsE.MOVEORDER.name())) {
			gameScreenSync.buildScreen(json);
		}

	}

	private void buildScreen() {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				((BorderPane) pane).setBottom(playerField.getNode());
				((BorderPane) pane).setTop(gameScreenSync.getTopPlayer());
				((BorderPane) pane).setRight(gameScreenSync.getRightPlayer());
				((BorderPane) pane).setLeft(gameScreenSync.getLeftPlayer());
				((BorderPane) pane).setCenter(new Label("CARDS"));

			}

		});

	}

	@Override
	public Pane getScreen() {
		return pane;
	}

}
