package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class LoginScreen implements GuiScreen {

	private Gui gui;

	private Pane pane;
	private Text welcomeTitle;
	private Label username_label;
	private Label password_label;
	private TextField username_field;
	private PasswordField password_field;
	private Label login_failed_label;

	private volatile boolean connected;
	
	public LoginScreen(Gui gui) {
		this.gui = gui;

		welcomeTitle = new Text("Welcome");
		welcomeTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		username_label = new Label("Username");
		password_label = new Label("Password");
		username_field = new TextField();
		password_field = new PasswordField();
		login_failed_label = new Label();

		buildScreen();
	}

	private void buildScreen() {

		username_field.setPromptText("username");
		password_field.setPromptText("password");

		Button login_button = new Button("LogIn");
		login_button.setOnAction(new LoginButtonHandler(gui, username_field, password_field, login_failed_label));

		pane = new GridPane();
		((GridPane) pane).setAlignment(Pos.CENTER);
		((GridPane) pane).setHgap(10);
		((GridPane) pane).setVgap(10);
		pane.setPadding(new Insets(25, 25, 25, 25));

		((GridPane) pane).add(welcomeTitle, 0, 0, 2, 1);
		((GridPane) pane).add(username_label, 0, 1);
		((GridPane) pane).add(username_field, 1, 1);
		((GridPane) pane).add(password_label, 0, 2);
		((GridPane) pane).add(password_field, 1, 2);
		
		HBox button_box = new HBox(10);
		button_box.setAlignment(Pos.BOTTOM_RIGHT);
		button_box.getChildren().add(login_button);
		button_box.setAlignment(Pos.BOTTOM_LEFT);
		button_box.getChildren().add(login_failed_label);
		
		((GridPane) pane).add(button_box, 1, 4);

	}

	public Pane getScreen() {
		return pane;
	}


}
