import java.util.ArrayList;
import java.util.Random;

public class MatchingAlgorithms {

    private final int DISTANCE_RANGE = 20;

    private int numberNodes;
    private int maxDistance;

    private ArrayList<Node> sNodes;
    private ArrayList<Node> rNodes;

    private MaxFlowBP mf;

    private PermutationMatch pm;

    public MatchingAlgorithms(int numberNodes, int maxDistance) {
	this.numberNodes = numberNodes;
	this.maxDistance = maxDistance;
	sNodes = new ArrayList<Node>(numberNodes);
	rNodes = new ArrayList<Node>(numberNodes);
    }

    public void newRandomNodes() {
	sNodes.clear();
	rNodes.clear();
	Random rand = new Random();
	for(int i = 0; i < numberNodes; i++) {
	    sNodes.add(new Node("x" + (i+1), rand.nextInt(DISTANCE_RANGE), rand.nextInt(DISTANCE_RANGE)));
	    rNodes.add(new Node("y" + (i+1), rand.nextInt(DISTANCE_RANGE), rand.nextInt(DISTANCE_RANGE)));
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

    /**  -------------------------MAX FLOW-------------------------  */
    
    private Graph makeGraph(ArrayList<Node> sNodes, ArrayList<Node> rNodes) {
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

    public ArrayList<Graph.Edge> runMaxFlowBP() {
	mf = new MaxFlowBP(makeGraph(sNodes, rNodes));
	ArrayList<Graph.Edge> mfMatching = mf.runAlgorithm();
	return mfMatching;
    }

    public ArrayList<MatchInfo> runPermutationMatch() {
	pm = new PermutationMatch(sNodes, rNodes);
	ArrayList<MatchInfo> pmMatching = pm.runAlgorithm();
	return pmMatching;
    }

     /**  -------------------------GREEDY ONLINE-------------------------  */

    public MatchInfo[] greedyOnlineMatch() {
	MatchInfo[] matches = new MatchInfo[numberNodes];
	double maxDistance = xyDistance(0, DISTANCE_RANGE, 0, DISTANCE_RANGE);  //Max possible distance
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
	double maxDistance = xyDistance(0, DISTANCE_RANGE, 0, DISTANCE_RANGE);  //Max possible distance
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
	    allMatches = removeMatchedNodes(allMatches, match.sNode, match.rNode);
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
	for(int i = 0; i < sNodes.size(); i++) {
	    Node sNode = sNodes.get(i);
	    for(int j = 0; j < rNodes.size(); j++) {
		Node rNode = rNodes.get(j);
		double dist = xyDistance(sNode.xPos, rNode.xPos, sNode.yPos, rNode.yPos);
		allMatches.add(new MatchInfo(sNode, rNode, dist));
	    }
	}
	return allMatches;
    }

    public ArrayList<MatchInfo> removeMatchedNodes(ArrayList<MatchInfo> allMatches, Node sNode, Node rNode) {
	ArrayList<MatchInfo> matchesToRemove = new ArrayList<MatchInfo>();
	for(MatchInfo match: allMatches) {
	    if (match.sNode.equals(sNode) || match.rNode.equals(rNode)) {
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

}