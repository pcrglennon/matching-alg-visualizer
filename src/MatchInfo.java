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

    public MatchInfo(Node sNode, Node rNode) {
	this.sNode = sNode;
	this.rNode = rNode;
	distance = xyDistance(sNode.xPos, rNode.xPos, sNode.yPos, rNode.yPos);
    }
    
    /**
     * Returns the distance between two points w/ (x,y) coordinates
     */
    private int xyDistance(int x1, int x2, int y1, int y2) {
	return (int)Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    public String toString() {
	return("Match: Server " + sNode + " and Request " + rNode + ".  Distance: " + distance);
    }
}