package application.controller;

import java.io.IOException;

import application.Main;
import application.model.UserProfile;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
/**
 * 
 * @author William Lippard
 *
 */
public class LoginController implements EventHandler {

	@FXML
	private TextField userName;

	@FXML
	private PasswordField password;

	/**
	 * This funiton hadles events from Login.fxml and authenticate the login
	 * @param event Event: handles events from Login.fxml
	 */
	@Override
	public void handle(Event event) {

		UserProfile user = UserProfile.authenticate(userName.getText(), password.getText());
		if(user==null) {
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(LoginController.class.getResource("../../Restricted.fxml"));

				AnchorPane layout = (AnchorPane) loader.load();
				Scene scene = new Scene(layout);

				Main.stage.setScene(scene);
				Main.stage.show();

			} catch(IOException e) {

				e.printStackTrace();
			}

		}
		else {
			try {


				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(LoginController.class.getResource("../../Recipe.fxml"));

				AnchorPane layout = (AnchorPane) loader.load();
				Scene scene = new Scene(layout);

				Main.stage.setScene(scene);
				Main.stage.show();


			} catch(IOException e) {

				e.printStackTrace();
			}


		}

	}


}
