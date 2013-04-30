import java.io.BufferedWriter;
import java.io.FileWriter;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Random;

/*
 * Tally Test for Permutation v. Greedy:
 *
 * Run 100 instances on each Parking Model, count wins for each alg on:
 * 1) Avg. Match Dist
 * 2) Worst Match Dist
 * 3) Best Match Dist
 *
 * The four models:
 * 1) Campus Model 1
 * 2) Campus Model 1 w/ 17 additional high-priority spaces
 * 3) Campus Model 2
 * 4) Office Model
 */

public class PermGreedyParkingModels {

    private CampusModelOne campusOne;
    private ArrayList<Node> campusOneSpots;
    private ArrayList<Node> campusOneDestinations;

    private CampusModelTwo campusTwo;
    private ArrayList<Node> campusTwoSpots;
    private ArrayList<Node> campusTwoDestinations;

    private PermutationMatch pm;
    
    //Store win counts for each model (index 0 = campus 1, 1 = campus 1 w/ extra spaces,
    //2 = campus 2, 3 = office
    private int[] pmAvgWins = {0,0,0,0};
    private int[] pmWorstWins = {0,0,0,0};
    private int[] pmBestWins = {0,0,0,0};
    private int[] goAvgWins = {0,0,0,0};
    private int[] goWorstWins = {0,0,0,0};
    private int[] goBestWins = {0,0,0,0};

    public PermGreedyParkingModels() {
	initModels();
    }

    private void initModels() {
	campusOne = new CampusModelOne();
	campusOneSpots = campusOne.getNewSpots();
	campusOneDestinations = campusOne.getNewDestinations();
	campusTwo = new CampusModelTwo();
	campusTwoSpots = campusTwo.getNewSpots();
	campusTwoDestinations = campusTwo.getNewDestinations();
    }
    
    private void resetModels() {
	campusOneSpots = campusOne.getNewSpots();
	campusOneDestinations = campusOne.getNewDestinations();
	campusTwoSpots = campusTwo.getNewSpots();
	campusTwoDestinations = campusTwo.getNewDestinations();
    }

    public ArrayList<Node> getSpotsCopy(int modelNum) {
	if(modelNum == 1) {
	    return new ArrayList<Node>(campusOneSpots);
	} else {
	    return new ArrayList<Node>(campusTwoSpots);
	}
    }
    
    public ArrayList<Node> getDestinationsCopy(int modelNum) {
	if(modelNum == 1) {
	    return new ArrayList<Node>(campusOneDestinations);
	} else {
	    return new ArrayList<Node>(campusTwoDestinations);
	}
    }

     /**  -------------------------PERMUTATION-------------------------  */

    public ArrayList<MatchInfo> runPermutationMatch(int modelNumber) {
	if(modelNumber == 1) {
	    pm = new PermutationMatch(campusOneSpots, campusOneDestinations);
	} else {
	    pm = new PermutationMatch(campusTwoSpots, campusTwoDestinations);
	}
	ArrayList<MatchInfo> pmMatching = pm.runAlgorithm();
	return pmMatching;
    }

     /**  -------------------------GREEDY ONLINE-------------------------  */

    public ArrayList<MatchInfo> greedyOnlineMatch(int modelNumber) {
	double maxDistance = Double.MAX_VALUE;
	ArrayList<Node> spots = getSpotsCopy(modelNumber);
	ArrayList<MatchInfo> matches = new ArrayList<MatchInfo>(spots.size());
	for(Node r: spots) {
	    double minDistance = maxDistance;
	    int selectedSNodeIndex = 0;
	    for(int i = 0; i < spots.size(); i++) {
		double dist = xyDistance(r.xPos, spots.get(i).xPos, r.yPos, spots.get(i).yPos);
		if(dist < minDistance) {
		    minDistance = dist;
		    selectedSNodeIndex = i;
		}
	    }
	    matches.add(new MatchInfo(r, spots.get(selectedSNodeIndex), minDistance));
	    spots.remove(selectedSNodeIndex);
	}
	return matches;
    }

    /**
     * Returns the distance between two points w/ (x,y) coordinates
     */

    private double xyDistance(int x1, int x2, int y1, int y2) {
	return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }
    
    /**---------------------------TEST---------------------------*/

    public void test() {
	//Size of the parking model
	DecimalFormat fourDecimals = new DecimalFormat("#.####");
	int numRuns = 1;
	StringBuilder sb = new StringBuilder();
	double sumAvgPMCost = 0.0;
	double sumWorstPMCost = 0.0;
	double sumAvgGOCost = 0.0;
	double sumWorstGOCost = 0.0;
	sb.append("\n\n---------TALLY TEST)----------\n\n");
	sb.append("\n---------PERM v. GREEDY PARKING MODELS (n = 100)----------\n\n");
	for(int j = 1; j <= numRuns; j++) {
	    //Randomize arrival order
	    resetModels();
	    sb.append("\n\n-----ITERATION " + j + "------");
	    System.out.println("\n\n---------ITERATION " + j + "--------\n\n");
	    ArrayList<MatchInfo> pmMatchCampusOne = runPermutationMatch(1);
	    double[] pmMatchCampusOneCosts = getCosts(pmMatchCampusOne);
	    sumAvgPMCost += pmMatchCampusOneCosts[0];
	    ArrayList<MatchInfo> goMatchCampusOne = greedyOnlineMatch(1);
	    double[] goMatchCampusOneCosts = getCosts(goMatchCampusOne);
	    sumAvgGOCost += goMatchCampusOneCosts[0];
	    //Determine Winners
	    determineCampusOneWinners(goMatchCampusOneCosts, pmMatchCampusOneCosts);
	    /**
	    sb.append("\n\nPERMUTATION AVG COST >> " + fourDecimals.format(avgPMCost));
	    sb.append("   ||   GREEDY AVG COST >> " + fourDecimals.format(avgGOCost));
	    sb.append("\nPERMUATION WORST COST >> " + fourDecimals.format(worstPMCost));
	    sb.append("   ||   GREEDY WORST COST >> " + fourDecimals.format(worstGOCost));
	    */
	}	
	sb.append("\n-------------------------Avg. PM Cost >> " + Double.valueOf(fourDecimals.format(sumAvgPMCost/numRuns)));
	sb.append("\n-------------------------Avg. GO Cost >> " + Double.valueOf(fourDecimals.format(sumAvgGOCost/numRuns)));
	sb.append("\n\nFOR CAMPUS ONE (no additional spaces):\n");
	sb.append("\nPERMUTATION WINS " + "\n\nAVERAGE >> " + pmAvgWins[0] + "\nWORST >> " + pmWorstWins[0] + "\nBEST >> " + pmBestWins[0] + "\n\n");
	sb.append("\nGREEDY ONLINE WINS " + "\n\nAVERAGE >> " + goAvgWins[0] + "\nWORST >> " + goWorstWins[0] + "\nBEST >> " + goBestWins[0]);

	try {
	    FileWriter fstream = new FileWriter("PERM_GREEDY_TALLY_PARKING_RESULTS.txt");
	    BufferedWriter out = new BufferedWriter(fstream);
	    out.write(sb.toString());
	    out.close();
	} catch (Exception e) {
				
	}
    }

    /**
     * Takes in a full matching, and returns an array containing the average,
     * worst, and best cost in that order.
     *
     * So getCosts(someMatching)[0] = the avg cost of that matching, etc
     */
    private double[] getCosts(ArrayList<MatchInfo> matches) {
	double sumCost = 0.0;
	double worstCost = 0.0;
	double bestCost = Double.MAX_VALUE;
	for(MatchInfo m: matches) {
	    if(m.distance > worstCost)
		worstCost = m.distance;
	    if(m.distance < bestCost)
		bestCost = m.distance;
	    sumCost += m.distance;
	}
	double avgCost = sumCost/matches.size();
	double[] toReturn = {avgCost, worstCost, bestCost};
	return toReturn;
    }

    private void determineCampusOneWinners(double[] goCosts, double[] pmCosts) {
	if(pmCosts[0] < goCosts[0]) {
	    pmAvgWins[0]++;
	} else {
	    goAvgWins[0]++;
	}
	if(pmCosts[1] < goCosts[1]) {
	    pmWorstWins[0]++;
	} else {
	    goWorstWins[0]++;
	}
	if(pmCosts[2] < goCosts[2]) {
	    pmBestWins[0]++;
	} else {
	    goBestWins[0]++;
	}
    }

    private void determineCampusOneExtraSpotsWinner() {

    }

    public static void main(String[] args) {
	PermGreedyParkingModels pgpm = new PermGreedyParkingModels();
	pgpm.test();
    }
}