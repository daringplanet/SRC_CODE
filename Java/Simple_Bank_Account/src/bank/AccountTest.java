package bank;
/**
 * @author William Lippard, aju722
 */
import static org.junit.Assert.*;

import org.junit.*;

public class AccountTest {

	private Account testAccount;

	@Before
	public void setUp(){
		testAccount = new Account("First Last", 10, 100.00);
	}
	
	@Test
	public void testInit(){
		
		//good inputs
		 testAccount = new Account("First Last", 10);
		assert(testAccount.getName() != null) : "testInit: name is null for testAccount";
		assert(testAccount.getAccountNumber() != 0) : "testInit: Account number is 0";
		assert(testAccount.getBalance() >= 0) : "testInit: Account Balance is below 0";
		
		
		testAccount = new Account("First Last", 10, 100.00);
		assert(testAccount.getName() != null) : "testInit: name is null for testAccount";
		assert(testAccount.getAccountNumber() != 0) : "testInit: Account number is 0";
		assert(testAccount.getBalance() >= 0) : "testInit: Account Balance is below 0";
		
		//bad inputs
		testAccount = new Account(null, -10);
		assert(testAccount.getName() != null) : "testInit: name is null for testAccount";
		assert(testAccount.getAccountNumber() >= 0) : "testInit: Account number is 0";
		assert(testAccount.getBalance() >=  0) : "testInit: Account Balance is below 0";
		
		testAccount = new Account(null, -10, -100);
		assert(testAccount.getName() != null) : "testInit: name is null for testAccount"; //has a string No Name put in Name's spot to notifiy the user there is no name under name
		assert(testAccount.getAccountNumber() >= 0) : "testInit: Account number is below 0"; //account with a not acceptble account number will be put to 0 to notify the user there is an error with the account number.
		assert(testAccount.getBalance() >=  0) : "testInit: Account Balance is below 0"; //account balance will be set to 0 if a number less then 0 is entered.
		
		
	}
	
	@Test
	public void testDepositPositive() {
		double oldBalance = testAccount.getBalance();
		testAccount.deposit(10.0);
		assert(testAccount.getBalance() >= 0) : "testDepositPositive: Balance is negitive";
		assert(testAccount.getBalance() >= oldBalance ) : "testDepositPositive: new balance is smaller than the old balance";
		
	}
	
	@Test
	public void testDepositNegative(){
		double oldBalance = testAccount.getBalance();
		testAccount.deposit(-10.0);
		assert(testAccount.getBalance() >= 0) : "testDepositNegative: Balance is negitive";
		assert(testAccount.getBalance() >= oldBalance ) : "testDepositNegative: new balance is smaller than the old balance";
		
	}
	
	@Test
	public void testWithdrawPositive() {
		double oldBalance = testAccount.getBalance();
		testAccount.withdraw(10);
		assert(testAccount.getBalance() >= 0) : "testWithdrawPositive: Balance is negitive";
		assert(testAccount.getBalance() <= oldBalance ) : "testWithdrawPositive: new balance is bigger than the old balance";
	}
	
	@Test
	public void testWithdrawNegative() {
		
		double oldBalance = testAccount.getBalance();
		testAccount.withdraw(-10);
		assert(testAccount.getBalance() >= 0) : "testWithdrawNegative: Balance is negitive";
		assert(testAccount.getBalance() <= oldBalance ) : "testWithdrawNegative: new balance is bigger than the old balance";
		
	}
	
	@Test
	public void testWithdrawOverdraft() {
		
		double oldBalance = testAccount.getBalance();
		testAccount.withdraw(200);
		assert(testAccount.getBalance() >= 0) : "testWithdrawOverdraft: Balance is negitive";
		assert(testAccount.getBalance() <= oldBalance ) : "testWithdrawOverdraft: new balance is bigger than the old balance";
		
	}
	
	
	

}
