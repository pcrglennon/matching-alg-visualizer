import java.util.ArrayList;

/**
 * Encapsulates methods which run the Permutation Matching algorithm
 *
 * High-level description of the algorithm: for eaching arriving request node r(i),
 * run an optimal offline match (MaxFlowBP) using all server nodes and all present
 * request nodes to get partial matching m(i).  Permanently match r(i) to s(i), which
 * is the sole server node  matched in m(i) that was not matched in m(i-1).
 *
 * For more detail, see "Online Weighted Matching" by Bala Kalyanasundaram and Kirk
 * Pruhs, and "On-line Algorithms for Weighted Bipartite Matching and Stable Marriages"
 * by Samir Khuller, Stephen G. Mitchell, and Vijay V. Vazirani
 */
public class PermutationMatch {

    private Graph g;
    private ArrayList<Node> sNodes;
    private ArrayList<Node> rNodes;
    //Keeps track of which request node is next to be matched
    private int rIndex;

    //This is the final matching
    private ArrayList<MatchInfo> matching;
    //Keeps track of server nodes which have been matched already
    private ArrayList<Node> matchedSNodes;

    public PermutationMatch(ArrayList<Node> sNodes, ArrayList<Node> rNodes) {
	this.sNodes = sNodes;
	this.rNodes = rNodes;
	rIndex = 0;
	matching = new ArrayList<MatchInfo>();
	matchedSNodes = new ArrayList<Node>();
	g = new Graph();
    }

    /**
     * Runs the matching
     */
    public ArrayList<MatchInfo> runAlgorithm() {
	initializeGraph();
	MaxFlowBP mf = new MaxFlowBP(g);
	ArrayList<Graph.Edge> mfMatching;
	while(rIndex < rNodes.size()) {
	    System.out.println("Perm iteration >> " + (rIndex+1));
	    //Reset the MaxFlowBP object to allow for a fresh matching 
	    mf.unmatchRequestNodes();
	    //Add the new request node to be matched
	    mf.addRequestNode(rNodes.get(rIndex), rIndex+1);
	    mfMatching = mf.runIncompleteSet();
	    //System.out.println("mfMatching:\n" + mfMatching);
	    Node sNode = findNewSNode(mfMatching);
	    matchedSNodes.add(sNode);
	    MatchInfo match = new MatchInfo(sNode, rNodes.get(rIndex));
	    //System.out.println("matched > " + match);
	    matching.add(match);
	    rIndex++;
	}
	//System.out.println(matching);
	return matching;
    }

    /**
     * Returns the server node not yet in matchedSNodes
     */
    private Node findNewSNode(ArrayList<Graph.Edge> mfMatching) {
	for(Graph.Edge e: mfMatching) {
	    if(!e.isSourceEdge() && !e.isSinkEdge()) {
		//Edge is INVERTED - so getEnd() returns the server node
		Node s = e.getEnd();
		if(!matchedSNodes.contains(s)) {
		    return s;
		}
	    }
	}
	return null;
    }

    /**
     * Sets up the initial graph w/ all server Nodes and the first request Node
     */
    private void initializeGraph() {
	int index = 1;
	for(Node s: sNodes) {
	    g.xNodes.put(index, s);
	    g.addEdgeFromSource(index);
	    index++;
	}
    }
}
