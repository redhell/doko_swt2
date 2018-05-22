package gui;

import org.json.JSONObject;

import backend.ConnectionSocket;
import backend.enums.JSONActionsE;
import backend.enums.JSONAttributesE;
import backend.enums.JSONEventsE;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class LoginButtonHandler implements EventHandler<ActionEvent> {

	private ConnectionSocket connectionSocket;

	private Gui gui;
	private TextField username_field;
	private PasswordField password_field;
	private Label login_failed_label;

	public LoginButtonHandler(Gui gui, TextField username_field, PasswordField password_field,
			Label login_failed_label) {
		this.gui = gui;
		this.username_field = username_field;
		this.password_field = password_field;
		this.login_failed_label = login_failed_label;

		connectionSocket = ConnectionSocket.getInstance();
	}

	@Override
	public void handle(ActionEvent event) {

		JSONObject login_credentials = new JSONObject();
		String username = username_field.getText();
		String password = password_field.getText();
		login_credentials.put(JSONActionsE.EVENT.name(), JSONEventsE.LOGIN.name());
		login_credentials.put(JSONAttributesE.USERNAME.name(), username);
		login_credentials.put(JSONAttributesE.PASSWORD.name(), password);

		boolean login_successful = connectionSocket.login(login_credentials.toString());

		if (login_successful) {
			login_failed_label.setTextFill(Color.GREEN);
			login_failed_label.setText("success");
			gui.getStage().setTitle(username);
			gui.changeToLobby();
		} else {
			login_failed_label.setTextFill(Color.RED);
			login_failed_label.setText("There was an error");
		}

	}

}
