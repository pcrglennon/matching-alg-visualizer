public class Node {

    public int xPos;
    public int yPos;

    public String id;

    private int price;
    private boolean matched;

    public Node(String id, int xPos, int yPos, int price) {
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

    public int getPrice() {
	return price;
    }

    public void setPrice(int price) {
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