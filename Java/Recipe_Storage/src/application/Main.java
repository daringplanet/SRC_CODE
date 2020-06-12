package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 * 
 * @author William Lippard
 * 
 *
 */
public class Main extends Application {

	public static Stage stage;

	/**
	 * This function loads and sets a scenes onto a stage and displays it to the user
	 * @param primaryStage Stage is the stage for all the scenes
	 */
	@Override
	public void start(Stage primaryStage) {

		this.stage = primaryStage;

		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation( Main.class.getResource("../Login.fxml"));

			AnchorPane layout = (AnchorPane) loader.load();
			Scene scene = new Scene( layout );

			primaryStage.setScene(scene);
			primaryStage.show();


		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
