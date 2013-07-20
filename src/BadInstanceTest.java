import java.io.BufferedWriter;
import java.io.FileWriter;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Random;

public class BadInstanceTest {

    private int numberNodes;
    private int distanceRange;

    private ArrayList<Node> sNodes;
    private ArrayList<Node> rNodes;

    private MaxFlowBP mf;

    private PermutationMatch pm;

    public BadInstanceTest() {
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
	MatchInfo[] matches = new MatchInfo[rNodes.size()];
	int maxDistance = Integer.MAX_VALUE;
	ArrayList<Node> sNodesCopy = getSNodes();
	int index = 0;
	for(Node r: rNodes) {
	    int minDistance = maxDistance;
	    int selectedSNodeIndex = 0;
	    for(int i = 0; i < sNodesCopy.size(); i++) {
		int dist = xyDistance(r.xPos, sNodesCopy.get(i).xPos, r.yPos, sNodesCopy.get(i).yPos);
		if(dist < minDistance) {
		    minDistance = dist;
		    selectedSNodeIndex = i;
		}
	    }
	    matches[index] = new MatchInfo(r, sNodesCopy.get(selectedSNodeIndex), minDistance);
	    sNodesCopy.remove(selectedSNodeIndex);
	    index++;
	}
	/*
	for(int i = 0; i < numberNodes; i++) {
	    int minDistance = maxDistance;
	    int selectedNodeIndex = 0;
	    for(int j = 0; j < sNodes; j++) {
		int dist = xyDistance(rNodes.get(i).xPos, sNodesCopy.get(j)[0], rNodes.get(i)[1], sNodesCopy.get(j)[1]);
		if(dist < minDistance) {
		    minDistance = dist;
		    selectedNodeIndex = j;
		}
	    }
	    matches[i] = new MatchInfo(rNodes.get(i), sNodesCopy.get(selectedNodeIndex), minDistance);
	    sNodesCopy.remove(selectedNodeIndex);
	}
	*/
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

    private void makeBadInstanceSet() {
	sNodes.clear();
	rNodes.clear();
	sNodes.add(new Node("x1", 0, 0));
	sNodes.add(new Node("x2", 3, 0));
	sNodes.add(new Node("x3", 7, 0));
	//sNodes.add(new Node("x4", 15, 0));
	rNodes.add(new Node("y1", 2, 0));
	rNodes.add(new Node("y2", 4, 0));
	rNodes.add(new Node("y3", 8, 0));
	//rNodes.add(new Node("y4", 16,0));
    }

    /**
     * Returns the distance between two points w/ (x,y) coordinates
     */

    private int xyDistance(int x1, int x2, int y1, int y2) {
	return (int)Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    public static void main(String[] args) {
	BadInstanceTest bit = new BadInstanceTest();
	bit.makeBadInstanceSet();
	ArrayList<MatchInfo> pm = bit.runPermutationMatch();
	System.out.println("\n\nPERMUTATION");
	System.out.println(pm);
	System.out.println("\n\nGREEDY");
	for(MatchInfo mi: bit.greedyOnlineMatch()) {
	    System.out.println(mi);
	}
    }
}