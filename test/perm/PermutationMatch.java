import java.util.ArrayList;

public class PermutationMatch {

    private Graph g;
    private ArrayList<Node> sNodes;
    private ArrayList<Node> rNodes;
    //Keeps track of which request node is next to be matched
    private int rIndex;

    private ArrayList<MatchInfo> matching;
    private ArrayList<Node> matchedSNodes;

    public PermutationMatch(ArrayList<Node> sNodes, ArrayList<Node> rNodes) {
	this.sNodes = sNodes;
	this.rNodes = rNodes;
	rIndex = 0;
	matching = new ArrayList<MatchInfo>();
	matchedSNodes = new ArrayList<Node>();
	g = new Graph();
    }

    public ArrayList<MatchInfo> runAlgorithm() {
	initializeGraph();
	MaxFlowBP mf = new MaxFlowBP(g);
	ArrayList<Graph.Edge> mfMatching;
	while(rIndex < rNodes.size()) {
	    System.out.println("Perm iteration >> " + (rIndex+1));
	    mf.unmatchRequestNodes();
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
