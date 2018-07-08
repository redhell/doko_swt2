package gui;

import org.json.JSONArray;
import org.json.JSONObject;

import backend.ConnectionSocket;
import backend.enums.GamemodeE;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class GameScreen implements GuiScreen, Runnable {

	private Gui gui;
	private Pane pane;

	private ConnectionSocket connectionSocket;

	private PlayerField playerField;
	private GameScreenSync gameScreenSync;

	//sec
	public static final int ROUNDTIMER = 45;

	public GameScreen(Gui gui) {
		this.gui = gui;

		gui.setWidth(Gui.GAMESCREEN_WIDTH);
		gui.setHeight(Gui.GAMESCREEN_HEIGHT);

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

				playerField.makeMove(JSONIngameAttributes.GETMOVE, null);

			} else if (json.getString(JSONEventsE.MAKEMOVE.name()).equals(JSONIngameAttributes.INVALID.name())) {

				playerField.makeMove(JSONIngameAttributes.INVALID, null);

			} else if (json.getString(JSONEventsE.MAKEMOVE.name()).equals(JSONIngameAttributes.VALID.name())) {

				JSONObject jsonCard = (JSONObject) json.get(JSONIngameAttributes.CARD.name());
				WertigkeitE wertigkeit = WertigkeitE.valueOf(jsonCard.get(CardE.WERTIGKEIT.name()).toString());
				SymbolE symbol = SymbolE.valueOf(jsonCard.get(CardE.SYMBOL.name()).toString());
				boolean trumpf = jsonCard.getBoolean(CardE.TRUMPF.name());

				Card parsedCard = new Card(wertigkeit, symbol, trumpf);

				playerField.makeMove(JSONIngameAttributes.VALID, parsedCard);
				playerField.removeCardFromDeck(parsedCard, JSONIngameAttributes.VALID);

			} else if (json.getString(JSONEventsE.MAKEMOVE.name()).equals(JSONIngameAttributes.TIMEEXPIRED.name())) {

				JSONObject jsonCard = (JSONObject) json.get(JSONIngameAttributes.CARD.name());
				WertigkeitE wertigkeit = WertigkeitE.valueOf(jsonCard.get(CardE.WERTIGKEIT.name()).toString());
				SymbolE symbol = SymbolE.valueOf(jsonCard.get(CardE.SYMBOL.name()).toString());
				boolean trumpf = jsonCard.getBoolean(CardE.TRUMPF.name());

				Card parsedCard = new Card(wertigkeit, symbol, trumpf);

				playerField.makeMove(JSONIngameAttributes.TIMEEXPIRED, parsedCard);
				playerField.removeCardFromDeck(parsedCard, JSONIngameAttributes.TIMEEXPIRED);

			}

			return;

		} else if (json.getString(JSONActionsE.EVENT.name()).equals(JSONEventsE.CARDBROADCAST.name())) {

			JSONObject jsonCard = (JSONObject) json.get(JSONIngameAttributes.CARD.name());
			WertigkeitE wertigkeit = WertigkeitE.valueOf(jsonCard.get(CardE.WERTIGKEIT.name()).toString());
			SymbolE symbol = SymbolE.valueOf(jsonCard.get(CardE.SYMBOL.name()).toString());
			boolean trumpf = jsonCard.getBoolean(CardE.TRUMPF.name());

			Card parsedCard = new Card(wertigkeit, symbol, trumpf);

			String playedBy = json.getString(JSONIngameAttributes.PLAYEDBY.name());

			gameScreenSync.updateField(parsedCard, playedBy);

			return;

		} else if (json.getString(JSONActionsE.EVENT.name()).equals(JSONEventsE.ROUNDWINNER.name())) {

			String roundWinner = json.getString(JSONEventsE.ROUNDWINNER.name());

			gameScreenSync.updateRoundWinner(roundWinner);

		} else if (json.getString(JSONActionsE.EVENT.name()).equals(JSONEventsE.GETGAMEMODE.name())) {

			if (json.has(JSONEventsE.GETGAMEMODE.name())) {

				if (json.getString(JSONEventsE.GETGAMEMODE.name()).equals(GamemodeE.NORMAL.name())) {
					playerField.setGameMode(GamemodeE.NORMAL);
				} else if (json.getString(JSONEventsE.GETGAMEMODE.name()).equals(GamemodeE.FLEISCHLOS.name())) {
					playerField.setGameMode(GamemodeE.FLEISCHLOS);
				} else if (json.getString(JSONEventsE.GETGAMEMODE.name()).equals(GamemodeE.FARBSTICH.name())) {
					playerField.setGameMode(GamemodeE.FARBSTICH);
				}
				

			} else {

				playerField.setGameMode(null);

			}

		} else if (json.getString(JSONActionsE.EVENT.name()).equals(JSONEventsE.GAMEWINNER.name())) {

			JSONArray jsonWinner = json.getJSONArray(JSONEventsE.GAMEWINNER.name());
			
			for(int i=0;i<jsonWinner.length();i++){
				System.out.println(((JSONObject)jsonWinner.get(i)).get("player") + " : " + ((JSONObject)jsonWinner.get(i)).get("score"));
			}
			
			
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
