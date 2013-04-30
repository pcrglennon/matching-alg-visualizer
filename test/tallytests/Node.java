public class Node {

    public int xPos;
    public int yPos;

    public String id;

    private double price;
    private boolean matched;

    public Node(String id, int xPos, int yPos, double price) {
	this.xPos = xPos;
	this.yPos = yPos;
	this.id = id;
	this.price = price;
	//Unmatched by default
	matched = false;
    }

    public Node(String id, int xPos, int yPos) {
	this(id, xPos, yPos, 0);
    }

    public double getPrice() {
	return price;
    }

    public void setPrice(double price) {
	this.price = price;
    }

    public boolean isMatched() {
	return matched;
    }

    public void setMatched(boolean isMatched) {
	matched = isMatched;
    }

    @Override
    public String toString() {
	return "N id(" + id + ") at (" + xPos + "," + yPos + ")";
    }
}