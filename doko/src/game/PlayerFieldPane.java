package game;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class PlayerFieldPane {

	protected Pane pane;
	protected String username;

	public abstract void buildScreen();
	
	public PlayerFieldPane(String username) {
		this.username=username;
	}
	
	protected String getUsername() {
		return username;
	}
	
	protected Pane getPane(){
		return pane;
	}
	
}
