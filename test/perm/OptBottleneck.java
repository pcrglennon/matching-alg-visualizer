import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import java.text.DecimalFormat;

/**
 * An implementation of Gross' Optimal Bottleneck Assignment Algorithm (1959)
 */
public class OptBottleneck {

    private ArrayList<Node> sNodes;
    private ArrayList<Node> rNodes;

    private int nNodes = 6;
    //Structures used by the Algorithm
    
    /** Cost Matrix: 
     *     y1 y2 y3
     * x1  1  3  2
     * x2  7  2  4
     * x3  8  1  5
     *
     * cost[i][j] is cost from x(i+1) to y(j+1)
     * (i.e.) cost[2][0] = cost from x3 to y1 = 8
     */
    private Cost[][] costMatrix;
    //Used to check if a row has a checked value
    private HashMap<Integer, Boolean> rowChecked;
    //Contains checked costs
    private ArrayList<Cost> checkedCosts;
    //Used to look for "unchecked zeros in a column", from Gross Alg.
    private ArrayList<Integer> uncheckedZeroCols;
    //Holds any "checked" zero columns, which are NOT the same as a column w/o a zero
    private ArrayList<Integer> checkedZeroCols;

    private ArrayList<Cost> matches;

    public OptBottleneck() {
	sNodes = new ArrayList<Node>(nNodes);
	rNodes = new ArrayList<Node>(nNodes);
    }

    public OptBottleneck(ArrayList<Node> sNodes, ArrayList<Node> rNodes) {
	this.sNodes = sNodes;
	this.rNodes = rNodes;
    }
    
    public ArrayList<MatchInfo> run() {
	costMatrix = setupCostMatrix();
	//These structures are used by the algorithm
	rowChecked = new HashMap<Integer, Boolean>();
	checkedCosts = new ArrayList<Cost>();
	uncheckedZeroCols = new ArrayList<Integer>();
	checkedZeroCols = new ArrayList<Integer>();
	matches = setupInitialCosts();
	boolean optimalMatch = false;
	while(!optimalMatch) {
	    Cost bNeckCost = getBottleneckCost(); //Rule 1
	    //System.out.println("bNC >> " + bNeckCost);
	    double bNeckVal = bNeckCost.val;
	    uncheckedZeroCols.add(bNeckCost.j);
	    Integer curCol = findUncheckedZeroCol(); //Rule 2
	    if(curCol == -1) {
		optimalMatch = true;
		break;
	    }
	    Cost curCost;
	    while(curCol > - 1) {
		curCost = goToUnmatchedCostInCol(curCol, bNeckVal); //Rule 3
		//System.out.println("Going to UM C >> " + curCost);
		if(curCost == null) {
		    uncheckedZeroCols.remove(curCol);
		    checkedZeroCols.add(curCol);
		    curCol = findUncheckedZeroCol(); //Return to Rule 2
		    if(curCol == -1) {
			optimalMatch = true;
			break;
		    }
		} else {
		    checkedCosts.add(curCost);
		    rowChecked.put(curCost.i, true); //Rule 4
		    //System.out.println("CHECKED " + curCost);
		    curCost = goToMatchedCostInRow(curCost.i);
		    curCol = curCost.j;
		    //System.out.println("Going to M C >> " + curCost);
		    if(uncheckedZeroCols.contains(curCost.j) || checkedZeroCols.contains(curCost.j)) {
			//System.out.println("REMOVING >> " + curCost);
			matches.remove(curCost);
			//System.out.println(matches);
			//System.out.println("SWITCHING MATCHES");
			switchMatches(curCost); //Rule 6
			uncheckedZeroCols.clear(); //Rule 7
			checkedZeroCols.clear();
			checkedCosts.clear();
			
			for(Integer i: rowChecked.keySet()) {
			    rowChecked.put(i, false);
			}
			//Reset, break out of loop
			curCol = -1;
		    } else {
			uncheckedZeroCols.add(curCost.j); //Return to Rule 3
		    }
		}
	    }
	}
	//System.out.println(matches);
	ArrayList<MatchInfo> finalMatches = getFinalMatches(matches);
	return finalMatches;
    }

    /**
     * Switches checked and matched costs, according to Rule 6 of the algorithm
     */
    private void switchMatches(Cost curCost) {
	curCost = goToCheckedCostInRow(curCost.i);
	//System.out.println("Matching >> " + curCost);
	matches.add(curCost);
	//System.out.println(matches);
	curCost = goToNextMatchedCostInCol(curCost);
	//System.out.println("Going to M C >> " + curCost);
	if(curCost == null)
	    //No other matched cost in Col, done
	    return;
	else {
	    matches.remove(curCost);
	    //System.out.println("REMOVED COST " + curCost.val + " from matches");
	    //System.out.println(matches);
	    switchMatches(curCost);
	}
    }

    private Integer findUncheckedZeroCol() {
	try {
	    return uncheckedZeroCols.get(0);
	} catch (Exception e) {
	    return -1;
	}
    }

    private Cost goToCheckedCostInRow(int curRow) {
	Cost end = null;
	for(int i = 0; i < costMatrix.length; i++) {
	    Cost c = costMatrix[curRow][i];
	    if(checkedCosts.contains(c)) {
		end = c;
		break;
	    }
	}
	//Algorithm guarantees it will find a checked value in current row, end should never be null
	return end;
    }

    /**
     * Go to the matched cost c in current row, and return c
     */
    private Cost goToMatchedCostInRow(int curRow) {
	Cost end = null;
	for(int i = 0; i < costMatrix.length; i++) {
	    Cost c = costMatrix[curRow][i];
	    if(matches.contains(c)) {
		end = c;
		break;
	    }
	}
	//Algorithm guarantees it will find a matched value in current row, end should never be null
	return end;
    }

    /**
     * Attempts to find matched cost c in current row, which is NOT curCost, and return c
     * If a different matched cost is not found in curCol, then return null
     */
    private Cost goToNextMatchedCostInCol(Cost curCost) {
	Cost end = null;
	for(int i = 0; i < costMatrix.length; i++) {
	    Cost c = costMatrix[i][curCost.j];
	    if(matches.contains(c) && c != curCost) {
		end = c;
		break;
	    }
	}
	return end;
    }

    /**
     * Attempt to go to next unmatched cost c in current column which meets these rules:
     * 1) cost of c < bNeckVal
     * 2) c is in a row with no checked values
     * and return c
     */
    private Cost goToUnmatchedCostInCol(int curCol, double bNeckVal) {
	Cost end = null;
	for(int i = 0; i < costMatrix.length; i++) {
	    Cost c = costMatrix[i][curCol];
	    if(c.val < bNeckVal && !rowChecked.get(i)) {
		end = c;
		break;
	    }
	}
	return end;
    }

    //Set up an initial matching with costMatrix[i][i] being matched from i = 0 to size of matrix
    private ArrayList<Cost> setupInitialCosts() {
	ArrayList<Cost> matches = new ArrayList<Cost>(costMatrix.length);
	for(int i = 0; i < costMatrix.length; i++) {
	    matches.add(costMatrix[i][i]);
	    rowChecked.put(i, false);
	}
	return matches;
    }

    private Cost getBottleneckCost() {
	double b = Double.MIN_VALUE;
	Cost bNeckCost = null;
	for(Cost c: matches) {
	    if(c.val > b) {
		bNeckCost = c;
		b = c.val;
	    }
	}
	return bNeckCost;
    }

    /**
     * Setup a cost matrix:
     
     */
    private Cost[][] setupCostMatrix() {
	DecimalFormat twoD = new DecimalFormat("#.##");
	Cost[][] costMatrix = new Cost[rNodes.size()][sNodes.size()];
	for (int i = 0; i < sNodes.size(); i++) {
	    Node sNode = sNodes.get(i);
	    for(int j = 0; j < rNodes.size(); j++) {
		Node rNode = rNodes.get(j);
		costMatrix[i][j] = new Cost(xyDistance(sNode.xPos, rNode.xPos, sNode.yPos, rNode.yPos), i, j);
	    }
	}
	/**   -----TEST CODE-----
	Cost[][] costMatrix = new Cost[6][6];
	double[][] vals = new double[6][6];
	double[] row1 = {8.6, 4.47, 8.06, 8.25, 8.25, 5};
	double[] row2 = {4.12, 5, 3.16, 3.61, 2.24, 1.41};
	double[] row3 = {10.2, 5.1, 10.05, 12.08, 11.05, 8.54};
	double[] row4 = {12.81, 8.6, 12.21, 11.05, 12.08, 8.54};
	double[] row5 = {11.18, 6.4, 10.77, 11.18, 11.18, 8};
	double[] row6 = {6.71, 2.24, 6.32, 8.06, 7, 4.47};
	vals[0] = row1;
	vals[1] = row2;
	vals[2] = row3;
	vals[3] = row4;
	vals[4] = row5;
	vals[5] = row6;
	for(int i = 0; i < costMatrix.length; i++) {
	    System.out.println("");
	    for(int j = 0; j < costMatrix[i].length; j++) {
		Cost c = new Cost(vals[i][j], i, j);
		costMatrix[i][j] = c;
		System.out.print(c.val + "  ");
	    }
	}
	*/
	return costMatrix;
    }
    
    
     public void newRandomNodes() {
	sNodes.clear();
	rNodes.clear();
	int maxDistance = 12;
	Random rand = new Random();
	for(int i = 0; i < nNodes; i++) {
	    sNodes.add(new Node("x" + (i+1), rand.nextInt(maxDistance), rand.nextInt(maxDistance)));
	    rNodes.add(new Node("y" + (i+1), rand.nextInt(maxDistance), rand.nextInt(maxDistance)));
	}
    }
 

    private double xyDistance(int x1, int x2, int y1, int y2) {
	return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    private void printStuff() {
	System.out.println("\nS NODES\n");
	for(Node s: sNodes) {
	    System.out.println(s);
	}
	System.out.println("\nR NODES\n");
	for(Node r: rNodes) {
	    System.out.println(r);
	}
	System.out.println("\nCOST MATRIX\n");
	for(int i = 0; i < costMatrix.length; i++) {
	    System.out.println("\n");
	    for(Cost c: costMatrix[i]) {
		System.out.print(c.val + "  ");
	    }
	}
	System.out.println("\n");
    }

    private ArrayList<MatchInfo> getFinalMatches(ArrayList<Cost> matches) {
	ArrayList<MatchInfo> finalMatches = new ArrayList<MatchInfo>(matches.size());
	for(Cost c: matches) {
	    MatchInfo mi = new MatchInfo(sNodes.get(c.i), rNodes.get(c.j), c.val);
	    finalMatches.add(mi);
	}
	return finalMatches;
    }
  
    public static void main(String[] args) {
	OptBottleneck obn = new OptBottleneck();
	obn.newRandomNodes();
	obn.run();
    }
    
}

class Cost {

    //The distance or cost value
    public double val;
    public int i; //Row
    public int j; //Column

    public Cost(double val, int i, int j) {
	this.val = val;
	this.i = i;
	this.j = j;
    }
    
    @Override
    public String toString() {
	return "Cost val: " + val + " row: " + i + " column: " + j;
    }
}