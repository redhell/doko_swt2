package game;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import entities.Card;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CardDeck {

	private HBox pane;

	public CardDeck() {
		pane = new HBox(5);
	}

	public void addCard(Card card) {
		
		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				Node to_remove = null;
				
				for(Node n : pane.getChildren()){
					if(n instanceof Label){
						to_remove = n;
						break;
					}
				}
				
				pane.getChildren().remove(to_remove);
				
				FileInputStream input;
				try {
					input = new FileInputStream(card.getPath());
					Image image = new Image(input);
					ImageView imageView = new ImageView(image);
					pane.getChildren().add(imageView);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

	}

	public Node getPane() {
		return pane;
	}

	public void updateRoundWinner(String roundWinner) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				pane.getChildren().clear();

				Label roundWonBy = new Label();
				roundWonBy.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
				roundWonBy.setTextFill(Color.GOLD);
				roundWonBy.setText(roundWinner + " won this round");
				pane.getChildren().add(roundWonBy);

			}
		});
	}

}
