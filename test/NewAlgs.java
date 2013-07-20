import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * The two Greedy Threshold Algorithms, which were originally just referred
 * to as "The New Algorithms"
 *
 * The two algorithms are nearly identical.  Both are based on the concept of
 * the "desirability score" of a parking spot/server node, which is simply the
 * average distance of the spot to all destinations/request nodes.  A lower 
 * desirabilty score means the spot is more desirable
 *
 * The algorithms find the two closest spots for each destination, and will pick
 * the closer of the two IF its desirabilty score is above a certain threshold.
 * The determination of the threshold is dependent on the model the algorithms
 * run on, and tests were done to determine the optimal threshold for each
 * model tested.  See the tests (TestCampusOne, TestCampusTwo, etc.) for the
 * threshold for each Parking Model.  The nature of these algorithms, which
 * must know the location of each destination in advance (to calculate the
 * desirabilty scores) means it can only be used on models with fixed
 * destinations, like the parking models
 *
 * The difference between the two algorithms is that NewAlgTwo (or GT2) will 
 * recalculate the desirabilty score of each spot every time a spot is matched
 * to a destination, while GT1 uses the original desirabilty scores made
 * before any matchings
 */
public class NewAlgs {
    
    private ArrayList<Node> sNodes; // Parking spots
    private ArrayList<Node> rNodes; // Cars w/ destinations
    private int threshold;

    //Desirability score of each server node (parking spot)
    private HashMap<String, Double> dScores;
    //To use as a copy of dScores, which are updated at each match by AlgTwo
    private HashMap<String, Double> dynamicDScores;
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
     * Run NewAlgOne, or GT1.  See comments at top of class for description
     */
    public ArrayList<MatchInfo> runAlgOne() {
	//Create copy of sNodes (need to remove nodes when matched)
	ArrayList<Node> sNodesCopy = new ArrayList<Node>(sNodes);
	ArrayList<MatchInfo> matches = new ArrayList<MatchInfo>(sNodesCopy.size());
	double maxDistance = Double.MAX_VALUE;
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
	    Node s = sNodesCopy.get(selectedIndexOne);
	    if(dScores.get(sNodesCopy.get(selectedIndexOne).id) > threshold) {
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

    /**
     * Run NewAlgTwo, or GT2
     */
    public ArrayList<MatchInfo> runAlgTwo() {
	//Create copy of sNodes (need to remove nodes when matched)
	ArrayList<Node> sNodesCopy = new ArrayList<Node>(sNodes);
	//Unmatched S Nodes - originally full - to be updated by updateDScores()
	unmatchedSNodes = new ArrayList<Node>(sNodes);
	//Create copy of dScores (need to alter dScores after destinations are matched)
	dynamicDScores = new HashMap<String, Double>(dScores);
	ArrayList<MatchInfo> matches = new ArrayList<MatchInfo>(sNodesCopy.size());
	double maxDistance = Double.MAX_VALUE;
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
	    if(dynamicDScores.get(sNodesCopy.get(selectedIndexOne).id) > threshold) {
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

    /**
     * Calculate the desirabilty score of each parking spot/server node
     * as the average distance from the spot to each destination/
     * request node
     */
    public void calculateDScores() {
	dScores = new HashMap<String, Double>();
	for(Node s: sNodes) {
	    double sumDist = 0;
	    for(Node r: rNodes) {
		sumDist += xyDistance(s.xPos, r.xPos, s.yPos, r.yPos);
	    }
	    dScores.put(s.id, sumDist/sNodes.size());
	}
    }
    
    /**
     * Update the D. Scores for each unmatched spot to reflect that a
     * destination, which has just been matched, is now unavailable
     */
    private void updateDScores(Node matched) {
	for(Node s: unmatchedSNodes) {
	    int nodesUnmatched = unmatchedSNodes.size();
	    double sumDist = dynamicDScores.get(s.id) * (nodesUnmatched+1);
	    double rDist = xyDistance(s.xPos, matched.xPos, s.yPos, matched.yPos);
	    sumDist -= xyDistance(s.xPos, matched.xPos, s.yPos, matched.yPos);
	    dynamicDScores.put(s.id, sumDist/nodesUnmatched);
	}
    }

    /**  -------------------------GREEDY ONLINE-------------------------  */

    public ArrayList<MatchInfo> greedyOnlineMatch() {
	double maxDistance = Double.MAX_VALUE;
	ArrayList<Node> sNodesCopy = new ArrayList<Node>(sNodes);
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
    
    /**
    public static void main(String[] args) {
	NewAlgs na = new NewAlgs();
	na.calculateDScores();
    }
    */
}