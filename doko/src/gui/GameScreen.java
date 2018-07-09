package gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameScreen implements GuiScreen, Runnable {

	private Gui gui;
	private Pane pane;

	private ConnectionSocket connectionSocket;

	private PlayerField playerField;
	private GameScreenSync gameScreenSync;

	// sec
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

			JSONObject jsonObj = json.getJSONObject(JSONEventsE.GAMEWINNER.name());

			JSONObject winnerObj = jsonObj.getJSONObject("winner");
			JSONObject loserObj = jsonObj.getJSONObject("loser");

			gameFinished(winnerObj, loserObj);

		} else if (json.getString(JSONActionsE.EVENT.name()).equals(JSONEventsE.SHOWSCORE.name())) {
			playerField.showCurrentScore(json.toString());
		}

	}

	private void gameFinished(JSONObject winnerObj, JSONObject loserObj) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				pane.getChildren().clear();
				try {
					
					Image image = new Image(new FileInputStream("assets/game_finish_background.png"));
				
					BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, 
							BackgroundPosition.CENTER, new BackgroundSize(Gui.window_width, Gui.window_height, false, false, false, false));
					pane = new GridPane();
					((GridPane) pane).setAlignment(Pos.CENTER_LEFT);
					((GridPane) pane).setHgap(10);
					((GridPane) pane).setVgap(10);
					pane.setPadding(new Insets(25, 25, 25, 25));
					pane.setBackground(new Background(backgroundImage));
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				VBox vbox = new VBox();
				Label l1 = new Label("WINNER:");
				l1.setTextFill(Color.WHITE);
				l1.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
				
				vbox.getChildren().add(l1);
				
				JSONArray winnerArr = winnerObj.getJSONArray("player");
				
				for(int i=0;i<winnerArr.length();i++){
					Label tempWinner = new Label("Player: " + winnerArr.getString(i));
					tempWinner.setTextFill(Color.WHITE);
					tempWinner.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
					vbox.getChildren().add(tempWinner);
				}
				Label scoreL1 = new Label(winnerObj.getString("score"));
				l1.setTextFill(Color.WHITE);
				l1.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
				vbox.getChildren().add(scoreL1);
				
				
				Label seperatorL = new Label("________________");
				l1.setTextFill(Color.WHITE);
				l1.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
				vbox.getChildren().add(seperatorL);
				
				Label l2 = new Label("LOSER:");
				l2.setTextFill(Color.WHITE);
				l2.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
				vbox.getChildren().add(l2);				
				

				JSONArray loserArr = loserObj.getJSONArray("player");
			
				for(int i=0;i<loserArr.length();i++){
					Label tempLoser = new Label("Player: " + loserArr.getString(i));
					tempLoser.setTextFill(Color.WHITE);
					tempLoser.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
					vbox.getChildren().add(tempLoser);
				}
				Label scoreL2 = new Label(winnerObj.getString("score"));
				l2.setTextFill(Color.WHITE);
				l2.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
				vbox.getChildren().add(scoreL2);
				
				
				((GridPane) pane).add(vbox, 15, 0, 2, 1);
				
				gui.changeScene(pane);
				
			}

		});

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

	public Gui getGui() {
		return gui;
	}

}
