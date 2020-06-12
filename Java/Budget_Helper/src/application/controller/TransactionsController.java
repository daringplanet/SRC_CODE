package application.controller;
/**
 * Controller for the Transactions, displaying a list view of the budget account
 * @author William Lippard
 */
import application.model.Budget;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class TransactionsController implements EventHandler {


	@FXML
	private ListView listView;

	private Budget budget = new Budget();

	/**
	 * handles the home button
	 * returns to the home screen
	 */
	public void homeHandle() {
		MainController.home();

	}

	/**
	 *  does nothing
	 * @param event Event
	 */
	@Override
	public void handle(Event event) {


	}
/**
 * this function sets up all the data needed to display to the user and adds the data to the listView
 */
	public void initialize() {
		
		for(int i=this.budget.budgetSize()-1; i>=0; i--)
			listView.getItems().add(this.budget.getOneTransaction(i));
		
	}

}
