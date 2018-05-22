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
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class CardManager {

	private HBox cardView;
	private List<Card> cards;

	public CardManager() {
		cardView = new HBox(5);
		cardView.setPadding((new Insets(15, 15, 15, 15)));

		cards = new LinkedList<Card>();
	}

	public void setCards(JSONObject jsonCards) {
		JSONArray cardsArr = jsonCards.getJSONArray(JSONIngameAttributes.CARDS.name());
		FileInputStream input;
		ImageView imageView = null;
		List<ImageView> temp_list = new LinkedList<ImageView>();

		for (int i = 0; i < cardsArr.length(); i++) {
			JSONObject temp = cardsArr.getJSONObject(i);
			System.out.println(temp.toString());

			String wertigkeit = temp.getString(CardE.WERTIGKEIT.name());
			String symbol = temp.getString(CardE.SYMBOL.name());
			System.out.println("WERTIGKEIT: " + wertigkeit);
			System.out.println("SYMBOL: " + symbol);

			System.out.println("VALUEOF WERTIGKEIT: " + WertigkeitE.valueOf(wertigkeit));
			System.out.println("VALUEOF Symbol: " + SymbolE.valueOf(symbol));

			Card card = new Card(WertigkeitE.valueOf(wertigkeit), SymbolE.valueOf(symbol));
			cards.add(card);

			try {
				input = new FileInputStream(card.getPath());
				Image image = new Image(input);
				imageView = new ImageView(image);
				temp_list.add(imageView);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				cardView.getChildren().addAll(temp_list);
			}
		});
	}

	public Node getNode() {
		return cardView;
	}

}
