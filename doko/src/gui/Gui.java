package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Gui extends Application {

	private Stage mainStage;
	private Scene mainScene;
	
	private GuiScreen guiScreen;
	private Thread currentScreen;
	
	private static final int window_width = 1024;
	private static final int window_height = 600;

	public Gui() {
		guiScreen = new LoginScreen(this);
		mainScene = new Scene(guiScreen.getScreen());
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		this.mainStage = primaryStage;

		mainStage.setScene(mainScene);

		mainStage.setResizable(false);
		mainStage.setWidth(window_width);
		mainStage.setHeight(window_height);

		mainStage.show();

	}

	public void changeToLobby() {
		guiScreen = new LobbyScreen(this);
		mainScene.setRoot(guiScreen.getScreen());
		currentScreen = new Thread((LobbyScreen) guiScreen);
		currentScreen.start();
	}

	public void changeToGame() {
		guiScreen = new GameScreen(this);
		currentScreen.stop();
		mainScene.setRoot(guiScreen.getScreen());
		currentScreen = new Thread((GameScreen) guiScreen);
		currentScreen.start();
	}

	public Stage getStage(){
		return mainStage;
	}
	
}
