package game;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TopPlayerField extends PlayerFieldPane {

	private List<ImageView> cardList;

	private HBox cardView;

	private HBox usernameBox;
	private Label usernameLabel;

	public TopPlayerField(String username) {
		super(username);

		pane = new BorderPane();

		cardView = new HBox();
		cardList = new LinkedList<ImageView>();

		usernameBox = new HBox();
		usernameLabel = new Label(username);
		usernameLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		usernameBox.getChildren().add(usernameLabel);
		usernameBox.setAlignment(Pos.CENTER);

		buildScreen();
	}

	@Override
	public void buildScreen() {

		FileInputStream input = null;
		Image image = null;
		try {
			input = new FileInputStream("assets/card_backwards_horizontally.png");
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
				cardView.getChildren().addAll(cardList);
				((BorderPane) pane).setCenter(cardView);
				((BorderPane) pane).setBottom(usernameBox);
			}
		});
	}

}
