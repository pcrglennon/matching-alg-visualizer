import java.util.ArrayList;

public class PermutationMatch {

    private Graph g;
    private ArrayList<Node> sNodes;
    private ArrayList<Node> rNodes;
    //Keeps track of which request node is next to be matched
    private int rIndex;

    private ArrayList<Graph.Edge> matching;

    public PermutationMatch(ArrayList<Node> sNodes, ArrayList<Node> rNodes) {
	this.sNodes = sNodes;
	this.rNodes = rNodes;
	matching = new ArrayList<Graph.Edge>();
	g = new Graph();
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
	//Get the first request Node
	rIndex = 0;
	try {
	    Node r = rNodes.get(0);
	    g.yNodes.put(index, r);
	    g.addEdgeToSink(index);
	    //Add edge from each server Node to the first request Node
	    for(Node s: sNodes) {
		g.addEdge(index, 1, xyDistance(s.xPos, r.xPos, s.yPos, r.yPos));
		index++;
	    }
	    rIndex++;
	} catch (NullPointerException e) {
	    System.exit(0);
	}
    }

    public ArrayList<Graph.Edge> runAlgorithm() {
	initializeGraph();
	MaxFlowBP mf = new MaxFlowBP(g);
	ArrayList<Graph.Edge> mfMatching = mf.runIncompleteSet();
	mf.addRequestNode(rNodes.get(rIndex), rIndex);
	rIndex++;
	mfMatching = mf.runIncompleteSet();
	System.out.println(mfMatching);
	return matching;
    }

    /**
     * Returns the distance between two points w/ (x,y) coordinates
     */
    private int xyDistance(int x1, int x2, int y1, int y2) {
	return (int)Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

}