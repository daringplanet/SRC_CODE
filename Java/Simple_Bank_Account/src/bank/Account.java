package bank;
/**
 * 
 * @author William Lippard, aju722
 * This class is a Account which maintains a name, account number, and a balance.
 *
 */
public class Account {
	
	private String name;
	private int accountNumber;
	private double balance;
	
	/**
	 * Constructor
	 * @param name String the name of the person's account
	 * @param accountNum int the account number
	 */
	public Account(String name, int accountNum){
		if(name!=null)
			this.name = name;
		else
			this.name = "No Name";
		
		if(accountNum > 0)
			this.accountNumber = accountNum;
		else
			this.accountNumber = 0;
		
		this.balance = 0;
		
	}
	/**
	 *  Constructor
	 * @param name String the name of the person's account
	 * @param accountNum int the account number
	 * @param balance double the account's balance
	 */
	public Account(String name, int accountNum, double balance){
		if(name!=null)
			this.name = name;
		else
			this.name = "No Name";
		
		if(accountNum > 0)
			this.accountNumber = accountNum;
		else
			this.accountNumber = 0;
		
		if(balance >= 0)
			this.balance = 0;
		else
			this.balance = 0;
	}
	
	
	
	/**
	 * deposits money into the account
	 * @param money double the amount wanting to deposit
	 * @return balance double the new balance
	 */
	public double deposit(double money){
		if(money>0)
			return this.balance += money;
		else
			return this.balance;
		
	}
	/**
	 * withdraws money from the account
	 * @param money double the amount wanting to withdraw
	 * @return double the new balance
	 */
	public double withdraw(double money){
		
		if(money>0 && money<this.balance)
			return this.balance-=money;
		else
			return this.balance; 
	}
	/**
	 * @return temp String that is formated to display the account's information
	 */
	@Override
	public String toString(){
		String temp="";
		
		temp+="Account Number: "  + this.getAccountNumber() + " Name: " + this.getName() +
				" Balance: " + this.getBalance() + "\n";
		
		return temp;
		
	}
	/**
	 * 
	 * @return name String the name on the account
	 */
	public String getName(){
		
		return name;
	}
	
/**
 * 	
 * @return accountNumber int the account Number
 */
	public int getAccountNumber(){
		
		return this.accountNumber;
	}
	/**
	 * 
	 * @return balance double the balance
	 */
	public double getBalance(){
		
		return this.balance;
	}
	/**
	 * sets a balance
	 * @param balance double the new balance
	 */
	public void setBalance(double balance){
		
		this.balance = balance;
	}
	/**
	 * sets a new account number
	 * @param num int new account number
	 */
	public void setAccountNumber(int num){
		
		this.accountNumber = num;
	}
	/**
	 * sets a new name
	 * @param name String the new name on the account
	 */
	public void setName(String name){
		
		this.name = name;
	}
}
