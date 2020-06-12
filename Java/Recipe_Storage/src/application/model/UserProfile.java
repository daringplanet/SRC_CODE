package application.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
/**
 * 
 * @author William Lippard
 *
 */
public class UserProfile {

	private String userName;
	private String password;


	public UserProfile(String userName, String password) {
		this.userName = userName;
		this.password = password;

	}

	/**
	 * This function is a class funtion that authenticates a user for login in.
	 * @param userName String of the userName entered
	 * @param password String of the password entered
	 * @return temp UserProfile if the login if correct and null if the login does not match any user
	 */
	public static UserProfile authenticate(String userName, String password) {
		File fp = new File("users.txt");
		Scanner scan;
		try {
			scan = new Scanner(fp);

			String line;
			String[] lines;
			while(scan.hasNextLine()) {
				line=scan.nextLine();
				lines = line.split(",", 2);
				if(userName.equals(lines[0])) {
					if(password.equals(lines[1])){
						UserProfile temp = new UserProfile(userName, password);
						return temp;

					}
				}

			}


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

}
