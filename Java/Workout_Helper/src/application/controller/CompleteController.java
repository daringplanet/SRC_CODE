package application.controller;

import java.io.IOException;

import application.Main;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
/**
 *
 * @author William Lippard
 *
 */
public class CompleteController implements EventHandler{


	/**
	 * this function handles the home button and changes the screen back to the start screen
	 */
	public void homeHandle(){

		try {
			// redirect user to next scene
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainController.class.getResource("../../Start.fxml"));

			AnchorPane layout = (AnchorPane) loader.load();
			Scene scene = new Scene( layout );


			Main.stage.setScene(scene);
			Main.stage.show();

			} catch( IOException e ){

				e.printStackTrace();
			}
	}
/**
 * not used
 */
	@Override
	public void handle(Event arg0) {
		// TODO Auto-generated method stub

	}

}
