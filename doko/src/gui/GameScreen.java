package gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import backend.ConnectionSocket;
import backend.enums.JSONActionsE;
import backend.enums.JSONEventsE;
import backend.enums.JSONIngameAttributes;
import entities.Card;
import entities.SymbolE;
import entities.WertigkeitE;
import game.CardManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class GameScreen implements GuiScreen, Runnable {

	private Gui gui;
	private Pane pane;

	private ConnectionSocket connectionSocket;

	//private CardManager cardManager;

	public GameScreen(Gui gui) {
		this.gui = gui;

		connectionSocket = ConnectionSocket.getInstance();

		//cardManager = new CardManager();

		buildScreen();
	}

	@Override
	public void run() {

		getCards();
		while (true)
			nextAction();

	}

	private void nextAction() {

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void getCards() {
		System.out.println("getCards");
		String cards = connectionSocket.readMessage();
		System.out.println("afterGetCards");
		System.out.println("cards: " + cards);
		if (cards == null)
			return;

		JSONObject json = new JSONObject(cards);
		if (json.getString(JSONActionsE.EVENT.name()).equals(JSONEventsE.SHUFFLE.name())) {
			System.out.println(json);
			//cardManager.setCards(json);

			return;
		}

	}

	private void buildScreen() {

		pane = new BorderPane();

		VBox asdf = new VBox();
		ObservableList<String> users;
		ListView<String> userList;

		users = FXCollections.observableArrayList();
		users.add("asdf");
		users.add("asdf");
		users.add("asdf");
		users.add("asdf");
		userList = new ListView<>(users);

		asdf.getChildren().add(userList);
		
		((BorderPane) pane).setRight(asdf);
		//((BorderPane) pane).setBottom(cardManager.getNode());
		System.out.println("done building screen");

	}

	@Override
	public Pane getScreen() {
		return pane;
	}

}
