package gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import backend.ConnectionSocket;
import backend.enums.JSONActionsE;
import backend.enums.JSONEventsE;
import backend.enums.JSONLobbyAttributes;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class LobbyScreen implements GuiScreen, Runnable {

	private Gui gui;

	private Pane pane;

	private ObservableList<String> users;
	private ListView<String> userList;

	private Label amount_games;
	private Label amount_wins;
	private Label amount_lost;
	private Label amount_score;

	private Label amount_queue;
	
	private Image image;
	private BackgroundImage bi;

	private ConnectionSocket connectionSocket;

	private volatile boolean gameStarted = false;

	public LobbyScreen(Gui gui) {
		this.gui = gui;

		users = FXCollections.observableArrayList();
		userList = new ListView<>(users);

		connectionSocket = ConnectionSocket.getInstance();
		
		try {
			image = new Image(new FileInputStream("assets/lobby.png"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bi = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
				BackgroundPosition.CENTER, new BackgroundSize(480, 360, false, false, false, false));

		buildScreen();
	}

	@Override
	public void run() {

		updateData();
		while (!gameStarted)
			getNextAction();

	}

	private void getNextAction() {

		String jsonString = connectionSocket.readMessage();

		if (jsonString == null)
			return;

		JSONObject json = new JSONObject(jsonString);

		if (json.getString(JSONActionsE.EVENT.name()).equals(JSONEventsE.USERLIST.name())) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					updateUserList(json);
				}
			});

			return;
		}
		if (json.getString(JSONActionsE.EVENT.name()).equals(JSONEventsE.QUEUENUMBER.name())) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					amount_queue.setText("In Queue: " + json.getString(JSONLobbyAttributes.NEWQUEUENUMBER.name()));
				}
			});

			return;
		}
		if (json.getString(JSONActionsE.EVENT.name()).equals(JSONEventsE.FAIL.name())) {

			if (json.getString(JSONEventsE.FAIL.name()).equals(JSONLobbyAttributes.ALREADYQUEUED.name())) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						Alert alert = new Alert(AlertType.CONFIRMATION, "You are already in the Queue");
						alert.showAndWait();
					}
				});

				return;
			}

		}
		if (json.getString(JSONActionsE.EVENT.name()).equals(JSONEventsE.STATISTICS.name())) {
			JSONArray statisticArr = json.getJSONArray(JSONLobbyAttributes.USERSTATISTIC.name());

			JSONObject games = statisticArr.getJSONObject(0);
			JSONObject wins = statisticArr.getJSONObject(1);
			JSONObject lost = statisticArr.getJSONObject(2);
			JSONObject score = statisticArr.getJSONObject(3);

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					amount_games.setText("#Games: " + games.getString("games"));
					amount_wins.setText("#Wins: " + wins.getString("wins"));
					amount_lost.setText("#Lost: " + lost.getString("lost"));
					amount_score.setText("#Score: " + score.getString("score"));
				}
			});

			return;
		}
		if (json.getString(JSONActionsE.EVENT.name()).equals(JSONEventsE.GAMESTART.name())) {
			gameStarted = true;
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					gui.changeToGame();
				}
			});

			return;
		}
		if (json.getString(JSONActionsE.EVENT.name()).equals(JSONEventsE.FLUSH.name())) {

			flush();
			flush();

			return;
		}

	}

	private void buildScreen() {

		amount_games = new Label("Games: ");
		amount_games.setFont(Font.font(16));
		amount_games.setTextFill(javafx.scene.paint.Color.WHEAT);
		amount_wins = new Label("Wins: ");
		amount_wins.setFont(Font.font(16));
		amount_wins.setTextFill(javafx.scene.paint.Color.WHEAT);
		amount_lost = new Label("Lost: ");
		amount_lost.setFont(Font.font(16));
		amount_lost.setTextFill(javafx.scene.paint.Color.WHEAT);
		amount_score = new Label("Score: ");
		amount_score.setFont(Font.font(16));
		amount_score.setTextFill(javafx.scene.paint.Color.WHEAT);

		VBox statistics = new VBox();
		statistics.setPadding(new Insets(10, 50, 50, 10));
		
		statistics.getChildren().add(amount_games);
		statistics.getChildren().add(amount_wins);
		statistics.getChildren().add(amount_lost);
		statistics.getChildren().add(amount_score);

		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10,10,10,10));
		vbox.setSpacing(10);
		
		amount_queue = new Label("In Queue: ");
		amount_queue.setFont(Font.font(16));
		amount_queue.setTextFill(javafx.scene.paint.Color.WHEAT);
		Button startGame_button = new Button("Start Game");
		startGame_button.setPadding(new Insets(10, 10, 10, 10));
		startGame_button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				JSONObject json = new JSONObject();
				json.put(JSONActionsE.EVENT.name(), JSONEventsE.QUEUEFORGAME.name());
				connectionSocket.sendMessage(json.toString());
			}
		});

		Button logout_button = new Button("Log Out");
		logout_button.setPadding(new Insets(10, 10, 10,10));
		logout_button.setOnAction(new EventHandler<ActionEvent>() {	
			
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		HBox hb = new HBox();
		hb.setPadding(new Insets(10, 10, 10, 0));
		hb.setSpacing(10);
		
		hb.getChildren().addAll(startGame_button, logout_button);
		
		vbox.getChildren().add(amount_queue);
		vbox.getChildren().add(hb);
		vbox.setPadding(new Insets(100,0,0,10));
		userList.setPadding(new Insets(10,10,10,10));
		userList.setMaxHeight(360);
		userList.setMaxWidth(140);
		//Transparent ListView
		//userList.setStyle();
		
		
		VBox vb = new VBox();
		vb.getChildren().addAll(statistics, vbox);
		
		
		pane = new BorderPane();
		pane.setBackground(new Background(bi));
		((BorderPane) pane).setRight(userList);
		((BorderPane) pane).setCenter(vb);
		//((BorderPane) pane).setBottom(hbox);
		
		
		gui.setHeight(Gui.window_height);
		gui.setWidth(Gui.window_width);

	}

	private void updateData() {
		getUserList();
		getStatistics();
		getQueueAmount();
	}

	private void updateUserList(JSONObject json) {
		JSONArray userArr = json.getJSONArray(JSONLobbyAttributes.NEWUSERLIST.name());
		List<String> newUserList = new LinkedList<String>();

		for (int i = 0; i < userArr.length(); i++) {
			JSONObject temp = userArr.getJSONObject(i);
			String username = temp.getString("username");
			boolean ingame = temp.getBoolean("ingame");

			String toAdd = ingame ? String.format("%s[ingame]", username) : String.format("%s", username);
			newUserList.add(toAdd);
		}

		userList.getItems().clear();
		userList.getItems().addAll(newUserList);
		newUserList.clear();
	}

	private void getUserList() {
		JSONObject json = new JSONObject();
		json.put(JSONActionsE.EVENT.name(), JSONEventsE.USERLIST.name());
		connectionSocket.sendMessage(json.toString());
	}

	private void getStatistics() {
		JSONObject json = new JSONObject();
		json.put(JSONActionsE.EVENT.name(), JSONEventsE.STATISTICS.name());
		connectionSocket.sendMessage(json.toString());
	}

	private void getQueueAmount() {
		JSONObject json = new JSONObject();
		json.put(JSONActionsE.EVENT.name(), JSONEventsE.QUEUENUMBER.name());
		connectionSocket.sendMessage(json.toString());
	}

	private void flush(){
		JSONObject json = new JSONObject();
		json.put(JSONActionsE.EVENT.name(), JSONEventsE.FLUSH.name());
		connectionSocket.sendMessage(json.toString());
	}
	
	@Override
	public Pane getScreen() {
		return pane;
	}

}
