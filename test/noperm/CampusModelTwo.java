import java.util.ArrayList;

public class CampusModelTwo extends ParkingModel {

    public CampusModelTwo() {
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
	for (int i = 0; i < 5; i++) {
	    destinations.add(new Node("x" + xCount,2,2));
	    xCount++;
	}
	for (int i = 0; i < 8; i++) {
	    destinations.add(new Node("x" + xCount,3,6));
	    xCount++;
	}
	for (int i = 0; i < 10; i++) {
	    destinations.add(new Node("x" + xCount,7,14));
	    xCount++;
	}
	for (int i = 0; i < 3; i++) {
	    destinations.add(new Node("x" + xCount,8,22));
	    xCount++;
	}
	destinations.add(new Node("x" + xCount,15,7));
	xCount++;
	for (int i = 0; i < 8; i++) {
	    destinations.add(new Node("x" + xCount,20,7));
	    xCount++;
	}
	for (int i = 0; i < 7; i++) {
	    destinations.add(new Node("x" + xCount,26,7));
	    xCount++;
	}
	for (int i = 0; i < 14; i++) {
	    destinations.add(new Node("x" + xCount,24,15));
	    xCount++;
	}
	destinations.add(new Node("x" + xCount,26,24));
	xCount++;
	for (int i = 0; i < 2; i++) {
	    destinations.add(new Node("x" + xCount,28,10));
	    xCount++;
	}
	for (int i = 0; i < 12; i++) {
	    destinations.add(new Node("x" + xCount,35,8));
	    xCount++;
	}
	for (int i = 0; i < 8; i++) {
	    destinations.add(new Node("x" + xCount,37,12));
	    xCount++;
	}
	for (int i = 0; i < 6; i++) {
	    destinations.add(new Node("x" + xCount,49,10));
	    xCount++;
	}
	for (int i = 0; i < 10; i++) {
	    destinations.add(new Node("x" + xCount,50,5));
	    xCount++;
	}
	for (int i = 0; i < 5; i++) {
	    destinations.add(new Node("x" + xCount,50,22));
	    xCount++;
	}
    }

    @Override
    protected void setupSpots() {
	spots.clear();
	int yCount = 1;
	for(int y = 0; y < 5; y++) {
	    for(int x = 5; x < 15; x++) {
		spots.add(new Node("y" + yCount,x,y));
		yCount++;
	    }
	}
	for(int y = 33; y < 38; y++) {
	    for(int x = 5; x < 15; x++) {
		spots.add(new Node("y" + yCount,x,y));
		yCount++;
	    }
	}
    }
}