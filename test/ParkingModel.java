import java.util.ArrayList;
import java.util.Collections;

/**
 * Abstract Parking Model class, extended by Campus Models One and Two and Office Model
 *
 * spots: an array of grid points which represent parking spaces
 * destinations: an array of grid points which represent destinations.  Destinations
 * may be shared by many commuters (going to the same building or door)
 */
public class ParkingModel {

    protected ArrayList<Node> spots;
    protected ArrayList<Node> destinations;

    public ParkingModel() {
	spots = new ArrayList<Node>();
	destinations = new ArrayList<Node>();
    }

    protected void shuffleDestinationOrder() {
	Collections.shuffle(destinations);
    }

    protected void setupSpots() {
    }

    protected void setupDestinations() {
    }
}