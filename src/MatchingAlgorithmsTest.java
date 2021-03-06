import java.io.BufferedWriter;
import java.io.FileWriter;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Random;

public class MatchingAlgorithmsTest {

    private int numberNodes;
    private int distanceRange;

    private ArrayList<Node> sNodes;
    private ArrayList<Node> rNodes;

    private MaxFlowBP mf;

    private PermutationMatch pm;

    public MatchingAlgorithmsTest(int numberNodes, int distanceRange) {
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

    /**  -------------------------MAX FLOW-------------------------  */
    
    private Graph makeGraph(ArrayList<Node> rNodes, ArrayList<Node> sNodes) {
	Graph g = new Graph();
	int index = 1;
	for(Node r: rNodes) {
	    g.xNodes.put(index, r);
	    g.addEdgeFromSource(index);
	    index++;
	}
	index = 1;
	for(Node s: sNodes) {
	    g.yNodes.put(index, s);
	    g.addEdgeToSink(index);
	    index++;
	}
	//Add all the edges
	index = 1;
	int sIndex = 1;
	for(Node r: rNodes) {
	    //Reset server node index
	    sIndex = 1;
	    for(Node s: sNodes) {
		g.addEdge(index, sIndex, xyDistance(r.xPos, s.xPos, r.yPos, s.yPos));
		sIndex++;
	    }
	    index++;
	}
	return g;
    }

    public ArrayList<Graph.Edge> runMaxFlowBP() {
	MaxFlowBP mf = new MaxFlowBP(makeGraph(rNodes, sNodes));
	ArrayList<Graph.Edge> mfMatching = mf.runAlgorithm();
	return mfMatching;
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

        /**  ----------------------RANDOM GREEDY ONLINE----------------------  */

    /**
     * Determines the 2 closest matches for each Request Node, randomly selects one
     */
    public MatchInfo[] randomGreedyOnlineMatch() {
	Random rand = new Random();
	MatchInfo[] matches = new MatchInfo[numberNodes];
	double maxDistance = xyDistance(0, distanceRange, 0, distanceRange);  //Max possible distance
	ArrayList<Node> sNodesCopy = getSNodes();
	int index = 0;
	for(Node r: rNodes) {
	    //If only one node left to match, match to last s Node
	    if(sNodesCopy.size() == 1) {
		Node lastSNode = sNodesCopy.get(0);
		double lastMatchDist = xyDistance(r.xPos, lastSNode.xPos, r.yPos, lastSNode.yPos);
		matches[index] = new MatchInfo(r, lastSNode, lastMatchDist);
		break;
	    }
	    double minDistanceOne = maxDistance;
	    double minDistanceTwo = maxDistance;
	    int selectedIndexOne = 0;
	    int selectedIndexTwo = 0;
	    //Find the smallest distance
	    for(int i = 0; i < sNodesCopy.size(); i++) {
		double dist = xyDistance(r.xPos, sNodesCopy.get(i).xPos, r.yPos, sNodesCopy.get(i).yPos);
		if(dist < minDistanceOne) {
		    minDistanceOne = dist;
		    selectedIndexOne = i;
		}
	    }
	    //Re-loop to find the second smallest distance.  Far from the best way to do this!
	    for(int i = 0; i < sNodesCopy.size(); i++) {
		double dist = xyDistance(r.xPos, sNodesCopy.get(i).xPos, r.yPos, sNodesCopy.get(i).yPos);
		if(dist < minDistanceTwo && i != selectedIndexOne) {
		    minDistanceTwo = dist;
		    selectedIndexTwo = i;
		}
	    }
	    //Randomly select one of the two
	    boolean choice = rand.nextBoolean();
	    if(choice) {
		matches[index] = new MatchInfo(r, sNodesCopy.get(selectedIndexOne), minDistanceOne);
		sNodesCopy.remove(selectedIndexOne);

	    } else {
		matches[index] = new MatchInfo(r, sNodesCopy.get(selectedIndexTwo), minDistanceTwo);
		sNodesCopy.remove(selectedIndexTwo);
	    }
	    index++;
	}
	return matches;
    }

    /**  -------------------------GREEDY OFFLINE-------------------------  */

    public MatchInfo[] greedyOfflineMatch() {
	MatchInfo[] finalMatches = new MatchInfo[numberNodes];
	ArrayList<MatchInfo> allMatches = constructFullMatchingList();
	for(int i = 0; i < numberNodes; i++) {
	    MatchInfo match = findMinMatch(allMatches);
	    allMatches = removeMatchedNodes(allMatches, match.rNode, match.sNode);
	    finalMatches[i] = match;
	}
	return finalMatches;
    }

    private MatchInfo findMinMatch(ArrayList<MatchInfo> allMatches) {
	int minDistIndex = 0;
	double minDist = allMatches.get(0).distance;
	for(MatchInfo match: allMatches) {
	    if(match.distance < minDist) {
		minDistIndex = allMatches.indexOf(match);
		minDist = match.distance;
	    }
	}
	return allMatches.get(minDistIndex);
    }

    private ArrayList<MatchInfo> constructFullMatchingList() {
	ArrayList<MatchInfo> allMatches = new ArrayList<MatchInfo>();
	for(int i = 0; i < rNodes.size(); i++) {
	    Node rNode = rNodes.get(i);
	    for(int j = 0; j < sNodes.size(); j++) {
		Node sNode = sNodes.get(j);
		double dist = xyDistance(rNode.xPos, sNode.xPos, rNode.yPos, sNode.yPos);
		allMatches.add(new MatchInfo(rNode, sNode, dist));
	    }
	}
	return allMatches;
    }

    public ArrayList<MatchInfo> removeMatchedNodes(ArrayList<MatchInfo> allMatches, Node rNode, Node sNode) {
	ArrayList<MatchInfo> matchesToRemove = new ArrayList<MatchInfo>();
	for(MatchInfo match: allMatches) {
	    if (match.rNode.equals(rNode) || match.sNode.equals(sNode)) {
		matchesToRemove.add(match);
	    }
	}
	for(MatchInfo match: matchesToRemove) {
	    allMatches.remove(match);
	}
	return allMatches;
    }

    /**
     * Returns the distance between two points w/ (x,y) coordinates
     */
    private double xyDistance(int x1, int x2, int y1, int y2) {
	return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    public static void main(String[] args) {
		MatchingAlgorithmsTest mat = new MatchingAlgorithmsTest(10, 30);
		DecimalFormat fourDecimals = new DecimalFormat("#.####");
		double sumAvgPOA = 0;   
		int numRuns = 30;
	
		for(int i = 10; i <= 100; i += 10) {
			StringBuilder sb = new StringBuilder();
			//double worstPOA = 0.0;
			//double sumAvgMFCost = 0.0;
			//double sumAvgPMCost = 0.0;
			double sumAvgGOCost = 0.0;
			double sumWorstGOCost = 0.0;
			double sumAvgRGOCost = 0.0;
			double sumWorstRGOCost = 0.0;
			mat.setNumberNodes(i);
			int range = (int)Math.sqrt(90*i);
			mat.setDistanceRange(range);
			sb.append("\n\n---------NEW NODE SIZE(" + i + ")----------\n\n");
			System.out.println("\n\n---------NEW NODE SIZE(" + i + ")----------");
			for(int j = 1; j <= numRuns; j++) {
				mat.newRandomNodes();
				sb.append("\n\n------NODE SIZE("+i+") ITERATION " + j + "------");
				System.out.println("\n\n------NODE SIZE("+i+") ITERATION " + j + "------\n\n");
				/**
				ArrayList<Graph.Edge> mfMatch = mat.runMaxFlowBP();
				double mfCost = 0.0;
				for(Graph.Edge e: mfMatch) {
					mfCost += e.getDistance();
				}
				double avgMFCost  = mfCost/i;
				sumAvgMFCost += avgMFCost;
				sb.append("\n\nMAXFLOW AVG COST >> " + avgMFCost);
				*/
				/*
				ArrayList<MatchInfo> pmMatch = mat.runPermutationMatch();

				double pmCost = 0.0;
				for(MatchInfo mi: pmMatch) {
				    pmCost += mi.distance;
				}
				double avgPMCost  = pmCost/i;
				sumAvgPMCost += avgPMCost;
				sb.append("\n\nPERMUTATION AVG COST >> " + avgPMCost);
				*/
				MatchInfo[] goMatch = mat.greedyOnlineMatch();
				double goCost = 0.0;
				double worstGOCost = 0.0;
				for(MatchInfo m: goMatch) {
				    if(m.distance > worstGOCost)
					worstGOCost = m.distance;
				    goCost += m.distance;
				}
				double avgGOCost = goCost/i;
				sumAvgGOCost += avgGOCost;
				sumWorstGOCost += worstGOCost;
				sb.append("\n\nGREEDY AVG COST >> " + fourDecimals.format(avgGOCost));
				MatchInfo[] rgoMatch = mat.randomGreedyOnlineMatch();
				double rgoCost = 0.0;
				double worstRGOCost = 0.0;
				for(MatchInfo m: rgoMatch) {
				    if(m.distance > worstRGOCost)
					worstRGOCost = m.distance;
				    rgoCost += m.distance;
				}
				double avgRGOCost = rgoCost/i;
				sumAvgRGOCost += avgRGOCost;
				sumWorstRGOCost += worstRGOCost;
				sb.append("   ||   RANDOM GREEDY AVG COST >> " + fourDecimals.format(avgRGOCost));
				sb.append("\nGREEDY WORST COST >> " + fourDecimals.format(worstGOCost));
				sb.append("   ||   RANDOM GREEDY WORST COST >> " + fourDecimals.format(worstRGOCost));
				//double poa = Double.valueOf(fourDecimals.format(((goCost/i) / (mfCost/i))));
				//double poa = Double.valueOf(fourDecimals.format(((goCost/i) / (pmCost/i))));
				//sb.append("   ||   POA >> " + poa);
				//if(poa > worstPOA) {
				//worstPOA = poa;
				//}
			}	
			sb.append("\n\n\n-------------------------FOR NODE SIZE(" + i + ")");
			//sb.append("\n-------------------------Avg. MF Cost >> " + Double.valueOf(fourDecimals.format(sumAvgMFCost/numRuns)));
			//sb.append("\n-------------------------Avg. PM Cost >> " + Double.valueOf(fourDecimals.format(sumAvgPMCost/numRuns)));
			sb.append("\n-------------------------Avg. GO Cost >> " + fourDecimals.format(sumAvgGOCost/numRuns));
			sb.append("\n-------------------------Avg. RGO Cost >> " + fourDecimals.format(sumAvgRGOCost/numRuns));
			sb.append("\n-------------------------Avg. Worst GO Cost >> " + fourDecimals.format(sumWorstGOCost/numRuns));
			sb.append("\n-------------------------Avg. Worst RGO Cost >> " + fourDecimals.format(sumWorstRGOCost/numRuns));
			//double avgPOA = Double.valueOf(fourDecimals.format((sumAvgGOCost/(numRuns*i)) / (sumAvgMFCost/(numRuns*i))));
			//double avgPOA = Double.valueOf(fourDecimals.format((sumAvgGOCost/(numRuns*i)) / (sumAvgPMCost/(numRuns*i))));
			//sumAvgPOA += avgPOA;
			//sb.append("\n-------------------------Avg. POA >> " + avgPOA);
			//sb.append("\n-------------------------Worst POA >> " + worstPOA);
			try {
				FileWriter fstream = new FileWriter("RESULTS " + i + ".txt");
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(sb.toString());
				out.close();
			} catch (Exception e) {
				
			}
		}
	}

}