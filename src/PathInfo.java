import java.util.ArrayList;

public class PathInfo {

    public int distance;
    public ArrayList<Graph.Edge> path;

    public PathInfo(int distance, ArrayList<Graph.Edge> path) {
	this.distance = distance;
	this.path = path;
    }

}