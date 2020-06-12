package FlightInformation;
/**
 * @author William Lippard aju722
 *
 */
public class CommercialFlight extends Flight {

	private String flightType = "Commercial";
	public CommercialFlight(String flightNumber, String origin, String destination, String departure, String arrivalTime) {

		super(flightNumber, origin, destination, departure, arrivalTime);

	}

	public boolean isCommercialFlight() {
		return true;
	}
	/**
	 * String formated string for commercial flight
	 */
	public String toString() {
		return this.getFlightType() + " Flight " + this.getFlightNumber() + " from " + this.getOrigin() + " to " + this.getDestination() +
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
