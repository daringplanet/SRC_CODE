package FlightInformation;
/**
 * @author William Lippard aju722
 *
 */
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;


public abstract class Flight  implements Comparable<Flight> {

	private String flightNumber;
	private String origin;
	private String destination;
	private String departureTime;
	private String arrivalTime;
	private ArrayList<Reservation> reservations = new ArrayList<Reservation>();




	public Flight(String flightNumber, String origin, String destination, String departure, String arrivalTime) {
		this.flightNumber = flightNumber;
		this.origin = origin;
		this.destination = destination;
		this.departureTime = departure;
		this.arrivalTime = arrivalTime;
		populateReservation();
	}

	/**
	 * This function populates the reservation array list
	 */
	public void populateReservation(){
		this.reservations.add(new Reservation("1", "2", "name1", "name2", "3"));
		this.setReservations(reservations.get(0).getFlightReservations(this));	
	}



	/**
	 * @return String a formated string to print flight information
	 */
	public String toString() {
		return " Flight " + flightNumber + " form " + origin + " to " + destination + " departs: "
				+ departureTime + " arrival: " + arrivalTime + ". Seats reserved: " + this.reservations.size();

	}

	/**
	 * @param o Flight flight object to compare with
	 * @return int the compare value to determine where to place in the arraylist
	 */
	public int compareTo(Flight o) {
		return this.getDepartureTime().compareTo(o.getDepartureTime());
	}

	/**
	 * @param line String that contains flight information
	 * @return newFlight Flight a new flight that is commercial or private
	 */
	public Flight parseFlightInformation(String line) {

		String flightInfo[] = line.split(",");
		String flightNumber = flightInfo[0];
		Flight newFlight;
		if(!isCommercialFlight(flightInfo[1]))
			newFlight = new PrivateFlight(flightNumber, flightInfo[2], flightInfo[3], flightInfo[4], flightInfo[5]);
		else
			newFlight = new CommercialFlight(flightNumber, flightInfo[2], flightInfo[3], flightInfo[4], flightInfo[5]);

		return newFlight;
	}


	/**
	 * @param flight String that contains commercial or private
	 * @return true/false depending on the if statment
	 */
	boolean isCommercialFlight(String flight) {
		if(flight.equals("commercial"))
			return true;
		else
			return false;
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
	 * @return origin String The flight origin
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * @param origin String the flight origin
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	/**
	 * @return destination String the destination of the flight
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * @param destination String the flight destination
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	/**
	 * @return departureTime String that contains the departure time
	 */
	public String getDepartureTime() {
		return departureTime;
	}

	/**
	 * @param departureTime Sting that contains the departure time
	 */
	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	/**
	 * @return arrivalTime String contains the arrival time
	 */
	public String getArrivalTime() {
		return arrivalTime;
	}

	/**
	 * @param arrivalTime String contains the arrival time
	 */
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	/**
	 * @return reservations ArrayList<Reservation> a collection reservations
	 */
	public ArrayList<Reservation> getReservations() {
		return reservations;
	}

	/**
	 * @param reservations ArrayList<Reservation> a collection reservations
	 */
	public void setReservations(ArrayList<Reservation> reservations) {
		this.reservations = reservations;
	}
	/**
	 * @return int the size of the reservation arraylist 
	 */
	public int getReservationSize() {

		return this.reservations.size();
	}

}
