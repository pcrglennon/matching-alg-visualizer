import java.util.ArrayList;
import java.util.HashMap;

public class Dijkstra {

    private Graph g;
    
    private ArrayList<Node> unvisitedNodes;
    private ArrayList<Node> visitedNodes;

    /**
     * Stores distances from initial node to all other nodes.
     *
     * Key - Node
     * Value - distance from initial node to key Node
     */
    private HashMap<Node, PathInfo> pathsFromSource;

    public Dijkstra(Graph g) {
	this.g = g;
	unvisitedNodes = new ArrayList<Node>(g.xNodes.values());
	unvisitedNodes.addAll(g.yNodes.values());
	visitedNodes = new ArrayList<Node>();
	pathsFromSource = new HashMap<Node, PathInfo>();
    }

    /**
     * Initializes and runs the algorithm
     *
     * Returns pathsFromSource
     */
    public HashMap<Node, PathInfo> runAlgorithm() {
	initializeDistances();
	while(unvisitedNodes.size() > 0) {
	    Node curNode = getMinimumDistUnvisitedNode();
	    //System.out.println("\nVisiting " + curNode);
	    updateDistances(curNode);
	    unvisitedNodes.remove(curNode);
	    visitedNodes.add(curNode);
	}
	return pathsFromSource;
    }

    /**
     * Return the unvisited node w/ the minimum distance from source
     */
    private Node getMinimumDistUnvisitedNode() {
	Node toReturn = unvisitedNodes.get(0);
	int minDist = pathsFromSource.get(toReturn).distance;
	for(int i = 1; i < unvisitedNodes.size(); i++) {
	    Node n = unvisitedNodes.get(i);
	    int nDist = pathsFromSource.get(n).distance;
	    if(nDist < minDist) {
		toReturn = n;
		minDist = nDist;
	    }
	}
	return toReturn;
    }

    /**
     * Return ArrayList of all unvisited nodes adjacent to Node n
     */
    private ArrayList<Node> findNeighbors(Node n) {
	ArrayList<Node> neighbors = new ArrayList<Node>();
	for(Graph.Edge e: g.edges) {
	    if(e.getSource().equals(n) && !visitedNodes.contains(e.getEnd())) {
		neighbors.add(e.getEnd());
	    }
	}
	return neighbors;
    }

    /**
     * Update the distances from Node n to all neighbors
     */
    private void updateDistances(Node curNode) {
	ArrayList<Node> neighbors = findNeighbors(curNode);
	for(Node neighbor: neighbors) {
	    int distanceThroughNode = pathsFromSource.get(curNode).distance + getAdjustedDistance(curNode, neighbor);
	    PathInfo neighborPI = pathsFromSource.get(neighbor);
	    if(distanceThroughNode < neighborPI.distance) {
		//System.out.println("new Min Dist from Src found for " + neighbor + ".  Replacing " + neighborPI.distance + " w/ " + distanceThroughNode);
		neighborPI.distance = distanceThroughNode;
		if(pathsFromSource.get(curNode).path != null) {
		    neighborPI.path = new ArrayList<Graph.Edge>(pathsFromSource.get(curNode).path);
		} else {
		    neighborPI.path = new ArrayList<Graph.Edge>();
		}
		//Add self to path
		neighborPI.path.add(g.getEdge(curNode, neighbor));
		
		//System.out.println("new path to >> " + neighbor + ": \n" + neighborPI.path);
	    }
	}
    }

    /**
     * Returns the distance of the edge that joins two nodes
     */
    private int getDistance(Node source, Node end) {
	for(Graph.Edge e: g.edges) {
	    if(e.getSource().equals(source) && e.getEnd().equals(end)) {
		return e.getDistance();
	    }
	}
	//Should NEVER happen, as it is only called on a node and a known neighbor
	return -1;
    }

    /**
     * Returns the "price-adjusted" distance between two nodes, which is the sum
     * of the prices of each node and the distance of the edge joining them 
     */
    private int getAdjustedDistance(Node source, Node end) {
	for(Graph.Edge e: g.edges) {
	    if(e.getSource().equals(source) && e.getEnd().equals(end)) {
		return e.getAdjustedDistance();
	    }
	}
	//Should NEVER happen, as it is only called on a node and a known neighbor
	return -1;
    }
    
    /**
     * Initialize the distances from initial node to all other nodes.
     *
     * Distance for the source node = 0
     * Distance for all other nodes = Interger.MAX_VALUE
     */
    private void initializeDistances() {
	try{
	    //Put in the source node
	    pathsFromSource.put(g.xNodes.get(-1), new PathInfo(0, null));
	} catch (Exception e) {
	    System.out.println("\n\n------EMPTY LIST------");
	    System.out.println("------EXITING------\n\n");
	    System.exit(0);
	}
	for(Node n: unvisitedNodes) {
	    //Don't clobber the 0 value of the source node!
	    if(n.equals(g.source)) {
		continue;
	    }
	    pathsFromSource.put(n, new PathInfo(Integer.MAX_VALUE, null));
	}
    }

    /**
     * Simple graph for testing
     *
     *
     * N1---1---N2---5---N3
     *          |      / |
     *          |     /  | 
     *          1    1   2
     *          |  /     |
     *          | /      |
     *          N4---9---N5
     *          \        /
     *           2      2
     *            \    /
     *             \  /
     *              N6
     */
    
    /**
    public static Graph makeTestGraph() {
	Graph g = new Graph();
	Node n1 = new Node("1");
	Node n2 = new Node("2");
	Node n3 = new Node("3");
	Node n4 = new Node("4");
	Node n5 = new Node("5");
	Node n6 = new Node("6");
	g.addEdge(n1, n2, 1);
	g.addEdge(n2, n3, 5);
	g.addEdge(n2, n4, 1);
	g.addEdge(n4, n3, 1);
	g.addEdge(n4, n5, 9);
	g.addEdge(n3, n5, 2);
	g.addEdge(n4, n6, 2);
	g.addEdge(n6, n5, 2);
	return g;
    }
    

    public static Graph makeTestGraph() {
	Graph g = new Graph();
	g.xNodes.put(1, new Node("x1"));
	g.xNodes.put(2, new Node("x2"));
	g.xNodes.put(3, new Node("x3"));
	for(int i: g.xNodes.keySet()) {
	    if(i == -1)
		continue;
	    g.addEdgeFromSource(i);
	}
	g.yNodes.put(1, new Node("y1"));
	g.yNodes.put(2, new Node("y2"));
	g.yNodes.put(3, new Node("y3"));
	
	g.addEdge(1, 1, 3);
	g.addEdge(1, 2, 1);
	g.addEdge(1, 3, 4);
	
	g.addEdge(2, 1, 2);
	g.addEdge(2, 2, 5);
	g.addEdge(2, 3, 2);
	
	g.addEdge(3, 1, 4);
	g.addEdge(3, 2, 6);
	g.addEdge(3, 3, 2);
	for(int i: g.yNodes.keySet()) {
	    if(i == -1)
		continue;
	    g.addEdgeToSink(i);
	}
	return g;
    }

    public static void main(String[] args) {
	Graph g = makeTestGraph();
	Dijkstra d = new Dijkstra(g);
	d.runAlgorithm();
    }
    */

}