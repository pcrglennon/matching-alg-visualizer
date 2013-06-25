import java.io.BufferedWriter;
import java.io.FileWriter;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Random;

public class PermGreedyGrid1060 {

    private int numberNodes;
    private int distanceRange;

    private ArrayList<Node> sNodes;
    private ArrayList<Node> rNodes;

    private PermutationMatch pm;

    public PermGreedyGrid1060(int numberNodes, int distanceRange) {
	this.numberNodes = numberNodes;
	this.distanceRange = distanceRange;
	sNodes = new ArrayList<Node>(numberNodes);
	rNodes = new ArrayList<Node>(numberNodes);
	newRandomNodes();
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

    public ArrayList<Node> getSNodes() {
	return new ArrayList<Node>(sNodes);
    }
    
    public ArrayList<Node> getRNodes() {
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

     /**  -------------------------PERMUTATION-------------------------  */

    public ArrayList<MatchInfo> runPermutationMatch() {
	pm = new PermutationMatch(sNodes, rNodes);
	ArrayList<MatchInfo> pmMatching = pm.runAlgorithm();
	return pmMatching;
    }

     /**  -------------------------GREEDY ONLINE-------------------------  */

    public MatchInfo[] greedyOnlineMatch() {
	MatchInfo[] matches = new MatchInfo[numberNodes];
	double maxDistance = Double.MAX_VALUE;
	ArrayList<Node> sNodesCopy = getSNodes();
	int index = 0;
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
	    matches[index] = new MatchInfo(r, sNodesCopy.get(selectedSNodeIndex), minDistance);
	    sNodesCopy.remove(selectedSNodeIndex);
	    index++;
	}
	return matches;
    }

    /**
     * Returns the distance between two points w/ (x,y) coordinates
     */

    private double xyDistance(int x1, int x2, int y1, int y2) {
	return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    public static void main(String[] args) {
	PermGreedyGrid1060 pgg = new PermGreedyGrid1060(10, 30);
	DecimalFormat fourDecimals = new DecimalFormat("#.####");
	int numRuns = 50;
	for(int i = 10; i <= 60; i += 10) {
	    StringBuilder sb = new StringBuilder();
	    double sumAvgPMCost = 0.0;
	    double sumWorstPMCost = 0.0;
	    double sumAvgGOCost = 0.0;
	    double sumWorstGOCost = 0.0;
	    int pmAvgWins = 0;
	    int pmWorstWins = 0;
	    int pmBestWins = 0;
	    int goAvgWins = 0;
	    int goWorstWins = 0;
	    int goBestWins = 0;
	    pgg.setNumberNodes(i);
	    int range = (int)Math.sqrt(90*i);
	    pgg.setDistanceRange(range);
	    sb.append("\n\n---------NEW NODE SIZE(" + i + ")----------\n\n");
	    System.out.println("\n\n---------NEW NODE SIZE(" + i + ")----------");
	    for(int j = 1; j <= numRuns; j++) {
		pgg.newRandomNodes();
		sb.append("\n\n------NODE SIZE("+i+") ITERATION " + j + "------");
		System.out.println("\n\n------NODE SIZE("+i+") ITERATION " + j + "------\n\n");
		ArrayList<MatchInfo> pmMatch = pgg.runPermutationMatch();
		double pmCost = 0.0;
		double worstPMCost = 0.0;
		double bestPMCost = Double.MAX_VALUE;
		for(MatchInfo mi: pmMatch) {
		    if(mi.distance > worstPMCost) 
			worstPMCost = mi.distance;
		    if(mi.distance < bestPMCost)
			bestPMCost = mi.distance;
		    pmCost += mi.distance;
		}
		double avgPMCost  = pmCost/i;
		sumAvgPMCost += avgPMCost;
		sumWorstPMCost += worstPMCost;
		sb.append("\n\nPERMUTATION AVG COST >> " + fourDecimals.format(avgPMCost));
		MatchInfo[] goMatch = pgg.greedyOnlineMatch();
		double goCost = 0.0;
		double worstGOCost = 0.0;
		double bestGOCost = Double.MAX_VALUE;
		for(MatchInfo m: goMatch) {
		    if(m.distance > worstGOCost)
			worstGOCost = m.distance;
		    if(m.distance < bestGOCost)
			bestGOCost = m.distance;
		    goCost += m.distance;
		}
		double avgGOCost = goCost/i;
		//Determine Winners
		if(avgGOCost > avgPMCost) {
		    pmAvgWins++;
		    System.out.println("PM is AVG Winner >> " + pmAvgWins);
		} else {
		    goAvgWins++;
		    System.out.println("GO is AVG Winner >> " + goAvgWins);
		}
		if(worstGOCost > worstPMCost) {
		    pmWorstWins++;
		    System.out.println("PM is WORST Winner >> " + pmWorstWins);
		} else {
		    goWorstWins++;
		    System.out.println("GO is WORST Winner >> " + goWorstWins);
		}
		if(bestGOCost > bestPMCost) {
		    pmBestWins++;
		    System.out.println("PM is BEST Winner >> " + pmBestWins);
		} else {
		    goBestWins++;
		    System.out.println("GO is BEST Winner >> " + goBestWins);
		}
		sumAvgGOCost += avgGOCost;
		sumWorstGOCost += worstGOCost;
		sb.append("   ||   GREEDY AVG COST >> " + fourDecimals.format(avgGOCost));
		sb.append("\nPERMUATION WORST COST >> " + fourDecimals.format(worstPMCost));
		sb.append("   ||   GREEDY WORST COST >> " + fourDecimals.format(worstGOCost));
	    }	
	    sb.append("\n\n\n-------------------------FOR NODE SIZE(" + i + ")");
	    sb.append("\n-------------------------Avg. PM Cost >> " + Double.valueOf(fourDecimals.format(sumAvgPMCost/numRuns)));
	    sb.append("\n-------------------------Avg. GO Cost >> " + Double.valueOf(fourDecimals.format(sumAvgGOCost/numRuns)));
	    sb.append("\n-------------------------Avg. Worst PM Cost >> " + Double.valueOf(fourDecimals.format(sumWorstPMCost/numRuns)));
	    sb.append("\n-------------------------Avg. Worst GO Cost >> " + Double.valueOf(fourDecimals.format(sumWorstGOCost/numRuns)));
	    sb.append("\nPERMUTATION WINS " + "\n\nAVERAGE >> " + pmAvgWins + "\nWORST >> " + pmWorstWins + "\nBEST >> " + pmBestWins + "\n\n");
	    sb.append("\nGREEDY ONLINE WINS " + "\n\nAVERAGE >> " + goAvgWins + "\nWORST >> " + goWorstWins + "\nBEST >> " + goBestWins);
	    try {
		FileWriter fstream = new FileWriter("grid_results/PM_GO_GRID_TALLY_RESULTS_" + i + ".txt");
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(sb.toString());
		out.close();
	    } catch (Exception e) {
				
	    }
	}
    }
}