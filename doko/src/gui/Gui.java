package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Gui extends Application {

	private Stage mainStage;
	private Scene mainScene;

	private GuiScreen guiScreen;
	private Thread currentScreen;

	public static final int window_width = 700;
	public static final int window_height = 550;
	public static final int GAMESCREEN_WIDTH = 1024;
	public static final int GAMESCREEN_HEIGHT = 850;

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

	public void changeToLogin() {
		
		if(currentScreen != null && guiScreen instanceof LobbyScreen){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					((LobbyScreen) guiScreen).shutdown();
					currentScreen.stop();
					
				}
			}).start();
		}
		
		boolean done_shutting_down = false;
		do{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			done_shutting_down = ((LobbyScreen) guiScreen).isShutdown();
		}while(!done_shutting_down);
		guiScreen = new LoginScreen(this);
		mainScene.setRoot(guiScreen.getScreen());
		mainScene = new Scene(guiScreen.getScreen());
		
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

	public Stage getStage() {
		return mainStage;
	}

	public void setWidth(int w) {
		mainStage.setWidth(w);
	}

	public void setHeight(int h) {
		mainStage.setHeight(h);
	}

	public void StageResizable(boolean sizeable) {
		mainStage.setResizable(sizeable);
	}
	
	public void changeScene(Pane root){
		mainScene.setRoot(root);
	}

}
