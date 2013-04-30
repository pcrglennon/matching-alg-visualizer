import java.util.ArrayList;

public class PathInfo {

    public double distance;
    public ArrayList<Graph.Edge> path;

    public PathInfo(double distance, ArrayList<Graph.Edge> path) {
	this.distance = distance;
	this.path = path;
    }

}