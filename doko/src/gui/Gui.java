package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Gui extends Application {

	private Stage mainStage;
	private Scene mainScene;
	
	private GuiScreen guiScreen;
	private Thread currentScreen;

	private static final int window_width = 700;
	private static final int window_height = 550;
	private static final int GAMESCREEN_WIDTH = 700;
	private static final int GAMESCREEN_HEIGHT = 550;
	
	private static final boolean resizable = false;
	
	
	public Gui() {
		guiScreen = new LoginScreen(this);
		mainScene = new Scene(guiScreen.getScreen());
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		this.mainStage = primaryStage;
		
		mainStage.setTitle("Doppelkopf Ultimate");

		mainStage.setScene(mainScene);
		mainStage.setResizable(resizable);
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
	
	public void setWidth(int w) {
		window_width = w;
	}
	
	public void setHeight(int h) {
		window_height = h;
	}
	
	public void StageResizable(boolean sizeable) {
		mainStage.setResizable(sizeable);
	}
}
