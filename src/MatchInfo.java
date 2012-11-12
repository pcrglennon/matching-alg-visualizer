/**
 * Container class which stores information about a matching of two nodes
 *
 * rNode - the (x,y) coords of the request Node
 * sNode - the (x,y) coords of the server Node
 * distance - the distance between rNode and sNode
 */

public class MatchInfo {
	
    public int[] rNode;
    public int[] sNode;
    public double distance;

    public MatchInfo(int[] rNode, int[] sNode, double distance) {
	this.rNode = rNode;
	this.sNode = sNode;
	this.distance = distance;
    }

    public String toString() {
	return("Match: Request Node " + rNode[0] + "," + rNode[1] + " and Server Node " + sNode[0] + "," + sNode[1] + ".  Distance: " + distance);
    }
}