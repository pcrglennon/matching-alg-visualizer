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
 */

public class PermGreedyCampusTwo {

    private CampusModelTwo campusTwo;
    private ArrayList<Node> spots;
    private ArrayList<Node> destinations;

    private PermutationMatch pm;
    
    //Store win counts for Average Cost, Worst Cost, and Best Cost (in that order)
    //i.e. pmWins[0] = number of Average Cost Wins
    private int[] pmWins = {0,0,0};
    private int[] goWins = {0,0,0};

    public PermGreedyCampusTwo() {
	initModel();
    }

    private void initModel() {
	campusTwo = new CampusModelTwo();
	spots = campusTwo.getNewSpots();
	destinations = campusTwo.getNewDestinations();
    }
    
    private void resetModel() {
	spots = campusTwo.getNewSpots();
	destinations = campusTwo.getNewDestinations();
    }

    public ArrayList<Node> getSpotsCopy() {
	return new ArrayList<Node>(spots);
    }
    
    public ArrayList<Node> getDestinationsCopy() {
	return new ArrayList<Node>(destinations);
    }

     /**  -------------------------PERMUTATION-------------------------  */

    public ArrayList<MatchInfo> runPermutationMatch() {
	pm = new PermutationMatch(spots, destinations);
	ArrayList<MatchInfo> pmMatching = pm.runAlgorithm();
	return pmMatching;
    }

     /**  -------------------------GREEDY ONLINE-------------------------  */

    public ArrayList<MatchInfo> greedyOnlineMatch() {
	double maxDistance = Double.MAX_VALUE;
	ArrayList<Node> spotsCopy = getSpotsCopy();
	ArrayList<MatchInfo> matches = new ArrayList<MatchInfo>(spotsCopy.size());
	for(Node r: destinations) {
	    double minDistance = maxDistance;
	    int selectedSNodeIndex = 0;
	    for(int i = 0; i < spotsCopy.size(); i++) {
		double dist = xyDistance(r.xPos, spotsCopy.get(i).xPos, r.yPos, spotsCopy.get(i).yPos);
		if(dist < minDistance) {
		    minDistance = dist;
		    selectedSNodeIndex = i;
		}
	    }
	    matches.add(new MatchInfo(r, spotsCopy.get(selectedSNodeIndex), minDistance));
	    spotsCopy.remove(selectedSNodeIndex);
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
	int numRuns = 80;
	StringBuilder sb = new StringBuilder();
	double sumAvgPMCost = 0.0;
	double sumWorstPMCost = 0.0;
	double sumAvgGOCost = 0.0;
	double sumWorstGOCost = 0.0;
	sb.append("\n\n---------TALLY TEST----------\n\n");
	sb.append("\n----------NUM RUNS - " + numRuns + "-------\n");
	sb.append("\n---------PERM v. GREEDY CAMPUS MODEL TWO (n = 100)----------\n\n");
	for(int j = 1; j <= numRuns; j++) {
	    //Randomize arrival order
	    resetModel();
	    sb.append("\n\n-----ITERATION " + j + "------");
	    System.out.println("\n\n---------ITERATION " + j + "--------\n\n");
	    ArrayList<MatchInfo> goMatching = greedyOnlineMatch();
	    double[] goMatchingCosts = getCosts(goMatching);
	    sumAvgGOCost += goMatchingCosts[0];
	    ArrayList<MatchInfo> pmMatching = runPermutationMatch();
	    double[] pmMatchingCosts = getCosts(pmMatching);
	    sumAvgPMCost += pmMatchingCosts[0];
	    //Determine Winners
	    determineWinners(goMatchingCosts, pmMatchingCosts);
	    /**
	    sb.append("\n\nPERMUTATION AVG COST >> " + fourDecimals.format(avgPMCost));
	    sb.append("   ||   GREEDY AVG COST >> " + fourDecimals.format(avgGOCost));
	    sb.append("\nPERMUATION WORST COST >> " + fourDecimals.format(worstPMCost));
	    sb.append("   ||   GREEDY WORST COST >> " + fourDecimals.format(worstGOCost));
	    */
	}	
	sb.append("\n-------------------------Avg. PM Cost >> " + Double.valueOf(fourDecimals.format(sumAvgPMCost/numRuns)));
	sb.append("\n-------------------------Avg. GO Cost >> " + Double.valueOf(fourDecimals.format(sumAvgGOCost/numRuns)));
	sb.append("\n\nFOR CAMPUS TWO:\n");
	sb.append("\nPERMUTATION WINS " + "\n\nAVERAGE >> " + pmWins[0] + "\nWORST >> " + pmWins[1] + "\nBEST >> " + pmWins[2] + "\n\n");
	sb.append("\nGREEDY ONLINE WINS " + "\n\nAVERAGE >> " + goWins[0] + "\nWORST >> " + goWins[1] + "\nBEST >> " + goWins[2]);
	try {
	    FileWriter fstream = new FileWriter("PERM_GREEDY_TALLY_C1_RESULTS.txt");
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

    private void determineWinners(double[] goCosts, double[] pmCosts) {
	if(pmCosts[0] < goCosts[0]) {
	    pmWins[0]++;
	} else {
	    goWins[0]++;
	}
	if(pmCosts[1] < goCosts[1]) {
	    pmWins[1]++;
	} else {
	    goWins[1]++;
	}
	if(pmCosts[2] < goCosts[2]) {
	    pmWins[2]++;
	} else {
	    goWins[2]++;
	}
    }

    public static void main(String[] args) {
	PermGreedyCampusTwo pgc2 = new PermGreedyCampusTwo();
	pgc2.test();
    }
}