package bank;
/**
 * @author William Lippard
 */
import java.util.*;

public class Bank {

private Set accounts = new HashSet();
private String bankName;


/**
 * constructor
 * @param bankName String the name of the bank
 */
	public Bank(String bankName){
		this.bankName = bankName;

	}


	/**
	 * adds a new account
	 * @param name String of the name of the owner of the account
	 * @param balance double the balance of the account
	 */
	public void addAccount(String name, double balance){
		Account newAccount = new Account(name, accounts.size()+1, balance);
		accounts.add(newAccount);

	}
	/**
	 * adds a new account
	 * @param name String the name of the owner of the account
	 */
	public void addAccount(String name){
		Account newAccount = new Account(name, accounts.size()+1);
		accounts.add(newAccount);
	}
	/**
	 * @return String the formated information of the bank and accounts
	 */
	public String toString(){
		String temp="";
		temp+= "Bank:" + this.bankName + "\n";
		temp+= this.accounts.toString() + "\n";
		return temp;

	}

}
