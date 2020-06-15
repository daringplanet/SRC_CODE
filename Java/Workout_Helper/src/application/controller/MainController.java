package application.controller;

import java.io.IOException;

import application.Main;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
/**
 *
 * @author William Lippard
 *
 */
public class MainController  implements EventHandler {

	@FXML
	CheckBox cardio;
	@FXML
	CheckBox strength;
	@FXML
	Button Start;
	/**
	 * checks to see if a workout has been selected then changes to the workout scene
	 */
	public void startWorkout() {

		if(cardio.isSelected() || strength.isSelected()) {

			try {
				// redirect user to next scene
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(MainController.class.getResource("../../Workout.fxml"));

				AnchorPane layout = (AnchorPane) loader.load();
				Scene scene = new Scene( layout );


				Main.stage.setScene(scene);
				Main.stage.show();

				} catch( IOException e ){

					e.printStackTrace();
				}



		}


	}

	/**
	 * makes sure that strength is unselected
	 */
	public void selectCardio() {
		strength.setSelected(false);
		Main.workout=0;

	}

	/**
	 * makes sure that cardio is unselected
	 */
	public void selectStrength() {
		cardio.setSelected(false);
		Main.workout=1;

	}


/**
 * not used
 */
	@Override
	public void handle(Event arg0) {
		// TODO Auto-generated method stub

	}

}
