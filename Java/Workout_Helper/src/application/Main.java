package application;
/**
 * @author William Lippard, aju722	
 */
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

public class Main extends Application {
	
	public static Stage stage;
	public static int workout=-1;
	/**
	 * this function loads a scene onto a stage
	 * @param primaryStage Stage used to set the stage
	 */
	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;
		try {
			// Load the FXML document (we created with SceneBuilder)
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation( Main.class.getResource("../Start.fxml") );
			
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
	 * launches the program
	 * @param args String[] arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
