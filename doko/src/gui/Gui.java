package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Gui extends Application {

	private Stage mainStage;
	private Scene mainScene;
	private GuiScreen guiScreen;

	public Gui() {
		guiScreen = new LoginScreen(this);
		mainScene = new Scene(guiScreen.getScreen());
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		this.mainStage = primaryStage;

		mainStage.setScene(mainScene);

		mainStage.setResizable(false);
		mainStage.setHeight(500);
		mainStage.setWidth(500);

		mainStage.show();

	}

	public void changeToLobby() {
		guiScreen = new LobbyScreen(this);
		Thread lobby_thread = new Thread((LobbyScreen) guiScreen);
		lobby_thread.start();
		mainScene.setRoot(guiScreen.getScreen());
	}

}
