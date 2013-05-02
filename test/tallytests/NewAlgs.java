import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class NewAlgs {
    
    private ArrayList<Node> sNodes; // Parking spots
    private ArrayList<Node> rNodes; // Cars w/ destinations
    private int threshold;

    //Desirability score of each server node (parking spot)
    private HashMap<Node, Double> dScores;
    //To use as a copy of dScores, which are updated at each match by AlgTwo
    private HashMap<Node, Double> dynamicDScores;
    private ArrayList<Node> unmatchedSNodes;
    
    public NewAlgs() {

    }

    public NewAlgs(ArrayList<Node> sNodes, ArrayList<Node> rNodes) {
	this.sNodes = sNodes;
	this.rNodes = rNodes;
	calculateDScores();
    }

    /**--------------------------ALG ONE ---------------------*/

    /**
    public void initModel() {
	calculateDScores();
    }
    */

    public ArrayList<MatchInfo> runAlgOne() {
	//Shuffle order of arrivals
	//Create copy of sNodes (need to remove nodes when matched)
	ArrayList<Node> sNodesCopy = new ArrayList<Node>(sNodes);
	ArrayList<MatchInfo> matches = new MatchInfo[sNodesCopy.size()];
	double maxDistance = Double.MAX_DISTANCE;
	for(Node r: rNodes) {
	    //Only one node left to match
	    if(sNodesCopy.size() == 1) {
		Node lastSNode = sNodesCopy.get(0);
		double lastMatchDist = xyDistance(r.xPos, lastSNode.xPos, r.yPos, lastSNode.yPos);
		matches.add(new MatchInfo(r, lastSNode, lastMatchDist));
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
	    //Re-loop to find the second smallest distance.  Innefficient, maybe fix later
	    for(int i = 0; i < sNodesCopy.size(); i++) {
		double dist = xyDistance(r.xPos, sNodesCopy.get(i).xPos, r.yPos, sNodesCopy.get(i).yPos);
		if(dist < minDistanceTwo && i != selectedIndexOne) {
		    minDistanceTwo = dist;
		    selectedIndexTwo = i;
		}
	    }
	    //If D. Score of closer node is above threshold, choose that node
	    if(dScores.get(sNodesCopy.get(selectedIndexOne)) > threshold) {
		matches.add(new MatchInfo(r, sNodesCopy.get(selectedIndexOne), minDistanceOne));
		sNodesCopy.remove(selectedIndexOne);
	    } else { // else choose the farther node
		matches.add(new MatchInfo(r, sNodesCopy.get(selectedIndexTwo), minDistanceTwo));
		sNodesCopy.remove(selectedIndexTwo);
	    }
	}
	return matches;
    }

    /**------------------------ALG TWO-----------------------*/

    public ArrayList<MatchInfo> runAlgTwoPM() {
	//Create copy of sNodes (need to remove nodes when matched)
	ArrayList<Node> sNodesCopy = new ArrayList<Node>(sNodes);
	//Unmatched S Nodes - originally full - to be updated by updateDScores()
	unmatchedSNodes = new ArrayList<Node>(sNodes);
	//Create copy of dScores (need to alter dScores after destinations are matched)
	dynamicDScores = new HashMap<Node, Double>(dScores);
	ArrayList<MatchInfo> matches = new MatchInfo[sNodesCopy.size()];
	for(Node r: rNodes) {
	    //Only one node left to match
	    if(sNodesCopy.size() == 1) {
		Node lastSNode = sNodesCopy.get(0);
		double lastMatchDist = xyDistance(r.xPos, lastSNode.xPos, r.yPos, lastSNode.yPos);
		matches.add(new MatchInfo(r, lastSNode, lastMatchDist));
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
	    //If D. Score of closer node is above threshold, choose that node
	    if(dynamicDScores.get(sNodesCopy.get(selectedIndexOne)) > threshold) {
		matches.add(new MatchInfo(r, sNodesCopy.get(selectedIndexOne), minDistanceOne));
		sNodesCopy.remove(selectedIndexOne);
		unmatchedSNodes.remove(selectedIndexOne);
	    } else { // else choose the farther node
		matches.add(new MatchInfo(r, sNodesCopy.get(selectedIndexTwo), minDistanceTwo));
		sNodesCopy.remove(selectedIndexTwo);
		unmatchedSNodes.remove(selectedIndexTwo);
	    }
	    //Update the D. Scores of each server node, to reflect that request
	    //node r has been matched
	    updateDScores(r);
	}
	return matches;
    }

    public void setThreshold(int threshold) {
	this.threshold = threshold;
    }

    public void calculateDScores() {
	dScores = new HashMap<Node, Double>();
	for(Node s: sNodes) {
	    double sumDist = 0;
	    for(Node r: rNodes) {
		sumDist += xyDistance(s.xPos, r.xPos, s.yPos, r.yPos);
	    }
	    dScores.put(s, sumDist/sNodes.size());
	    System.out.println(s + " >> " + sumDist/sNodes.size());
	}
    }
    
    private void updateDScores(Node matched) {
	for(Node s: unmatchedSNodes) {
	    int nodesUnmatched = unmatchedSNodes.size();
	    double sumDist = dynamicDScores.get(s) * (nodesUnmatched+1);
	    double rDist = xyDistance(s.xPos, matched.xPos, s.yPos, matched.yPos);
	    sumDist -= xyDistance(s.xPos, matched.xPos, s.yPos, matched.yPos);
	    dynamicDScores.put(s, sumDist/nodesUnmatched);
	}
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

    public static void main(String[] args) {
	NewAlgs na = new NewAlgs();
	na.initModel();
	na.calculateDScores();
    }
}