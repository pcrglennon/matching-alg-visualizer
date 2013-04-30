import java.util.ArrayList;

public class CampusModelOne extends ParkingModel {

    public CampusModelOne() {
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
	setupSpots(false);
    }

    protected void setupSpots(boolean extraCap) {
	spots.clear();
	int yCount = 1;
	for(int y = 16; y < 20; y++) {
	    for(int x = 0; x < 5; x++) {
		spots.add(new Node("y" + yCount,x,y));
		yCount++;
	    }
	}
	for(int y = 3; y < 7; y++) {
	    for(int x = 7; x < 11; x++) {
		spots.add(new Node("y" + yCount,x,y));
		yCount++;
	    }
	}
	for(int x = 32; x < 45; x++) {
	    spots.add(new Node("y" + yCount,x,0));
	    yCount++;
	}
	for(int y = 8; y < 11; y++) {
	    for(int x = 62; x < 67; x++) {
		spots.add(new Node("y" + yCount,x,y));
		yCount++;
	    }
	}
	for(int y = 16; y < 20; y++) {
	    for(int x = 60; x < 63; x++) {
		spots.add(new Node("y" + yCount,x,y));
		yCount++;
	    }
	}
	for(int y = 30; y < 36; y++) {
	    for(int x = 66; x < 70; x++) {
		spots.add(new Node("y" + yCount,x,y));
		yCount++;
	    }
	}
    }

    public static void main(String[] args) {
	CampusModelOne cm1 = new CampusModelOne();
	cm1.setupSpots(false);
	cm1.setupDestinations();
	cm1.shuffleDestinationOrder();
    }

}