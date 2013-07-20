import java.util.ArrayList;

/**
 * Simple container class for the information of a path between nodes:
 *
 * distance - the total distance/cost between the two nodes
 * path - an array of edges which describes the actual path
 *
 * Used by Dijkstra and MaxFlowBP
 */
public class PathInfo {

    public double distance;
    public ArrayList<Graph.Edge> path;

    public PathInfo(double distance, ArrayList<Graph.Edge> path) {
	this.distance = distance;
	this.path = path;
    }

}