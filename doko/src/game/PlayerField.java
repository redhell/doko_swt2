package game;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

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
import gui.GameScreen;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PlayerField extends PlayerFieldPane {

	private GameScreen gameScreen;
	private GameScreenSync gameScreenSync;
	private ConnectionSocket connectionSocket = ConnectionSocket.getInstance();
	private MoveTimer moveTimer;

	private HBox cardView;

	private HBox usernameBox;
	private Label usernameLabel;

	private Label timeLabel;

	private VBox gameInfoBox;
	private Label gameInfo;
	private Label gameModeLabel;
	private Button playCardButton;
	private Button normalGameButton;
	private Button fleischlosGameButton;
	private Button farbstichGameButton;
	private Button showScore;

	Map<ImageView, Card> cardList = new HashMap<ImageView, Card>();

	private volatile boolean canMove = false;

	private Card currentCardPicked = null;
	private String currentCardID = "-1";

	private boolean gameMode = false;

	public PlayerField(GameScreen gameScreen, GameScreenSync gameScreenSync, String username) {
		super(username);
		this.gameScreen = gameScreen;
		this.gameScreenSync = gameScreenSync;

		pane = new BorderPane();
		cardView = new HBox(5);

		usernameBox = new HBox();
		usernameLabel = new Label(username);
		usernameLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		usernameBox.getChildren().add(usernameLabel);
		usernameBox.setAlignment(Pos.CENTER);

		timeLabel = new Label();
		timeLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

		gameInfoBox = new VBox();
		playCardButton = new Button("play Card");
		normalGameButton = new Button("Normales Spiel");
		fleischlosGameButton = new Button("Fleischlos");
		farbstichGameButton = new Button("Farbstich");
		gameInfo = new Label("");
		gameModeLabel = new Label("");
		showScore = new Button("show Score");

		gameInfoBox.getChildren().add(gameInfo);
		gameInfoBox.getChildren().add(gameModeLabel);
		gameInfoBox.getChildren().add(normalGameButton);
		gameInfoBox.getChildren().add(fleischlosGameButton);
		gameInfoBox.getChildren().add(farbstichGameButton);

	}

	public void setCards(JSONObject jsonCards) {
		JSONArray cardsArr = jsonCards.getJSONArray(JSONIngameAttributes.CARDS.name());
		FileInputStream input;
		ImageView imageView = null;

		for (int i = 0; i < cardsArr.length(); i++) {
			JSONObject temp = cardsArr.getJSONObject(i);

			String wertigkeit = temp.getString(CardE.WERTIGKEIT.name());
			String symbol = temp.getString(CardE.SYMBOL.name());
			boolean trumpf = temp.getBoolean(CardE.TRUMPF.name());
			boolean schweinchen = temp.getBoolean(CardE.SCHWEINCHEN.name());

			Card card = new Card(WertigkeitE.valueOf(wertigkeit), SymbolE.valueOf(symbol), trumpf);
			card.setSchweinchen(schweinchen);

			try {
				input = new FileInputStream(card.getPath());
				Image image = new Image(input);
				imageView = new ImageView(image);
				imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						currentCardPicked = cardList.get(event.getSource());
						currentCardID = ((ImageView) event.getSource()).getId();
						event.consume();
					}
				});
				imageView.setId(i + "");
				cardList.put(imageView, card);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}

		buildScreen();
	}

	@Override
	public void buildScreen() {

		playCardButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if (canMove && moveTimer.hasTime()) {

					JSONObject json = new JSONObject();
					json.put(JSONActionsE.EVENT.name(), JSONEventsE.MAKEMOVE.name());
					json.put(JSONEventsE.MAKEMOVE.name(), JSONIngameAttributes.CARD.name());
					JSONObject jsonCard = new JSONObject();
					jsonCard.put(CardE.WERTIGKEIT.name(), currentCardPicked.getWertigkeit());
					jsonCard.put(CardE.SYMBOL.name(), currentCardPicked.getSymbol());
					jsonCard.put(CardE.TRUMPF.name(), currentCardPicked.isTrumpf());

					json.put(JSONIngameAttributes.CARD.name(), jsonCard);

					connectionSocket.sendMessage(json.toString());

					canMove = false;
				}

			}
		});

		normalGameButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if (gameMode) {

					JSONObject json = new JSONObject();
					json.put(JSONActionsE.EVENT.name(), JSONEventsE.GETGAMEMODE.name());
					json.put(JSONEventsE.GETGAMEMODE.name(), GamemodeE.NORMAL.name());

					connectionSocket.sendMessage(json.toString());

					gameMode = false;
				}

			}
		});

		fleischlosGameButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if (gameMode) {

					JSONObject json = new JSONObject();
					json.put(JSONActionsE.EVENT.name(), JSONEventsE.GETGAMEMODE.name());
					json.put(JSONEventsE.GETGAMEMODE.name(), GamemodeE.FLEISCHLOS.name());

					connectionSocket.sendMessage(json.toString());

					gameMode = false;
				}

			}
		});

		farbstichGameButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if (gameMode) {

					JSONObject json = new JSONObject();
					json.put(JSONActionsE.EVENT.name(), JSONEventsE.GETGAMEMODE.name());
					json.put(JSONEventsE.GETGAMEMODE.name(), GamemodeE.FARBSTICH.name());

					connectionSocket.sendMessage(json.toString());

					gameMode = false;
				}

			}
		});

		showScore.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(!canMove){
					JSONObject json = new JSONObject();
					json.put(JSONActionsE.EVENT.name(), JSONEventsE.SHOWSCORE.name());

					connectionSocket.sendMessage(json.toString());

				}

			}
		});
		
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				cardView.getChildren().addAll(cardList.keySet());
				((BorderPane) pane).setTop(usernameBox);
				((BorderPane) pane).setCenter(cardView);
				((BorderPane) pane).setLeft(timeLabel);
				((BorderPane) pane).setRight(gameInfoBox);
			}

		});

	}

	public void makeMove(JSONIngameAttributes attribute, Card card) {

		String setText = "";

		if (attribute == JSONIngameAttributes.GETMOVE) {
			moveTimer = new MoveTimer(this, timeLabel);
			moveTimer.start();
			canMove = true;
			gameInfo.setTextFill(Color.GREEN);
			setText = "PICK A CARD";
		} else if (attribute == JSONIngameAttributes.INVALID) {
			canMove = true;
			gameInfo.setTextFill(Color.RED);
			setText = "CANNOT PLAY THAT CARD";
		} else if (attribute == JSONIngameAttributes.VALID) {
			canMove = false;
			moveTimer.done();
			moveTimer.stop();
			gameInfo.setTextFill(Color.GREEN);
			setText = "CARD ACCEPTED: " + card;
			gameScreenSync.updateField(card, connectionSocket.getUsername());
		} else if (attribute == JSONIngameAttributes.TIMEEXPIRED) {
			canMove = false;
			moveTimer.done();
			moveTimer.stop();
			gameInfo.setTextFill(Color.GREEN);
			setText = "TIME EXPIRED;\nRANDOM CARD PICKED: " + card;
			gameScreenSync.updateField(card, connectionSocket.getUsername());
		}

		final String infoBoxTextUpdate = setText;

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				gameInfo.setText(infoBoxTextUpdate);

			}

		});

	}

	public void timeExpired() {

		canMove = false;

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				timeLabel.setTextFill(Color.RED);
				timeLabel.setText("timer expired!");
				gameInfo.setTextFill(Color.RED);
				gameInfo.setText("Timer expired!");

				JSONObject json = new JSONObject();
				json.put(JSONActionsE.EVENT.name(), JSONEventsE.MAKEMOVE.name());
				json.put(JSONEventsE.MAKEMOVE.name(), JSONIngameAttributes.TIMEEXPIRED.name());

				connectionSocket.sendMessage(json.toString());

			}
		});

	}

	public Node getNode() {
		return pane;
	}

	public void removeCardFromDeck(Card to_remove, JSONIngameAttributes attribute) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				Node hasToBeRemoved = null;

				if (attribute == JSONIngameAttributes.VALID) {

					for (Node image : cardView.getChildren()) {

						ImageView temp = (ImageView) image;
						if (temp.getId().equals(currentCardID)) {
							hasToBeRemoved = temp;
							break;
						}

					}

					cardView.getChildren().remove(hasToBeRemoved);
					return;

				} else if (attribute == JSONIngameAttributes.TIMEEXPIRED) {

					for (Node image : cardView.getChildren()) {

						ImageView temp = (ImageView) image;
						if (cardList.get(temp).equals(to_remove)) {
							hasToBeRemoved = image;
							break;
						}

					}

					cardView.getChildren().remove(hasToBeRemoved);

				}

			}
		});

	}

	public void setGameMode(GamemodeE mode) {

		String text = "";

		if (mode == null) {
			gameMode = true;
			return;
		} else if (mode == GamemodeE.NORMAL) {
			text = "GAMEMODE: NORMAL";
		} else if (mode == GamemodeE.FLEISCHLOS) {
			text = "GAMEMODE: FLEISCHLOS";
		} else if (mode == GamemodeE.FARBSTICH) {
			text = "GAMEMODE: FARBSTICH";
		}

		final String gameModeLabelText = text;

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				gameModeLabel.setText(gameModeLabelText);

				gameInfoBox.getChildren().clear();

				gameInfoBox.getChildren().add(gameInfo);
				gameInfoBox.getChildren().add(gameModeLabel);
				gameInfoBox.getChildren().add(playCardButton);
				gameInfoBox.getChildren().add(showScore);

			}
		});

	}

	public void showCurrentScore(String currentScore) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				JSONObject json = new JSONObject(currentScore);


				Stage dialog = new Stage();
				HBox hboxTemp = new HBox();
				hboxTemp.getChildren().add(new Label(currentScore));
				Scene scene = new Scene(hboxTemp);
				
				// populate dialog with controls.
				dialog.setScene(scene);

				dialog.initOwner(gameScreen.getGui().getStage());
				dialog.initModality(Modality.APPLICATION_MODAL);
				dialog.showAndWait();
			}

		});
	}

}
