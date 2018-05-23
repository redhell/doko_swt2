package game;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import backend.enums.JSONIngameAttributes;
import entities.Card;
import entities.CardE;
import entities.SymbolE;
import entities.WertigkeitE;
import gui.GameScreen;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PlayerField extends PlayerFieldPane{

	private GameScreen gameScreen;

	private List<Card> cards;
	private HBox cardView;
	
	private HBox usernameBox;
	private Label usernameLabel;

	List<ImageView> cardList = new LinkedList<ImageView>();

	private final static double cardScale = 1.25;

	public PlayerField(String username) {
		super(username);
	}
	
	public PlayerField(GameScreen gameScreen,String username) {
		super(username);
		this.gameScreen = gameScreen;
		
		pane = new BorderPane();
		cardView = new HBox(5);
		
		usernameBox = new HBox();
		usernameLabel = new Label(username);
		usernameLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		usernameBox.getChildren().add(usernameLabel);
		usernameBox.setAlignment(Pos.CENTER);
		
		cards = new LinkedList<Card>();
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
			cards.add(card);

			try {
				input = new FileInputStream(card.getPath());
				Image image = new Image(input);
				imageView = new ImageView(image);
				cardList.add(imageView);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}
		
		buildScreen();
	}

	public Node getNode() {
		return pane;
	}

	@Override
	public void buildScreen() {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				cardView.getChildren().addAll(cardList);
				((BorderPane) pane).setTop(usernameBox);
				((BorderPane) pane).setCenter(cardView);
			}
		});
	
	}
	
}
