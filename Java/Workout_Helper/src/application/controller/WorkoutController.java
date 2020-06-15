package application.controller;

import java.io.IOException;

import Model.Workout;
import application.Main;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
/**
 *
 * @author William Lippard
 *
 */
public class WorkoutController implements EventHandler{

	@FXML
	public Label workType, currentExercise, time, progress;
	@FXML
	public ImageView picture;
	private Workout work;
	private String type;

/**
 * sets up the workout
 */
	public void initialize() {
		if(Main.workout==0) {
			String[] temp = {"toetouch", "situp", "jumpingjack"};
			work = new Workout(3, temp);
			type = "cardio";

			startWorkout();

		} else {
			String[] temp = {"superman", "situp", "pushup"};
			work = new Workout(3, temp);
			type="Strength";
			startWorkout();
		}

	}

	/**
	 * this starts the workout by updating all the labels and images accourdingly
	 */
	public void startWorkout(){
		Image one = new Image(work.getImage1());
		Image two = new Image(work.getImage2());
		updatePicture(picture, one, two);
		timer();
		this.workType.setText("Workout: " + type);
		updateLable();
		sleeper();
	}

	/**
	 * this function changes to the next workout and updates all labels and images accordingly
	 */
	public void nextWorkout(){
		this.work.updateWorkout();

		Image one = new Image(work.getImage1());
		Image two = new Image(work.getImage2());
		updatePicture(picture, one, two);
		timer();




		updateLable();


		sleeper();


	}
	/**
	 * will change the screen to the completed screen
	 */
	public void completed(){


			try {
				// redirect user to next scene
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(MainController.class.getResource("../../Complete.fxml"));

				AnchorPane layout = (AnchorPane) loader.load();
				Scene scene = new Scene( layout );


				Main.stage.setScene(scene);
				Main.stage.show();

				} catch( IOException e ){

					e.printStackTrace();
				}



	}

	/**
	 * this function creates a new task to run on a new thread which cycles through the two images creating an
	 * anamation for 30 seconds
	 * @param view ImageView
	 * @param image1 Image
	 * @param image2 Image
	 */
	public void updatePicture(ImageView view, Image image1, Image image2  ) {
		Task<Void> pictureUpdate = new Task<Void>() {
            @Override
            protected Void call() throws Exception {


        		for(int i=0; i<30; i++) {
        			view.setImage(image1);

                try {
                    Thread.sleep(500);
                    view.setImage(image2);
            			Thread.sleep(500);

                } catch (InterruptedException e) {
                		e.printStackTrace();
                }
        	}//end for
                return null;
            }
        };
        new Thread(pictureUpdate).start();
	}

	/**
	 * this function allow to pause the program without pausing the other treads to wait on them to finish
	 */
	public void sleeper() {
		Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

            	Thread.sleep(30000);
            	if(work.getCurrentWorkout() < 3)
            	nextWorkout();
            	else
            		completed();

                return null;
            }
        };
        new Thread(sleeper).start();
	}


	/**
	 * this function sets up a frame to run runTimeTask
	 */
	public void timer(){

		Runnable task = new Runnable(){
			public void run(){

			runTimeTask();
			}
		};

		Thread backgroundThread = new Thread(task);
		backgroundThread.start();


	}


	/**
	 * this handles the time changing
	 */
	public void runTimeTask(){
		for(int i=30; i>=0; i--){
			try {
				final String temp = "Time remaining " + i + " seconds";


			Platform.runLater(new Runnable(){

				@Override
				public void run(){

					time.setText(temp);
				}
			});

				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}

	/**
	 * this function  sets up a frame to run runLabelTask
	 */
	public void updateLable(){

		Runnable task = new Runnable(){
			public void run(){

			runLabelTask();
			}
		};

		Thread backgroundThread = new Thread(task);
		//backgroundThread.setDaemon(true);
		backgroundThread.start();


	}
	/**
	 * this function updates the labels accordingly to the workout
	 */
	public void runLabelTask(){

		String temp1 = "Exercise: " + work.getStringWorkout();
		String temp2 = "Progress: exercise " + work.getCurrentWorkout() + " of 3";


			Platform.runLater(new Runnable(){

				@Override
				public void run(){

					currentExercise.setText(temp1);
					progress.setText(temp2);

				}
			});


	}









	@Override
	public void handle(Event event) {
		// TODO Auto-generated method stub

	}

}
