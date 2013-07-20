import java.util.ArrayList;

/**
 * A Parking model which simulates a small office parking lot scheme.
 *
 * 150 parking spots in one lot and 150 destinations
 * Destinations are split 3 sets of doors, 50 commuters to each door
 *
 * See the parking_model_images folder for a picture
 */

public class OfficeModel extends ParkingModel {

    public OfficeModel() {
	super();
    }

    /**
     * Returns a new copy of the array of parking spots
     */
    public ArrayList<Node> getNewSpots() {
	setupSpots();
	return spots;
    }

    /**
     * Returns an array of destinations in randomized order
     */
    public ArrayList<Node> getNewDestinations() {
	setupDestinations();
	shuffleDestinationOrder();
	return destinations;
    }
    
    /**
     * Constructs the destinations array
     */
    @Override
    protected void setupDestinations() {
	destinations.clear();
	int xCount = 1;
	for (int i = 0; i < 50; i++) {
	    destinations.add(new Node("x" + xCount,2,2));
	    xCount++;
	}
	for (int i = 50; i < 100; i++) {
	    destinations.add(new Node("x" + xCount,3,6));
	    xCount++;
	}
	for (int i = 100; i < 150; i++) {
	    destinations.add(new Node("x" + xCount,7,14));
	    xCount++;
	}
    }

    /**
     * Constructs the spots array
     */
    @Override
    protected void setupSpots() {
	spots.clear();
	int yCount = 1;
	for(int y = 5; y < 11; y++) {
	    for(int x = 0; x < 25; x++) {
		spots.add(new Node("y" + yCount,x,y));
		yCount++;
	    }
	}
    }
}