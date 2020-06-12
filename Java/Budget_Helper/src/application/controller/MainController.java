package application.controller;
/**
 * This class controls the Main or the home screen 
 * @author William Lippard
 * 
 */
import java.io.IOException;

import application.Main;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

public class MainController implements EventHandler {

	/**
	 * this function allows us to return to this screen from other screens in the application
	 */
	public static void home() {

		try{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainController.class.getResource("../../Main.fxml"));

			AnchorPane layout = (AnchorPane) loader.load();
			Scene scene = new Scene( layout );

			Main.stage.setScene(scene);
			Main.stage.show();

		} catch( IOException e ){

			e.printStackTrace();
		}


	}

/**
 * this function handles the budget button and changes the scene to the budget scene
 * @param event not used
 */
	public void budgetHandle(Event event){

		try{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainController.class.getResource("../../Budget.fxml"));

			AnchorPane layout = (AnchorPane) loader.load();
			Scene scene = new Scene( layout );

			Main.stage.setScene(scene);
			Main.stage.show();

		} catch( IOException e ){

			e.printStackTrace();
		}

	}

	/**
	 * this function handles the transaction button and changes the scene to the transaction scene
	 * @param event not used
	 */
	public void transHandle(Event event){

		try{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainController.class.getResource("../../Transactions.fxml"));

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
 * @param event Event 
 */
	@Override
	public void handle(Event event) {
		// TODO Auto-generated method stub

	}




}
