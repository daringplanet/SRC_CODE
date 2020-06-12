package application;
/**
 * @author William Lippard
 * 
 * This program can help people manage their budget.
 * 
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;


public class Main extends Application {

	public static Stage stage;
/**
 * This function loads and sets a scenes onto a stage and displays it to the user
 * @pram primaryStage Stage is the stage for all the scenes
 * 
 */
	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;
		try {
			// Load the FXML document (we created with SceneBuilder)
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation( Main.class.getResource("../Main.fxml") );

			// Load the layout from the FXML and add it to the scene
			AnchorPane layout = (AnchorPane) loader.load();				
			Scene scene = new Scene( layout );

			// Set the scene to stage and show the stage to the user
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Main method to launch the application
	 * @param args
	 */
	public static void main(String[] args) {


		launch(args);
	}
}
