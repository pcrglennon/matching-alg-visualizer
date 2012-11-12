import java.util.ArrayList;
import java.util.Random;

public class MatchingAlgorithms {

    private final int DISTANCE_RANGE = 20;

    private int numberNodes;
    private int maxDistance;

    private ArrayList<int[]> reqNodes;
    private ArrayList<int[]> servNodes;

    public MatchingAlgorithms(int numberNodes, int maxDistance) {
	this.numberNodes = numberNodes;
	this.maxDistance = maxDistance;
	reqNodes = new ArrayList<int[]>(numberNodes);
	servNodes = new ArrayList<int[]>(numberNodes);
    }

    public void newRandomNodes() {
	reqNodes.clear();
	servNodes.clear();
	Random rand = new Random();
	for(int i = 0; i < numberNodes; i++) {
	    int[] randReqNode = {rand.nextInt(DISTANCE_RANGE), rand.nextInt(DISTANCE_RANGE)};
	    reqNodes.add(randReqNode);
	    int[] randServNode = {rand.nextInt(DISTANCE_RANGE), rand.nextInt(DISTANCE_RANGE)};
	    servNodes.add(randServNode);
	}
    }

    public ArrayList<int[]> getReqNodes() {
	return new ArrayList<int[]>(reqNodes);
    }
    
    public ArrayList<int[]> getServNodes() {
	return new ArrayList<int[]>(servNodes);
    }

     /**  -------------------------GREEDY ONLINE-------------------------  */

    //TODO - Double check this w/ real examples

    public MatchInfo[] greedyOnlineMatch() {
	MatchInfo[] matches = new MatchInfo[numberNodes];
	ArrayList<int[]> servNodesCopy = getServNodes();
	int maxDistance = xyDistance(0, DISTANCE_RANGE, 0, DISTANCE_RANGE);  //Max possible distance
	for(int i = 0; i < numberNodes; i++) {
	    int minDistance = maxDistance;
	    int selectedNodeIndex = 0;
	    for(int j = 0; j < servNodesCopy.size(); j++) {
		int dist = xyDistance(reqNodes.get(i)[0], servNodesCopy.get(j)[0], reqNodes.get(i)[1], servNodesCopy.get(j)[1]);
		if(dist < minDistance) {
		    minDistance = dist;
		    selectedNodeIndex = j;
		}
	    }
	    matches[i] = new MatchInfo(reqNodes.get(i), servNodes.get(selectedNodeIndex), minDistance);
	    servNodesCopy.remove(selectedNodeIndex);
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
	for(int i = 0; i < reqNodes.size(); i++) {
	    int[] rNode = reqNodes.get(i);
	    for(int j = 0; j < servNodes.size(); j++) {
		int[] sNode = servNodes.get(j);
		double dist = xyDistance(rNode[0], sNode[0], rNode[1], sNode[1]);
		allMatches.add(new MatchInfo(rNode, sNode, dist));
	    }
	}
	return allMatches;
    }

    public ArrayList<MatchInfo> removeMatchedNodes(ArrayList<MatchInfo> allMatches, int[] rNode, int[] sNode) {
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

    private int xyDistance(int x1, int x2, int y1, int y2) {
	return (int)Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

}