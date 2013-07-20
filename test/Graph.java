import java.util.ArrayList;
import java.util.HashMap;

/**
 * A representation of a Complete Bipartite Graph
 *
 * Nodes are split into two sets: xNodes and yNodes, and each xNode has an
 * edge into each yNode
 *
 * Two nodes are categorized as the source and sink nodes.  By construction,
 * source is in xNodes, and sink is in yNodes.  Source has an edge into each
 * node in xNodes, and sink has an edge from each node in yNodes
 */
public class Graph {

    /**
     * Used a hashmap with integer "Node ID" keys to avoid errors when other
     * classes want to alter the nodes.  This keeps the nodes in one place,
     * and other classes can access them by their ID
     */
    public HashMap<Integer, Node> xNodes;
    public HashMap<Integer, Node> yNodes;
    public ArrayList<Edge> edges;
n
    //Source and Sink nodes
    public Node source;
    public Node sink;

    /**
     * Initialization, add the source and sink nodes
     */
    public Graph() {
	xNodes = new HashMap<Integer, Node>();
	yNodes = new HashMap<Integer, Node>();
	edges = new ArrayList<Edge>();
	
	source = new Node("src", -1, -1);
	sink = new Node("sink", -1, -1);
	xNodes.put(-1, source);
	yNodes.put(-1, sink);
    }

    public void addEdge(int sourceID, int endID, double distance) {
	edges.add(new Edge(sourceID, endID, distance));
    }

    /**
     * Gets the edge (if any) which connects Nodes s and t, or null if 
     * none is found
     */
    public Edge getEdge(Node s, Node t) {
	for(Edge e: edges) {
	    if(e.getSource().equals(s) && e.getEnd().equals(t)) {
		return e;
	    }
	}
	return null;
    }
    
    /** 
     * Add an edge from source to a node in xNodes
     */
    public void addEdgeFromSource(int endID) {
	edges.add(new Edge(-1, endID, 0, true, false));
    }

    /**
     * Add an dege from a node in yNodes to sink
     */
    public void addEdgeToSink(int sourceID) {
	edges.add(new Edge(sourceID, -1, 0, false, true));
    }

    /**
     * Representation of a directional Edge between two Nodes
     */
    class Edge {

	/**
	 * Maintains the "ID" of the source node (in xNodes) and end node 
	 * (in yNodes).  Calling this sourceID is confusing, and I can't 
	 * remember why I did that.
	 */
	public int sourceID;
	public int endID;
	private double distance;

	//Is this a forwards edge or backwards edge? (Used in MaxFlow)
	private boolean forwardsEdge;

	//Is this an edge from the source node?
	private boolean sourceEdge;
	//Is this an edge to the sink node?
	private boolean sinkEdge;

	public Edge(int sourceID, int endID, double distance, boolean sourceEdge, boolean sinkEdge) {
	    this.sourceID = sourceID;
	    this.endID = endID;
	    this.distance = distance;
	    this.sourceEdge = sourceEdge;
	    this.sinkEdge = sinkEdge;
	    //Forwards by default
	    forwardsEdge = true;
	}

	/**
	 * Default constructor - neither a sourceEdge nor a sinkEdge
	 */
	public Edge(int sourceID, int endID, double distance) {
	    this(sourceID, endID, distance, false, false);
	}

	/**
	 * Get the "source" node of an edge, i.e. the node which the edge "begins"
	 *
	 * If its a forwards edge, its source will be in xNodes, as the initial graph
	 * has all edges from xNodes into yNodes
	 * If its a backwards/inverted edge, its "source" is actually in yNodes
	 */
	public Node getSource() {
	    if(sourceEdge) {
		return source;
	    } else if(sinkEdge || !forwardsEdge) {
		return(yNodes.get(sourceID));
	    } else {
 		return(xNodes.get(sourceID));
	    }
	}

	/**
	 * Similar to getSource()
	 */
	public Node getEnd() {
	    if(sinkEdge) {
		return sink;
	    } else if(sourceEdge || !forwardsEdge) {
		return(xNodes.get(endID));
	    } else {
		return(yNodes.get(endID));
	    }
	}

	/**
	 * Inverts the edge, i.e. makes a forwards edge backwards or vice versa
	 *
	 * Flips the sourceID and sinkID, old source is new sink, vice versa
	 */
	public void invert() {
	    int tmp = sourceID;
	    sourceID = endID;
	    endID = tmp;
	    if(forwardsEdge) {
		forwardsEdge = false;
	    } else {
		forwardsEdge = true;
	    }
	}

	public double getDistance() {
	    return distance;
	}

	/**
	 * Gets the "Adjusted Distance" of an edge, by using its price.  This is all
	 * for the MaxFlowBP algorithm
	 */
	public double getAdjustedDistance() {
	    if(sourceEdge || sinkEdge) {
		return distance;
	    }
	    if(forwardsEdge) {
		return (xNodes.get(sourceID).getPrice() + distance - yNodes.get(endID).getPrice());
	    } else {
		return (xNodes.get(endID).getPrice() + distance - yNodes.get(sourceID).getPrice());
	    }
	}
						
	public boolean isForwardsEdge() {
	    return forwardsEdge;
	}

	public boolean isSourceEdge() {
	    return sourceEdge;
	}

	public boolean isSinkEdge() {
	    return sinkEdge;
	}

	/**
	 * Outputs the Edge information in a psuedo-useful manner
	 */
	public String toString() {
	    if(sourceEdge) {
		if(forwardsEdge) {
		    return "E src<source> end<" + xNodes.get(endID) + "> Dist <0>";
		} else {
		    return "E src<" + xNodes.get(sourceID) + "> end<src> Dist <0> BACKWARDS EDGE";
		}
	    } else if (sinkEdge) {
		if(forwardsEdge) {
		    return "E src<" + yNodes.get(sourceID)  + "> end <sink> Dist <0>";
		} else {
		    return "E src<sink> end<" + yNodes.get(endID) + "> Dist <0> BACKWARDS EDGE";
		}
	    } else if(forwardsEdge) {
		return "E src <" + xNodes.get(sourceID) + "> end <" + yNodes.get(endID) + "> Dist <" + distance + ">";
	    } else {
		return "E src <" + yNodes.get(sourceID) + "> end <" + xNodes.get(endID) + "> Dist <" + distance + ">" + " BACKWARDS EDGE";
	    }
	}
    }
} 