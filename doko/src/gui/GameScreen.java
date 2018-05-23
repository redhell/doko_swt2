package gui;

import org.json.JSONObject;

import backend.ConnectionSocket;
import backend.enums.JSONActionsE;
import backend.enums.JSONEventsE;
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

	public GameScreen(Gui gui) {
		this.gui = gui;

		connectionSocket = ConnectionSocket.getInstance();

		playerField = new PlayerField(this,connectionSocket.getUsername());
		gameScreenSync = new GameScreenSync(this, connectionSocket.getUsername());

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

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
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

				((BorderPane) pane).setTop(gameScreenSync.getTopPlayer());
				((BorderPane) pane).setRight(gameScreenSync.getRightPlayer());
				((BorderPane) pane).setLeft(gameScreenSync.getLeftPlayer());
				((BorderPane) pane).setBottom(playerField.getNode());

			}

		});

	}

	@Override
	public Pane getScreen() {
		return pane;
	}

}
