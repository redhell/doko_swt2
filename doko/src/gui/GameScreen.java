package gui;

import org.json.JSONObject;

import backend.ConnectionSocket;
import backend.enums.JSONActionsE;
import backend.enums.JSONEventsE;
import backend.enums.JSONIngameAttributes;
import entities.Card;
import entities.CardE;
import entities.SymbolE;
import entities.WertigkeitE;
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

	public static final int ROUNDTIMER = 10;

	public GameScreen(Gui gui) {
		this.gui = gui;

		connectionSocket = ConnectionSocket.getInstance();

		gameScreenSync = new GameScreenSync(this, connectionSocket.getUsername());
		playerField = new PlayerField(this, gameScreenSync, connectionSocket.getUsername());

		pane = new BorderPane();
	}

	@Override
	public void run() {

		getCards();
		getOrder();
		buildScreen();
		while (true)
			nextAction();

	}

	private void nextAction() {

		String jsonString = connectionSocket.readMessage();

		if (jsonString == null)
			return;

		JSONObject json = new JSONObject(jsonString);

		if (json.getString(JSONActionsE.EVENT.name()).equals(JSONEventsE.MAKEMOVE.name())) {

			if (json.getString(JSONEventsE.MAKEMOVE.name()).equals(JSONIngameAttributes.GETMOVE.name())) {

				System.out.println("GETTING MOVE");
				playerField.makeMove(JSONIngameAttributes.GETMOVE, null);

			} else if (json.getString(JSONEventsE.MAKEMOVE.name()).equals(JSONIngameAttributes.INVALID.name())) {

				System.out.println("INVALID");
				playerField.makeMove(JSONIngameAttributes.INVALID, null);

			} else if (json.getString(JSONEventsE.MAKEMOVE.name()).equals(JSONIngameAttributes.VALID.name())) {

				System.out.println("VALID");

				JSONObject jsonCard = (JSONObject) json.get(JSONIngameAttributes.CARD.name());
				WertigkeitE wertigkeit = WertigkeitE.valueOf(jsonCard.get(CardE.WERTIGKEIT.name()).toString());
				SymbolE symbol = SymbolE.valueOf(jsonCard.get(CardE.SYMBOL.name()).toString());

				Card parsedCard = new Card(wertigkeit, symbol);

				playerField.makeMove(JSONIngameAttributes.VALID, parsedCard);
				playerField.removeCardFromDeck(parsedCard, JSONIngameAttributes.VALID);

			} else if (json.getString(JSONEventsE.MAKEMOVE.name()).equals(JSONIngameAttributes.TIMEEXPIRED.name())) {

				System.out.println("TIMEXPIRED");

				JSONObject jsonCard = (JSONObject) json.get(JSONIngameAttributes.CARD.name());
				WertigkeitE wertigkeit = WertigkeitE.valueOf(jsonCard.get(CardE.WERTIGKEIT.name()).toString());
				SymbolE symbol = SymbolE.valueOf(jsonCard.get(CardE.SYMBOL.name()).toString());

				Card parsedCard = new Card(wertigkeit, symbol);

				playerField.makeMove(JSONIngameAttributes.TIMEEXPIRED, parsedCard);
				playerField.removeCardFromDeck(parsedCard, JSONIngameAttributes.TIMEEXPIRED);

			}

			return;

		} else if (json.getString(JSONActionsE.EVENT.name()).equals(JSONEventsE.CARDBROADCAST.name())) {

			System.out.println("client received broadcast");

			JSONObject jsonCard = (JSONObject) json.get(JSONIngameAttributes.CARD.name());
			WertigkeitE wertigkeit = WertigkeitE.valueOf(jsonCard.get(CardE.WERTIGKEIT.name()).toString());
			SymbolE symbol = SymbolE.valueOf(jsonCard.get(CardE.SYMBOL.name()).toString());

			Card parsedCard = new Card(wertigkeit, symbol);

			String playedBy = json.getString(JSONIngameAttributes.PLAYEDBY.name());

			gameScreenSync.updateField(parsedCard, playedBy);

			return;
	
		} else if (json.getString(JSONActionsE.EVENT.name()).equals(JSONEventsE.ROUNDWINNER.name())) {

			String roundWinner = json.getString(JSONEventsE.ROUNDWINNER.name());

			System.out.println("got roundwinner: " + roundWinner);
			
			gameScreenSync.updateRoundWinner(roundWinner);

		}

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
				((BorderPane) pane).setCenter(gameScreenSync.getCardDeck());

			}

		});

	}

	@Override
	public Pane getScreen() {
		return pane;
	}

}
