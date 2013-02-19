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

    public ArrayList<Graph.Edge> runPermutationMatch() {
	pm = new PermutationMatch(sNodes, rNodes);
	ArrayList<Graph.Edge> pmMatching = pm.runAlgorithm();
	return pmMatching;
    }

     /**  -------------------------GREEDY ONLINE-------------------------  */

    public MatchInfo[] greedyOnlineMatch() {
	MatchInfo[] matches = new MatchInfo[numberNodes];
	int maxDistance = xyDistance(0, DISTANCE_RANGE, 0, DISTANCE_RANGE);  //Max possible distance
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
    private int xyDistance(int x1, int x2, int y1, int y2) {
	return (int)Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

}