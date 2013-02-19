import java.util.ArrayList;
import java.util.HashMap;

public class Graph {

    //Maps ID to Node
    public HashMap<Integer, Node> xNodes;
    public HashMap<Integer, Node> yNodes;
    public ArrayList<Edge> edges;

    //Source and Sink nodes
    public Node source;
    public Node sink;

    public Graph() {
	xNodes = new HashMap<Integer, Node>();
	yNodes = new HashMap<Integer, Node>();
	edges = new ArrayList<Edge>();
	
	source = new Node("src", -1, -1);
	sink = new Node("sink", -1, -1);
	xNodes.put(-1, source);
	yNodes.put(-1, sink);
    }

    public void addEdge(int sourceID, int endID, int distance) {
	edges.add(new Edge(sourceID, endID, distance));
    }

    public Edge getEdge(Node s, Node t) {
	for(Edge e: edges) {
	    if(e.getSource().equals(s) && e.getEnd().equals(t)) {
		return e;
	    }
	}
	return null;
    }

    public void addEdgeFromSource(int endID) {
	edges.add(new Edge(-1, endID, 0, true, false));
    }

    public void addEdgeToSink(int sourceID) {
	edges.add(new Edge(sourceID, -1, 0, false, true));
    }

    class Edge {

	public int sourceID;
	public int endID;
	private int distance;

	private boolean forwardsEdge;

	private boolean sourceEdge;
	private boolean sinkEdge;

	public Edge(int sourceID, int endID, int distance, boolean sourceEdge, boolean sinkEdge) {
	    this.sourceID = sourceID;
	    this.endID = endID;
	    this.distance = distance;
	    this.sourceEdge = sourceEdge;
	    this.sinkEdge = sinkEdge;
	    //Forwards by default
	    forwardsEdge = true;
	}

	public Edge(int sourceID, int endID, int distance) {
	    this(sourceID, endID, distance, false, false);
	}

	public Node getSource() {
	    if(sourceEdge) {
		return source;
	    } else if(sinkEdge || !forwardsEdge) {
		return(yNodes.get(sourceID));
	    } else {
 		return(xNodes.get(sourceID));
	    }
	}

	public Node getEnd() {
	    if(sinkEdge) {
		return sink;
	    } else if(sourceEdge || !forwardsEdge) {
		return(xNodes.get(endID));
	    } else {
		return(yNodes.get(endID));
	    }
	}

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

	public int getDistance() {
	    return distance;
	}

	public int getAdjustedDistance() {
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

	public String toString() {
	    if(sourceEdge) {
		return "E src<source> end<" + xNodes.get(endID) + "> distance <0>";
	    } else if (sinkEdge) {
		return "E src<" + yNodes.get(sourceID)  + "> end <sink> distance <0>";
	    } else if(forwardsEdge) {
		return "E src <" + xNodes.get(sourceID) + "> end <" + yNodes.get(endID) + "> Dist <" + distance + ">";
	    } else {
		return "E src <" + yNodes.get(sourceID) + "> end <" + xNodes.get(endID) + "> Dist <" + distance + ">" + " BACKWARDS EDGE";
	    }
	}
    }
} 