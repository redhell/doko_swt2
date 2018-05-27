package game;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LeftPlayerField extends PlayerFieldPane {

	private List<ImageView> cardList;

	private Label usernameLabel;

	public LeftPlayerField(String username) {
		super(username);

		pane = new VBox(5);
		
		usernameLabel = new Label(username);
		usernameLabel.setAlignment(Pos.BOTTOM_CENTER);
		usernameLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		
		cardList = new LinkedList<ImageView>();

		buildScreen();
	}

	@Override
	public void buildScreen() {

		
		FileInputStream input = null;
		Image image = null;
		try {
			input = new FileInputStream("assets/card_backwards_vertically.png");
			image = new Image(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < 10; i++) {
			cardList.add(new ImageView(image));
		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				pane.getChildren().addAll(cardList);
				pane.getChildren().add(usernameLabel);
			}
		});

	}
	
	public void removeCard() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				Node to_remove = null;

				for (Node n : pane.getChildren()) {
					if (n instanceof ImageView) {
						to_remove = n;
						break;
					}
				}

				pane.getChildren().remove(to_remove);
			}
		});
	}

}
