import java.util.ArrayList;
import java.util.Collections;

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