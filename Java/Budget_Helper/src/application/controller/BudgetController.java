package application.controller;
/**
 * Controller for the Budget, displaying a pie chart of the budget account
 * @author William Lippard
 */
import application.model.Budget;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ListView;

public class BudgetController implements EventHandler {



	@FXML
	private PieChart pieChart;

	private Budget budget = new Budget();

/**
 * this function returns to the home/main scene
 */
	public void homeHandle() {
		MainController.home();
	}

/**
 * this fucntion sets up and builds the pie chart according to the budget
 */
	public void initialize() {
		PieChart.Data income = new PieChart.Data("Income", budget.getIncome());
		PieChart.Data expenses = new PieChart.Data("Expenses", budget.getExpenses());
		PieChart.Data remaining = new PieChart.Data("Remaining", budget.getRemaining());
		pieChart.getData().add(income);
		pieChart.getData().add(expenses);
		pieChart.getData().add(remaining);
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
