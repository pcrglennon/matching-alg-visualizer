import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Optimal Offline Matching Algorithm
 *
 * High-Level description: add a source and sink node to a Bipartite set in
 * order to form a Directed Graph, then repeatedly find "augmenting paths" through
 * graph, and match nodes along that path.  For a more detailed explanation, see
 * textbook "Algorithm Design" by Jon Kleinberg and Eva Tardos, chapter 7, or 
 * search for Max Flow Algorithm adapted to Weighted Bipartite Matching
 *
 * The implementation of this algorithm is based on Kleinberg and Tardos' 
 * description in their textbook
 *
 * Commented-out println() statements were used in debugging
 */
public class MaxFlowBP {

    private Graph g;
    
    // The set of edges which represent the matching
    private ArrayList<Graph.Edge> matching;
    
    public MaxFlowBP(Graph g) {
	this.g = g;
	matching = new ArrayList<Graph.Edge>();
    }

    /**
     * Run the Algorithm:
     *
     * Setup the intial node prices, then iteratively add augmenting paths
     * until no more can be added
     *
     * Returns an ArrayList of Edges, not MatchInfos.  This is another result
     * of poor planning!
     */
    public ArrayList<Graph.Edge> runAlgorithm() {
	setInitialNodePrices();
	int counter = 1;
	while(!isPerfectMatching()) {
	    System.out.println("\nMF ITERATION >> " + counter);
	    //Run Dijkstra to get the cost from source node to all other nodes
	    HashMap<Node, PathInfo> pathsFromSource = new Dijkstra(g).runAlgorithm();
	    PathInfo minPathToSink = getMinPathToSink(pathsFromSource);
	    //System.out.println("\nPATH\n" + minPathToSink.path);
	    augmentPath(minPathToSink);
	    //change prices
	    for(Node y: g.yNodes.values()){
		//Don't modify the price of the sink node!
		if(!y.id.equals("sink")) {
		    y.setPrice(y.getPrice() + pathsFromSource.get(y).distance);
		}
	    }
	    for(Node x: g.xNodes.values()){
		if(!x.id.equals("src")) {
		    x.setPrice(x.getPrice() + pathsFromSource.get(x).distance);
		}
	    }
	    counter++;
	    //System.out.println("\nMATCHING\n");
	    //for(Graph.Edge e: matching)
		//System.out.println(e + " A. Dist > " + e.getAdjustedDistance());
	}
	return matching;
    }

    /**
     * Similar to runAlgorithm(), but terminates when all REQUEST nodes are matched
     */
    public ArrayList<Graph.Edge> runIncompleteSet() {
	// As this is called repeatedly by PermutationMatch, graph must be reset
	matching.clear();
	resetGraph();
	setInitialNodePrices();
	int counter = 1;
	while(!allRequestNodesMatched()) {
	    System.out.println("\nMF (Inc. Set) ITERATION >> " + counter);
	    HashMap<Node, PathInfo> pathsFromSource = new Dijkstra(g).runAlgorithm();
	    PathInfo minPathToSink = getMinPathToSink(pathsFromSource);
	    //System.out.println("PATH\n" + minPathToSink.path);
	    augmentPath(minPathToSink);
	    //Update Prices
	    for(Node y: g.yNodes.values()) {
		if(!y.id.equals("sink")) {
		    y.setPrice(y.getPrice() + pathsFromSource.get(y).distance);
		}
	    }
	    for(Node x: g.xNodes.values()) {
		if(!x.id.equals("src")) {
		    x.setPrice(x.getPrice() + pathsFromSource.get(x).distance);
		}
	    }
	    counter++;
	}
	return matching;
    }

    /**
     * Finds the minimum-cost path to the sink node
     *
     * Essentially, finds the unmatched node in yNodes set with minimum distance
     * from source
     *
     * As all sourcenode-to-xNode and yNode-to-sinknode edges are cost-0, this
     * basically returns the minimum-cost path from an unmatched xNode to
     * an unmatched yNode
     */ 
    private PathInfo getMinPathToSink(HashMap<Node, PathInfo> pathsFromSource) {
	Node minDistYNode = null;
	double minDistToSink = Double.MAX_VALUE;
	for(Node n: g.yNodes.values()) {
	    if(!n.isMatched() && !n.id.equals("sink") && pathsFromSource.get(n).distance + n.getPrice() < minDistToSink) {
		minDistYNode = n;
		minDistToSink = pathsFromSource.get(n).distance + n.getPrice();
	    }
	}
	PathInfo minPathToSink = pathsFromSource.get(minDistYNode);
	minPathToSink.path.add(g.getEdge(minDistYNode, g.sink));
	return minPathToSink;
    }
    
    /**
     * Matches unmatched nodes along path, and unmatches matched nodes
     * along path
     *
     * Matched nodes may be along path in case of a backwards edge being
     * included on the path (again, see the textbook or another description of
     * the algorithm for a more in-depth explanation)
     */
    private void augmentPath(PathInfo minPathToSink) {
	for(Graph.Edge e: minPathToSink.path) {
	    if(e.isSourceEdge() || e.isSinkEdge()) {
		    e.invert();
	    } else {
		if(matching.contains(e)) {
		    //Make the edge forward, w/ positive distance
		    matching.remove(e);
		    e.invert();
		} else {
		    //Add the "backwards edge" to the matching
		    e.getSource().setMatched(true);
		    e.getEnd().setMatched(true);
		    e.invert();
		    matching.add(e);
		}
	    }
	}
    }

    /**
     * Reset any inverted edges
     */
    private void resetGraph() {
	//System.out.println("\nRESET\n");
	for(Graph.Edge e: g.edges) {
	    if(!e.isForwardsEdge()) {
		//System.out.println(e);
		e.invert();
	    }
	}
    }

    /**
     * Add a new request node to the graph - used by Permutation Match
     *
     * r - Request Node to add
     * index - Index of new Request Node
     */
    public void addRequestNode(Node r, int index) {
	//Unmatched by default
	r.setMatched(false);
	g.yNodes.put(index, r);
	g.addEdgeToSink(index);
	int sIndex = 1;
	for(Node s: g.xNodes.values()) {
	    if(!s.id.equals("src")) {
		g.addEdge(sIndex, index, xyDistance(s.xPos, r.xPos, s.yPos, r.yPos));
		sIndex++;
	    }
	}
    }

    /**
     * Returns true if all nodes (save sink and source nodes) are matched
     *
     * By construction, sink and source will never be matched
     */
    private boolean isPerfectMatching() {
	for(Node n: g.xNodes.values()) {
	    if(!n.id.equals("src") && !n.isMatched()) {
		return false;
	    }
	}
	for(Node n: g.yNodes.values()) {
	    if(!n.id.equals("sink") && !n.isMatched()) {
		return false;
	    }
	}
	return true;
    }

    /**
     * Only check if all request nodes are matched
     *
     * Used when MaxFlow is being run on an incomplete set of request nodes.
     * See runIncompleteSet()
     */
    private boolean allRequestNodesMatched() {
	for(Node n: g.yNodes.values()) {
	    if(!n.id.equals("sink") && !n.isMatched()) {
		return false;
	    }
	}
	return true;
    }

    /**
     * Unmatch all request nodes for a fresh run
     */
    public void unmatchRequestNodes() {
	for(Node n: g.yNodes.values()) {
	    n.setMatched(false);
	}
    }
    
    /**
     * See textbook/other algorithm description to understand why prices
     * are set as such.  I don't fully understand it myself
     */
    private void setInitialNodePrices() {
	//All Nodes created w/ default price of 0, so xNodes is already set
	for(Node n: g.yNodes.values()) {
	    double minDistIntoN = getMinimumDistanceIntoNode(n);
	    n.setPrice(minDistIntoN);
	}
    }

    /**
     * Returns the cost of minimum-cost edge into Node n (a yNode)
     */
    private double getMinimumDistanceIntoNode(Node n) {
	double minDist = Double.MAX_VALUE;
	for(Graph.Edge e: g.edges) {
	    if(e.getEnd().equals(n)) {
		if(e.getDistance() < minDist) {
		    minDist = e.getDistance();
		}
	    }n
	}
	return minDist;
    }

    /**
     * Returns the distance between two points w/ (x,y) coordinates
     */
    private double xyDistance(int x1, int x2, int y1, int y2) {
	return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }
    
    /**
    public Graph makeTestGraph() {
	Graph g = new Graph();
	g.xNodes.put(1, new Node("x1", 0, 0));
	g.xNodes.put(2, new Node("x2", 4, 7));
	g.xNodes.put(3, new Node("x3", 9, 2));
	for(int i: g.xNodes.keySet()) {
	    //-1 is the id of the source node
	    if(i == -1) {
		continue;
	    }
	    g.addEdgeFromSource(i);
	}
	g.yNodes.put(1, new Node("y1", 9, 3));
	g.yNodes.put(2, new Node("y2", 7, 3));
	g.yNodes.put(3, new Node("y3", 5, 5));
	
	int index = 1;
	int yIndex = 1;
	for(index; index < g.xNodes.size(); index++) {
	    yIndex = 1;
	    for(yIndex; yIndex < g.yNodes.size(); yIndex++) {
		g.addEdge(index, yIndex, x.xPos, y.xPos, x.yPos, y.yPos);
	    }
	}

	for(int i: g.yNodes.keySet()) {
	    if(i == -1) {
		continue;
	    }
	    g.addEdgeToSink(i);
	}

	return g;
    }

    public static void main(String[] args) {
	MaxFlowBP mf = new MaxFlowBP();
	mf.runAlgorithm();
    }
    */
}