package application.model;
/**
 * this class sets up a budget
 * @author William Lippard
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

import javafx.scene.control.ListView;

public class Budget {
	private double income;
	private double expenses;
	private double remaining;



	private ArrayList<String> transactions = new ArrayList<String>();
/**
 * this constructor sets up a budget according to data.csv file located in the src folder
 */
	public Budget(){
		this.income=0; //set the income, expenses, and remaining to 0
		this.expenses=0;
		this.remaining=0;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader(); //loads in the file
		java.net.URL url = classLoader.getResource("data.csv");
		File fp = new File(url.getPath());
		Scanner scan;
		try {
			scan = new Scanner(fp);

			String line; //used to scan in the whole line from the file
			String[] lines; //used to separate the whole line to individual parts
			String[] date; //used to separate the date to check for the month
			while(scan.hasNextLine()) {
				line=scan.nextLine();
				lines = line.split(",", 3); //used to separate the whole line to individual parts
				date = lines[0].split("-", 3); //used to separate the date to check for the month
				date[0] = month(date[0]); //replaces the number month with the month name


				if(lines[1].equals("paycheck")) //if the money was income update income
					updateIncome(lines[2]);
				else 
					updateExpense(lines[2]); //else update expenses



				addToList(normalize(lines, date)); //this function addes to the arraylist called budget
													//and normalize formates the data nicely

			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		calRemaining(); //at the end of the file we want to calculate what remains of the income after all the expenses
	}



/**
 * this funciton formates the string stored in the budget arrayList for a transaction
 * @param lines String[] that contains what the transaction was and how much
 * @param date String[] that contains the date
 * @return temp String the formated string
 */
	private String normalize(String[] lines, String[] date) {
		lines[2] = "$"+lines[2];
		String temp = String.format("%s %s, %s %25s %23s \t%s", date[0], date[1], date[2], lines[1], "", lines[2]);
		return temp;
	}






/**
 * this function calculate the month's name and returns it
 * @param month String of numbers of months
 * @return month's Name
 */
	private String month(String month) {

		if(month.equals("01"))
			return "January";
		else if(month.equals("02"))
			return "Febuary";
		else if(month.equals("03"))
			return "March";
		else if(month.equals("04"))
			return "April";
		else if(month.equals("05"))
			return "May";
		else if(month.equals("06"))
			return "June";
		else if(month.equals("07"))
			return "July";
		else if(month.equals("08"))
			return "August";
		else if(month.equals("09"))
			return "September";
		else if(month.equals("10"))
			return "October";
		else if(month.equals("11"))
			return "November";
		else
			return "December";

	}

	/**
	 * this function calculates the remaining income after expenses and sets the remaining class var
	 */
	private void calRemaining(){
		this.remaining = this.income - this.expenses;

	}
/**
 * this function keeps the expenses up to date with a total expenses
 * @param money String how much is spent
 */
	private void updateExpense(String money) {
		double temp = Double.parseDouble(money);
		this.expenses+=temp;

	}

/**
 * this function keeps the income up to date with a total income
 * @param money String how much is acquired
 */
	private void updateIncome(String money) {

		double temp = Double.parseDouble(money);
		this.income+=temp;

	}

	/**
	 * this function adds to the budget arraylist of strings 
	 * @param line String a formated string of a transaction
	 */
	private void addToList(String line) {
		this.transactions.add(line);

	}
/**
 * this function returns one transaction in a string from
 * @param i int the index of the budget arraylist get get one transaction
 * @return String a fromated string of a transaction
 */
	public String getOneTransaction(int i){

		return this.transactions.get(i).toString();
	}

/**
 * return the budget size (the number of transactions)
 * @return budget.size int the number of transactions
 */
	public int budgetSize(){

		return this.transactions.size();
	}
	/**
	 * returns the total income
	 * @return income double the total income
	 */
	public double getIncome(){

		return this.income;

	}
/**
 * returns the total expenses
 * @return expenses Double the total expenses
 */
	public double getExpenses(){

		return this.expenses;
	}

	/**
	 * returns what remains of income after expenses
	 * @return remaining double what remains of income after expenses
	 */
	public double getRemaining(){
		
		return this.remaining;
	}


}
