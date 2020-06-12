package FlightInformation;
import java.io.*;
import java.util.*;
/**
 * @author William Lippard aju722
 *
 */
public class Reservation implements Comparable<Flight> {

	private String reservationNumber;
	private String flightNumber;
	private String firstName;
	private String lastName;
	private String seatNumber;
	/**
	 * 
	 * 
	 * @param reservationNumber String the reservation number
	 * @param flightNumber String the flight number
	 * @param lastName String the reservation's lastName
	 * @param firstName String the reservation's firstName
	 * @param seatNumber Stirng the seat number
	 * @return Reservation returns a reservation
	 */
	public Reservation(String reservationNumber, String flightNumber, String lastName, String firstName, String seatNumber) {
		this.reservationNumber = reservationNumber;
		this.flightNumber = flightNumber;
		this.firstName = firstName;
		this.lastName = lastName;
		this.seatNumber = seatNumber;

	}


	/**
	 * 
	 * @param line contains one reservation
	 * @return Reservation newReservation the new reservation obj
	 */
	public Reservation parseReservationInformation(String line) {

		String reservationInfo[] = line.split(",");
		String flightNumber = reservationInfo[0];
		String seatNumber = reservationInfo[3];
		String resevationNumber = reservationInfo[0] + reservationInfo[3];

		Reservation newReservation = new Reservation(resevationNumber, flightNumber,
				reservationInfo[1], reservationInfo[2], seatNumber);
		return newReservation;
	}


	/**
	 * @param flight Flight  to compare
	 * @return ArrayList<Reservation> temp a sorted arraylist of all the resrevations of for that flight
	 */
	public ArrayList<Reservation> getFlightReservations(Flight flight){
		ArrayList<Reservation> temp = new ArrayList<Reservation>();
		try {
			File file = new File("reservations.txt");
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			int i=0;
			while ((line = br.readLine()) != null) {
				Reservation oneRes = new Reservation("81522", "22", "name", "name", "22");
				oneRes = oneRes.parseReservationInformation(line);	
				if(oneRes.compareTo(flight) == 0)
					temp.add(oneRes);
			}
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
		Collections.sort(temp, ReservationComparator );
		return temp;
	}

	/**
	 * @return String that is formated to print a reservation
	 */
	@Override
	public String toString() {

		return "Flight " + this.flightNumber + " (" + this.seatNumber + ") " + this.firstName + ", " 
				+ this.lastName;
	}

	/**
	 * @param o Flight used to compare the flight number to the reservtion number
	 * @return int the value of the compare to determine which one is bigger
	 */
	@Override
	public int compareTo(Flight o) {
		return this.getFlightNumber().compareTo(o.getFlightNumber());

	}
	/**
	 * Comparator
	 */
	public static Comparator<Reservation> ReservationComparator = new Comparator<Reservation>() {
		/**
		 * @param rev1 Reservation first reservation to compare
		 * @param rev2 Reservation 2nd reservation to compare
		 * @return int seatNum value to determine which reservation comes first according to seat number
		 */
		public int compare(Reservation rev1, Reservation rev2) {
			int seatNum = Integer.parseInt(rev1.getSeatNumber()) - Integer.parseInt(rev2.getSeatNumber());
			return seatNum;

		}

	};


	/**
	 * @return firstName String of the first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName String the first name
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return lastName String the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName String the last name
	 */
	public void setLastName(String lastName) {
		lastName = lastName;
	}

	/**
	 * @return reservationNumber String the reservation number
	 */
	public String getReservationNumber() {
		return reservationNumber;
	}

	/**
	 * @param reservationNumber String the reservation number
	 */
	public void setReservationNumber(String reservationNumber) {
		this.reservationNumber = reservationNumber;
	}

	/**
	 * @return flightNumber String the flight number
	 */
	public String getFlightNumber() {
		return flightNumber;
	}

	/**
	 * @param flightNumber String the flight number
	 */
	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	/**
	 * @return seatNumber String the seat number
	 */
	public String getSeatNumber() {
		return seatNumber;
	}

	/**
	 * @param seatNumber String the seat number
	 */
	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}


}
