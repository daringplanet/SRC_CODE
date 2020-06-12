package Model;

/**
 * 
 * @author William Lippard, aju722
 *
 */
public class Workout {
	//private int workout;
	private int workoutLeft;
	private String[] exercies;
	private int currentWorkout;
	/**
	 * Constructor, creats a new Workout
	 * @param numWork int the number of workouts
	 * @param exercies String[] the exercies name
	 */
	public Workout( int numWork, String[] exercies) {
		this.workoutLeft = numWork;
		this.exercies = exercies;
		this.currentWorkout = 1;
		
		
	}
	/**
	 * updates the workout to the next one
	 */
	public void updateWorkout() {
		this.workoutLeft-=1;
		this.currentWorkout+=1;
		
	}
	/**
	 * gets the number of workouts left
	 * @return int number of workouts
	 */
	public int getNumWorkoutLeft() {
		
		return this.workoutLeft;
	}
	
	
	/**
	 * gets the current workout
	 * @return int the current workout number
	 */
	public int getCurrentWorkout(){
		return this.currentWorkout;
	}
	
	/**
	 * gets the current workout name
	 * @return String the name of the workout
	 */
	public String getStringWorkout() {
		return this.exercies[this.currentWorkout-1];
		
	}
	
	/**
	 * gets the images's path as a string
	 * @return String the images's path
	 */
	public String getImage1(){
		String temp="";
		temp+= "images/" + getStringWorkout() + "1.png";
		
		return temp;
		
	}
	
	/**
	 * gets the images's path as a string
	 * @return String the images's path
	 */
	public String getImage2(){
		String temp="";
		temp+= "images/" + getStringWorkout() + "2.png";
		return temp;
		
	}

}
