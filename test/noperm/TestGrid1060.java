import java.io.BufferedWriter;
import java.io.FileWriter;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Random;

/**
 * Runs Max-Flow, Opt Bottleneck,  both New Algs, and Greedy on The 2D Grid 
 * w/ n = 10 up to 60, by increments of 10
 */

public class TestGrid1060 {

    private int numberNodes;
    private int distanceRange;

    private ArrayList<Node> sNodes;
    private ArrayList<Node> rNodes;

    private OptBottleneck ob;
    private MaxFlowBP mf;

    //Store win counts for Average Cost, Worst Cost, and Best Cost (in that order)
    //i.e. mfWins[0] = number of Average Cost Wins
    private int[] obWins = {0,0,0};
    private int[] mfWins = {0,0,0};
    private int[] goWins = {0,0,0};

    public TestGrid1060() {
	sNodes = new ArrayList<Node>(numberNodes);
	rNodes = new ArrayList<Node>(numberNodes);
    }

    public void newRandomNodes() {
	sNodes.clear();
	rNodes.clear();
	Random rand = new Random();
	for(int i = 0; i < numberNodes; i++) {
	    sNodes.add(new Node("x" + (i+1), rand.nextInt(distanceRange), rand.nextInt(distanceRange)));
	    rNodes.add(new Node("y" + (i+1), rand.nextInt(distanceRange), rand.nextInt(distanceRange)));
	}
    }

    public ArrayList<Node> getSNodesCopy() {
	return new ArrayList<Node>(sNodes);
    }
    
    public ArrayList<Node> getRNodesCopy() {
	return new ArrayList<Node>(rNodes);
    }

    public void setNumberNodes(int numberNodes) {
	this.numberNodes = numberNodes;
    }

    public int getDistanceRange() {
	return distanceRange;
    }

    public void setDistanceRange(int distanceRange) {
	this.distanceRange = distanceRange;
    }

    /**  -------------------------OPT BOTTLENECK-------------------------  */

    public ArrayList<MatchInfo> optBottleneckMatch() {
	ob = new OptBottleneck(sNodes, rNodes);
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

    public ArrayList<Graph.Edge> maxFlowMatch() {
	mf = new MaxFlowBP(makeGraph(rNodes, sNodes));
	ArrayList<Graph.Edge> mfMatching = mf.runAlgorithm();
	return mfMatching;
    }

    /**  -------------------------GREEDY ONLINE-------------------------  */

    public ArrayList<MatchInfo> greedyOnlineMatch() {
	double maxDistance = Double.MAX_VALUE;
	ArrayList<Node> sNodesCopy = getSNodesCopy();
	ArrayList<MatchInfo> matches = new ArrayList<MatchInfo>(sNodesCopy.size());
	for(Node r: rNodes) {
	    double minDistance = maxDistance;
	    int selectedSNodeIndex = 0;
	    for(int i = 0; i < sNodesCopy.size(); i++) {
		double dist = xyDistance(r.xPos, sNodesCopy.get(i).xPos, r.yPos, sNodesCopy.get(i).yPos);
		if(dist < minDistance) {
		    minDistance = dist;
		    selectedSNodeIndex = i;
		}
	    }
	    matches.add(new MatchInfo(r, sNodesCopy.get(selectedSNodeIndex), minDistance));
	    sNodesCopy.remove(selectedSNodeIndex);
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
	DecimalFormat fourDecimals = new DecimalFormat("#.####");
	int numRuns = 80;
	for(int i = 10; i <= 60; i+= 10) {
	    System.out.println("\n\n\nNEW N = " + i);
	    StringBuilder sb = new StringBuilder();
	    sb.append("\n\n----------2D GRID - RESULTS FOR N = (" + i + ")----------\n\n");
	    setNumberNodes(i);
	    int range = (int)Math.sqrt(90*i);
	    setDistanceRange(range);
	    double sumAvgOBCost = 0.0;
	    double sumOBBneckCost = 0.0;
	    double sumAvgMFCost = 0.0;
	    double sumMFBneckCost = 0.0;
	    double sumAvgGOCost = 0.0;
	    double sumGOBneckCost = 0.0;
	    resetWinCounts();
	    for(int j = 1; j <= numRuns; j++) {
		newRandomNodes();
		System.out.println("\n\n---------ITERATION " + j + "----------");
		sb.append("\n\n---------ITERATION " + j + "----------\n\n");
		ArrayList<MatchInfo> obMatching = optBottleneckMatch();
		double[] obMatchingCosts = getCosts(obMatching);
		sumAvgOBCost += obMatchingCosts[0];
		sumOBBneckCost += obMatchingCosts[1];
		ArrayList<Graph.Edge> mfMatching = maxFlowMatch();
		double[] mfMatchingCosts = getMFCosts(mfMatching);
		sumAvgMFCost += mfMatchingCosts[0];
		sumMFBneckCost += mfMatchingCosts[1];
		ArrayList<MatchInfo> goMatching = greedyOnlineMatch();
		double[] goMatchingCosts = getCosts(goMatching);
		sumAvgGOCost += goMatchingCosts[0];
		sumGOBneckCost += goMatchingCosts[1];
		//Determine Winners
		determineWinners(obMatchingCosts, mfMatchingCosts, goMatchingCosts);
		sb.append("\nOPT BNECK AVG COST >> " + fourDecimals.format(obMatchingCosts[0]));
		sb.append("   ||   OPT BNECK WORST COST >> " + fourDecimals.format(obMatchingCosts[1]));
		sb.append("\nMAX FLOW AVG COST >> " + fourDecimals.format(mfMatchingCosts[0]));
		sb.append("   ||   MAX FLOW WORST COST >> " + fourDecimals.format(mfMatchingCosts[1]));
		sb.append("\nGREEDY AVG COST >> " + fourDecimals.format(goMatchingCosts[0]));
		sb.append("   ||   GREEDY WORST COST >> " + fourDecimals.format(goMatchingCosts[1]));
	    }	
	    sb.append("\n\n\n--------------------AVERAGE COSTS (i = " + i + "):-------------------\n\n");
	    sb.append("\n----------------------Avg. OPT BNECK Cost >> " + Double.valueOf(fourDecimals.format(sumAvgOBCost/numRuns)));
	    sb.append("\n----------------------Avg. MAX FLOW Cost >> " + Double.valueOf(fourDecimals.format(sumAvgMFCost/numRuns)));
	    sb.append("\n----------------------Avg. GREEDY Cost >> " + Double.valueOf(fourDecimals.format(sumAvgGOCost/numRuns)));
	    sb.append("\n\n-------------------AVERAGE BOTTLENECK COSTS (i = " + i + "):-----------------------\n\n");
	    sb.append("\n----------------------Avg. OPT BNECK Bottleneck Cost >> " + Double.valueOf(fourDecimals.format(sumOBBneckCost/numRuns)));
	    sb.append("\n----------------------Avg. MAX FLOW Bottleneck Cost >> " + Double.valueOf(fourDecimals.format(sumMFBneckCost/numRuns)));
	    sb.append("\n----------------------Avg. GREEDY Bottleneck Cost >> " + Double.valueOf(fourDecimals.format(sumGOBneckCost/numRuns)));
	    sb.append("\n\nWIN COUNTS (i = " + i + "):\n");
	    sb.append("\nOPT BNECK WINS " + "\n\nAVERAGE >> " + obWins[0] + "\nWORST >> " + obWins[1] + "\nBEST >> " + obWins[2]);
	    sb.append("\n\nMAX FLOW WINS " + "\n\nAVERAGE >> " + mfWins[0] + "\nWORST >> " + mfWins[1] + "\nBEST >> " + mfWins[2]);
	    sb.append("\n\nGREEDY WINS " + "\n\nAVERAGE >> " + goWins[0] + "\nWORST >> " + goWins[1] + "\nBEST >> " + goWins[2]);
	    try {
		FileWriter fstream = new FileWriter("results/grid/GRID_" + i + "_RESULTS.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(sb.toString());
		out.close();
	    } catch (Exception e) {
				
	    }
	} //End i for loop
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

    private void determineWinners(double[] obCosts, double[] mfCosts, double[] goCosts) {
	determineWinner(obCosts[0], mfCosts[0], goCosts[0], 0);
	determineWinner(obCosts[1], mfCosts[1], goCosts[1], 1);
	determineWinner(obCosts[2], mfCosts[2], goCosts[2], 2);
    }

    /**
     *Each alg gets 1 point if its cost is the minimum (in avg, worst, and best)
     *
     *Type is the index of algorithm's win array, or the "type" of wins per that objective
     *Type 0 = avg cost, 1 = worst cost, 2 = best cost
     */
    private void determineWinner(double obCost, double mfCost, double goCost, int type) {
	double avgWinner = Math.min(Math.min(obCost, mfCost), goCost);
	if(avgWinner == obCost)
	    obWins[type]++;
	if(avgWinner == mfCost)
	    mfWins[type]++;
	if(avgWinner == goCost)
	    goWins[type]++;
    }

    private void resetWinCounts() {
	obWins[0] = obWins[1] = obWins[2] = 0;
	mfWins[0] = mfWins[1] = mfWins[2] = 0;
	goWins[0] = goWins[1] = goWins[2] = 0;
    }

    public static void main(String[] args) {
	TestGrid1060 tg1060 = new TestGrid1060();
	tg1060.test();
    }
}