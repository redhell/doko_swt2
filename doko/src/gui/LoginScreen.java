package gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
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
	private BackgroundImage backgroundImage;
	private Image image;
	
	public LoginScreen(Gui gui) {
		this.gui = gui;

		welcomeTitle = new Text("Welcome");
		welcomeTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		username_label = new Label("Username");
		password_label = new Label("Password");
		username_field = new TextField();
		password_field = new PasswordField();
		login_failed_label = new Label();
		
		try {
			image = new Image(new FileInputStream("assets/doko.png"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		backgroundImage = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, 
				BackgroundPosition.CENTER, new BackgroundSize(700, 550, false, false, false, false));

		buildScreen();
	}

	private void buildScreen() {

		username_field.setPromptText("username");
		username_field.setMaxWidth(125);
		password_field.setPromptText("password");
		password_field.setMaxWidth(125);
		
		welcomeTitle.setFill(Color.WHITE);
		username_label.setTextFill(Color.WHITE);
		password_label.setTextFill(Color.WHITE);

		Button login_button = new Button("LogIn");
		login_button.setOnAction(new LoginButtonHandler(gui, username_field, password_field, login_failed_label));
		//login_button.setPadding(new Insets(5, 5, 5, 5));
		login_button.setFont(Font.font(15));
		
		pane = new GridPane();
		((GridPane) pane).setAlignment(Pos.CENTER_LEFT);
		((GridPane) pane).setHgap(10);
		((GridPane) pane).setVgap(10);
		pane.setPadding(new Insets(25, 25, 25, 25));
		
		pane.setBackground(new Background(backgroundImage));
		
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
