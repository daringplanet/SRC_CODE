package FlightInformation;
/**
 * @author William Lippard aju722
 *
 */
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class Lab3 {

	/**
	 * @param args arguments for the main method
	 * this is a program control method that test out flight
	 */
	public static void main(String[] args) {
		ArrayList<Flight> flights = new ArrayList<Flight>();
		try {
			File file = new File("flight-schedule.txt");
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			int i=0;
			while ((line = br.readLine()) != null) {
				Flight oneFlight = new PrivateFlight("1", "asdf", "asdf", "6:05", "14:30");
				flights.add(oneFlight.parseFlightInformation(line));

			}
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
		int i;
		Collections.sort(flights);
		for(i=0; i<100; i++)
			System.out.print("-");
		System.out.println();

		for(i=0; i<flights.size(); i++)
			System.out.println(flights.get(i).toString());
		for(i=0; i<100; i++)
			System.out.print("-");
		System.out.println();
		for(i=0; i<flights.size(); i++) {
			for(int j=0; j<flights.get(i).getReservationSize(); j++){
				System.out.println(flights.get(i).getReservations().get(j).toString());
			}

		}
	}




}

