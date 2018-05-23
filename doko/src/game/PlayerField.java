package game;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import backend.ConnectionSocket;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PlayerField extends PlayerFieldPane {

	private GameScreen gameScreen;
	private ConnectionSocket connectionSocket = ConnectionSocket.getInstance();

	private HBox cardView;

	private HBox usernameBox;
	private Label usernameLabel;

	private Label timeLabel;
	private Button playCardButton;

	Map<ImageView, Card> cardList = new HashMap<ImageView, Card>();

	private final static double cardScale = 1.25;

	private volatile boolean canMove = false;

	private Card currentCardPicked = null;

	public PlayerField(GameScreen gameScreen, String username) {
		super(username);
		this.gameScreen = gameScreen;

		pane = new BorderPane();
		cardView = new HBox(5);

		usernameBox = new HBox();
		usernameLabel = new Label(username);
		usernameLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		usernameBox.getChildren().add(usernameLabel);
		usernameBox.setAlignment(Pos.CENTER);

		timeLabel = new Label("Time left:");
		playCardButton = new Button("play Card");

	}

	public void setCards(JSONObject jsonCards) {
		JSONArray cardsArr = jsonCards.getJSONArray(JSONIngameAttributes.CARDS.name());
		FileInputStream input;
		ImageView imageView = null;

		for (int i = 0; i < cardsArr.length(); i++) {
			JSONObject temp = cardsArr.getJSONObject(i);

			String wertigkeit = temp.getString(CardE.WERTIGKEIT.name());
			String symbol = temp.getString(CardE.SYMBOL.name());

			Card card = new Card(WertigkeitE.valueOf(wertigkeit), SymbolE.valueOf(symbol));

			try {
				input = new FileInputStream(card.getPath());
				Image image = new Image(input);
				imageView = new ImageView(image);
				imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						currentCardPicked = cardList.get(event.getSource());
						event.consume();
					}
				});
				cardList.put(imageView, card);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}

		buildScreen();
	}

	public Node getNode() {
		return pane;
	}

	public void updateTime(String time) {

		timeLabel.setText("Time left: " + time);

	}

	@Override
	public void buildScreen() {

		playCardButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if (canMove) {

					gameScreen.makeMove(currentCardPicked);

				}

				canMove = false;

			}
		});

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				cardView.getChildren().addAll(cardList.keySet());
				((BorderPane) pane).setTop(usernameBox);
				((BorderPane) pane).setCenter(cardView);
				((BorderPane) pane).setLeft(timeLabel);
				((BorderPane) pane).setRight(playCardButton);
			}

		});

	}

	public void makeMove() {

		canMove = true;

	}

}
