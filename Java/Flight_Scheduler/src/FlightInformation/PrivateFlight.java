package FlightInformation;

/**
 * @author William Lippard aju722
 *
 */
public class PrivateFlight extends Flight {


	private String flightType = "Private";

	public PrivateFlight(String flightNumber, String origin, String destination, String departure, String arrivalTime) {

		super(flightNumber, origin, destination, departure, arrivalTime);

	}
	/**
	 * String formated string for private flight
	 */
	public String toString() {
		return this.getFlightType() + "    Flight " + this.getFlightNumber() + " from " + this.getOrigin() + " to " + this.getDestination() +
				" departs: " + this.getDepartureTime() + " arrives: " + this.getArrivalTime() + ". Seats reserved: " + this.getReservationSize();
	}

	/**
	 * @return flightType String the type of flight
	 */
	public String getFlightType() {
		return flightType;
	}

	/**
	 * @param flightType String the type of flight
	 */
	public void setFlightType(String flightType) {
		this.flightType = flightType;
	}


}
