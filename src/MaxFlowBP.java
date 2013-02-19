import java.util.ArrayList;
import java.util.HashMap;

public class MaxFlowBP {

    private Graph g;

    private ArrayList<Graph.Edge> matching;
    
    public MaxFlowBP(Graph g) {
	this.g = g;
	matching = new ArrayList<Graph.Edge>();
    }

    public ArrayList<Graph.Edge> runAlgorithm() {
	setInitialNodePrices();
	int counter = 1;
	while(!isPerfectMatching()) {
	    System.out.println("\nITERATION >> " + counter);
	    HashMap<Node, PathInfo> pathsFromSource = new Dijkstra(g).runAlgorithm();
	    PathInfo minPathToSink = getMinPathToSink(pathsFromSource);
	    //System.out.println("\nPATH\n" + minPathToSink.path);
	    augmentPath(minPathToSink);
	    //change prices
	    for(Node y: g.yNodes.values()){
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

    public ArrayList<Graph.Edge> runIncompleteSet() {
	setInitialNodePrices();
	int counter = 1;
	while(!allRequestNodesMatched()) {
	    System.out.println("\nITERATION >> " + counter);
	    HashMap<Node, PathInfo> pathsFromSource = new Dijkstra(g).runAlgorithm();
	    PathInfo minPathToSink = getMinPathToSink(pathsFromSource);
	    System.out.println("\nPATH\n" + minPathToSink.path);
	    augmentPath(minPathToSink);
	    //Update Prices
	    for(Node y: g.yNodes.values()) {
		if(!y.id.equals("sink")) {
		    y.setPrice(y.getPrice() + pathsFromSource.get(y).distance);
		    //System.out.println("new Y price >> " + y.getPrice());
		}
	    }
	    for(Node x: g.xNodes.values()) {
		if(!x.id.equals("src")) {
		    x.setPrice(x.getPrice() + pathsFromSource.get(x).distance);
		    //System.out.println("new x Price >> " + x.getPrice());
		}
	    }
	    counter++;
	    //for(Graph.Edge e: matching)
		//System.out.println(e + " A. Dist > " + e.getAdjustedDistance());
	}
	return matching;
    }

    /**
     * Add a new request node to the graph - used by Permutation Match
     */
    public void addRequestNode(Node n, int index) {
	g.yNodes.put(index, n);
	for(Node s: g.xNodes.values()) {
	    
	}
    }


    private PathInfo getMinPathToSink(HashMap<Node, PathInfo> pathsFromSource) {
	Node minDistYNode = null;
	int minDistToSink = Integer.MAX_VALUE;
	for(Node n: g.yNodes.values()) {
	    if(!n.isMatched() && !n.id.equals("sink") && pathsFromSource.get(n).distance + n.getPrice() < minDistToSink) {
		minDistYNode = n;
		minDistToSink = pathsFromSource.get(n).distance + n.getPrice();
	    }
	}
	PathInfo minPathToSink = pathsFromSource.get(minDistYNode);
	return minPathToSink;
    }
    
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
     */
    private boolean allRequestNodesMatched() {
	for(Node n: g.yNodes.values()) {
	    if(!n.id.equals("sink") && !n.isMatched()) {
		return false;
	    }
	}
	return true;
    }

    private void setInitialNodePrices() {
	//All Nodes created w/ default price of 0, so xNodes is already set
	for(Node n: g.yNodes.values()) {
	    int minDistIntoN = getMinimumDistanceIntoNode(n);
	    n.setPrice(minDistIntoN);
	}
    }
    
    private int getMinimumDistanceIntoNode(Node n) {
	int minDist = Integer.MAX_VALUE;
	for(Graph.Edge e: g.edges) {
	    if(e.getEnd().equals(n)) {
		if(e.getDistance() < minDist) {
		    minDist = e.getDistance();
		}
	    }
	}
	return minDist;
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