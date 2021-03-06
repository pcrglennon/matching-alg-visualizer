import java.io.BufferedWriter;
import java.io.FileWriter;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Random;

/**
 * Runs Max-Flow, both New Algs, and Greedy on Campus Model 1 w/ 17 additional
 * high-desirability spots
 *
 * Note - Does not include Opt Bottleneck, as that algorithm requires that
 * the # of spots(server nodes) = the # of destinations to set up a square 
 * (n x n) cost matrix
 *
 * Optimal Threshold for Campus 1 w/ extra spots = 19
 */

public class TestCampusOneExtra {

    private CampusModelOne campusOneExtra;
    private ArrayList<Node> spots;
    private ArrayList<Node> destinations;

    private OptBottleneck ob;
    private MaxFlowBP mf;
    private PermutationMatch pm;
    private NewAlgs na;

    //Store win counts for Average Cost, Worst Cost, and Best Cost (in that order)
    //i.e. mfWins[0] = number of Average Cost Wins
    private int[] obWins = {0,0,0};
    private int[] mfWins = {0,0,0};
    private int[] pmWins = {0,0,0};
    private int[] na1Wins = {0,0,0};
    private int[] na2Wins = {0,0,0};
    private int[] goWins = {0,0,0};

    public TestCampusOneExtra() {
	initModel();
	na = new NewAlgs(spots, destinations);
	na.setThreshold(19);
    }

    private void initModel() {
	campusOneExtra = new CampusModelOne();
	spots = campusOneExtra.getNewSpots(true);
	destinations = campusOneExtra.getNewDestinations();
    }
    
    private void resetModel() {
	spots = campusOneExtra.getNewSpots(true);
	destinations = campusOneExtra.getNewDestinations();
    }

    public ArrayList<Node> getSpotsCopy() {
	return new ArrayList<Node>(spots);
    }
    
    public ArrayList<Node> getDestinationsCopy() {
	return new ArrayList<Node>(destinations);
    }

    /**  -------------------------OPT BOTTLENECK-------------------------  */

    public ArrayList<MatchInfo> optBottleneckMatch() {
	ob = new OptBottleneck(spots, destinations);
	ArrayList<MatchInfo> obMatches = ob.run();
	return obMatches;
    }

    /**  -------------------------MAX FLOW-------------------------  */
    
    private Graph makeGraph(ArrayList<Node> rNodes, ArrayList<Node> sNodes) {
	Graph g = new Graph();
	int index = 1;
	for(Node s: sNodes) {
	    g.xNodes.put(index, s);
	    g.addEdgeFromSource(index);
	    index++;
	}
	index = 1;
	for(Node r: rNodes) {
	    g.yNodes.put(index, r);
	    g.addEdgeToSink(index);
	    index++;
	}
	//Add all the edges
	index = 1;
	int rIndex = 1;
	for(Node s: sNodes) {
	    //Reset server node index
	    rIndex = 1;
	    for(Node r: rNodes) {
		g.addEdge(index, rIndex, xyDistance(s.xPos, r.xPos, s.yPos, r.yPos));
		rIndex++;
	    }
	    index++;
	}
	return g;
    }

    //For this Model, there are more spots than destinations, so run max flow
    //until all request nodes(destinations) are matched, via runIncompleteSet()
    public ArrayList<Graph.Edge> maxFlowMatch() {
	mf = new MaxFlowBP(makeGraph(destinations, spots));
	ArrayList<Graph.Edge> mfMatching = mf.runIncompleteSet();
	return mfMatching;
    }

    /**  -------------------------PERMUTATION-------------------------  */

    public ArrayList<MatchInfo> runPermutationMatch() {
	pm = new PermutationMatch(spots, destinations);
	ArrayList<MatchInfo> pmMatching = pm.runAlgorithm();
	return pmMatching;
    }

     /**  -------------------------NEW ALGORITHMS-------------------------  */
    
    public ArrayList<MatchInfo> newAlgOneMatch() {
	ArrayList<MatchInfo> na1Matching = na.runAlgOne();
	return na1Matching;
    }

    public ArrayList<MatchInfo> newAlgTwoMatch() {
	ArrayList<MatchInfo> na2Matching = na.runAlgTwo();
	return na2Matching;
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
	int numRuns = 100;
	StringBuilder sb = new StringBuilder();
	double sumAvgOBCost = 0.0;
	double sumOBBneckCost = 0.0;
	double sumAvgMFCost = 0.0;
	double sumMFBneckCost = 0.0;
	double sumAvgPMCost = 0.0;
	double sumPMBneckCost = 0.0;
	double sumAvgNA1Cost = 0.0;
	double sumNA1BneckCost = 0.0;
	double sumAvgNA2Cost = 0.0;
	double sumNA2BneckCost = 0.0;
	double sumAvgGOCost = 0.0;
	double sumGOBneckCost = 0.0;
	sb.append("\n----------NUM RUNS - " + numRuns + "-------\n");
	sb.append("\n---------CAMPUS MODEL ONE (n = 100)----------\n\n");
	for(int j = 1; j <= numRuns; j++) {
	    //Randomize arrival order
	    resetModel();
	    sb.append("\n\n-----ITERATION " + j + "------");
	    System.out.println("\n\n---------ITERATION " + j + "--------\n\n");
	    ArrayList<MatchInfo> obMatching = optBottleneckMatch();
	    double[] obMatchingCosts = getCosts(obMatching);
	    sumAvgOBCost += obMatchingCosts[0];
	    sumOBBneckCost += obMatchingCosts[1];
	    ArrayList<Graph.Edge> mfMatching = maxFlowMatch();
	    double[] mfMatchingCosts = getMFCosts(mfMatching);
	    sumAvgMFCost += mfMatchingCosts[0];
	    sumMFBneckCost += mfMatchingCosts[1];
	    ArrayList<MatchInfo> pmMatching = runPermutationMatch();
	    double[] pmMatchingCosts = getCosts(pmMatching);
	    sumAvgPMCost += pmMatchingCosts[0];
	    sumPMBneckCost += pmMatchingCosts[1];
	    ArrayList<MatchInfo> na1Matching = newAlgOneMatch();
	    double[] na1MatchingCosts = getCosts(na1Matching);
	    sumAvgNA1Cost += na1MatchingCosts[0];
	    sumNA1BneckCost += na1MatchingCosts[1];
	    ArrayList<MatchInfo> na2Matching = newAlgTwoMatch();
	    double[] na2MatchingCosts = getCosts(na2Matching);
	    sumAvgNA2Cost += na2MatchingCosts[0];
	    sumNA2BneckCost += na2MatchingCosts[1];
	    ArrayList<MatchInfo> goMatching = greedyOnlineMatch();
	    double[] goMatchingCosts = getCosts(goMatching);
	    sumAvgGOCost += goMatchingCosts[0];
	    sumGOBneckCost += goMatchingCosts[1];
	    //Determine Winners
	    determineWinners(obMatchingCosts, mfMatchingCosts, pmMatchingCosts, na1MatchingCosts, na2MatchingCosts, goMatchingCosts);
	    sb.append("\nOPT BNECK AVG COST >> " + fourDecimals.format(obMatchingCosts[0]));
	    sb.append("   ||   OPT BNECK  COST >> " + fourDecimals.format(obMatchingCosts[1]));
	    sb.append("\nMAX FLOW AVG COST >> " + fourDecimals.format(mfMatchingCosts[0]));
	    sb.append("   ||   MAX FLOW WORST COST >> " + fourDecimals.format(mfMatchingCosts[1]));
	    sb.append("\n\nPERMUTATION AVG COST >> " + fourDecimals.format(pmMatchingCosts[0]));
	    sb.append("   ||   PERMUATION WORST COST >> " + fourDecimals.format(pmMatchingCosts[1]));
	    sb.append("\nNEW ALG 1 AVG COST >> " + fourDecimals.format(na1MatchingCosts[0]));
	    sb.append("   ||   NEW ALG 1 WORST COST >> " + fourDecimals.format(na1MatchingCosts[1]));
	    sb.append("\nNEW ALG 2 AVG COST >> " + fourDecimals.format(na2MatchingCosts[0]));
	    sb.append("   ||   NEW ALG 2 WORST COST >> " + fourDecimals.format(na2MatchingCosts[1]));
	    sb.append("\nGREEDY AVG COST >> " + fourDecimals.format(goMatchingCosts[0]));
	    sb.append("   ||   GREEDY WORST COST >> " + fourDecimals.format(goMatchingCosts[1]));
	}	
	sb.append("\n\n-----------------------AVERAGE COSTS:-----------------------\n\n");
	sb.append("\n-------------------------Avg. OPT BNECK Cost >> " + Double.valueOf(fourDecimals.format(sumAvgOBCost/numRuns)));
	sb.append("\n-------------------------Avg. MAX FLOW Cost >> " + Double.valueOf(fourDecimals.format(sumAvgMFCost/numRuns)));
	sb.append("\n-------------------------Avg. PERMUTATION Cost >> " + Double.valueOf(fourDecimals.format(sumAvgPMCost/numRuns)));
	sb.append("\n-------------------------Avg. NEW ALG 1 Cost >> " + Double.valueOf(fourDecimals.format(sumAvgNA1Cost/numRuns)));
	sb.append("\n-------------------------Avg. NEW ALG 2 Cost >> " + Double.valueOf(fourDecimals.format(sumAvgNA2Cost/numRuns)));
	sb.append("\n-------------------------Avg. GREEDY Cost >> " + Double.valueOf(fourDecimals.format(sumAvgGOCost/numRuns)));
sb.append("\n\n-----------------------AVERAGE BOTTLENECK COSTS:-----------------------\n\n");
	sb.append("\n-------------------------Avg. OPT BNECK Bottleneck Cost >> " + Double.valueOf(fourDecimals.format(sumOBBneckCost/numRuns)));
	sb.append("\n-------------------------Avg. MAX FLOW Bottleneck Cost >> " + Double.valueOf(fourDecimals.format(sumMFBneckCost/numRuns)));
	sb.append("\n-------------------------Avg. PERMUTATION Bottleneck Cost >> " + Double.valueOf(fourDecimals.format(sumPMBneckCost/numRuns)));
	sb.append("\n-------------------------Avg. NEW ALG 1 Bottleneck Cost >> " + Double.valueOf(fourDecimals.format(sumNA1BneckCost/numRuns)));
	sb.append("\n-------------------------Avg. NEW ALG 2 Bottleneck Cost >> " + Double.valueOf(fourDecimals.format(sumNA2BneckCost/numRuns)));
	sb.append("\n-------------------------Avg. GREEDY Bottleneck Cost >> " + Double.valueOf(fourDecimals.format(sumGOBneckCost/numRuns)));
	sb.append("\n\nFOR CAMPUS ONE (no additional spaces):\n");
	sb.append("\nOPT BNECK WINS " + "\n\nAVERAGE >> " + obWins[0] + "\nWORST >> " + obWins[1] + "\nBEST >> " + obWins[2]);
	sb.append("\n\nMAX FLOW WINS " + "\n\nAVERAGE >> " + mfWins[0] + "\nWORST >> " + mfWins[1] + "\nBEST >> " + mfWins[2]);
	sb.append("\n\nPERMUTATION WINS " + "\n\nAVERAGE >> " + pmWins[0] + "\nWORST >> " + pmWins[1] + "\nBEST >> " + pmWins[2]);
	sb.append("\n\nNEW ALG 1 WINS " + "\n\nAVERAGE >> " + na1Wins[0] + "\nWORST >> " + na1Wins[1] + "\nBEST >> " + na1Wins[2]);
	sb.append("\n\nNEW ALG 2 WINS " + "\n\nAVERAGE >> " + na2Wins[0] + "\nWORST >> " + na2Wins[1] + "\nBEST >> " + na2Wins[2]);
	sb.append("\n\nGREEDY WINS " + "\n\nAVERAGE >> " + goWins[0] + "\nWORST >> " + goWins[1] + "\nBEST >> " + goWins[2]);
	try {
	    FileWriter fstream = new FileWriter("results/CAMPUS_ONE_EXTRACAP_RESULTS.txt");
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

    /**
     * Same as getCosts(ArrayList<MatchInfo>), but for Edges (in Graph.java),
     * which are used for the Max Flow Alg
     */
    private double[] getMFCosts(ArrayList<Graph.Edge> edges) {
	double sumCost = 0.0;
	double worstCost = 0.0;
	double bestCost = Double.MAX_VALUE;
	for(Graph.Edge e: edges) {
	    if(e.getDistance() > worstCost)
		worstCost = e.getDistance();
	    if(e.getDistance() < bestCost)
		bestCost = e.getDistance();
	    sumCost += e.getDistance();
	}
	double avgCost = sumCost/edges.size();
	double[] toReturn = {avgCost, worstCost, bestCost};
	return toReturn;
    }

    private void determineWinners(double[] obCosts, double[] mfCosts, double[] pmCosts, double[] na1Costs, double[] na2Costs, double[] goCosts) {
	determineWinner(obCosts[0], mfCosts[0], pmCosts[0], na1Costs[0], na2Costs[0], goCosts[0], 0);
	determineWinner(obCosts[1], mfCosts[1], pmCosts[0], na1Costs[1], na2Costs[1], goCosts[1], 1);
	determineWinner(obCosts[2], mfCosts[2], pmCosts[0], na1Costs[2], na2Costs[2], goCosts[2], 2);
    }

    /**
     *Each alg gets 1 point if its cost is the minimum (in avg, worst, and best)
     *
     *Type is the index of algorithm's win array, or the "type" of wins per that objective
     *Type 0 = avg cost, 1 = worst cost, 2 = best cost
     */
    private void determineWinner(double obCost, double mfCost, double pmCost, double na1Cost, double na2Cost, double goCost, int type) {
	double avgWinner = Math.min(Math.min(Math.min(obCost, mfCost), Math.min(pmCost, na1Cost)), Math.min(na2Cost, goCost));
	if(avgWinner == obCost)
	    obWins[type]++;
	if(avgWinner == mfCost)
	    mfWins[type]++;
	if(avgWinner == pmCost)
	    pmWins[type]++;
	if(avgWinner == na1Cost)
	    na1Wins[type]++;
	if(avgWinner == na2Cost)
	    na2Wins[type]++;
	if(avgWinner == goCost)
	    goWins[type]++;
    }

    public static void main(String[] args) {
	TestCampusOneExtra tc1e = new TestCampusOneExtra();
	tc1e.test();
    }
}