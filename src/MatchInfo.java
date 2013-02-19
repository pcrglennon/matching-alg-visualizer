/**
 * Container class which stores information about a matching of two nodes
 *
 * rNode - the (x,y) coords of the request Node
 * sNode - the (x,y) coords of the server Node
 * distance - the distance between rNode and sNode
 */

public class MatchInfo {
	
    public Node sNode;
    public Node rNode;
    public double distance;

    public MatchInfo(Node sNode, Node rNode, double distance) {
	this.sNode = sNode;
	this.rNode = rNode;
	this.distance = distance;
    }

    public String toString() {
	return("Match: Server " + sNode + " and Request " + rNode + ".  Distance: " + distance);
    }
}