import java.util.ArrayList;

public class OfficeModel extends ParkingModel {

    public OfficeModel() {
	super();
    }

    public ArrayList<Node> getNewSpots() {
	setupSpots();
	return spots;
    }

    public ArrayList<Node> getNewDestinations() {
	setupDestinations();
	shuffleDestinationOrder();
	return destinations;
    }
    
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