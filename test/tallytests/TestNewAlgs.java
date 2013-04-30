import java.io.BufferedWriter;
import java.io.FileWriter;

import java.text.DecimalFormat;

import java.util.ArrayList;

public class TestNewAlgs {

    private NewAlgs algs;
    
    private int numRuns = 2500;

    public TestNewAlgs() {
	algs = new NewAlgs();
    }

    public void testAlgs() {
	algs.initParkingModel();
	DecimalFormat fourD = new DecimalFormat("#.####");
	StringBuilder sb = new StringBuilder();
	//Run for thresholds 29-34
	for(int i = 19; i < 51; i++) {
	    System.out.println("\n\n========THRESHOLD " + i + "========\n\n");
	    sb.append("\n\n========THRESHOLD " + i + "========\n\n");
	    algs.setThreshold(i);
	    double threshA1SumAvgCost = 0.0;
	    double threshA2SumAvgCost = 0.0;
	    double threshA1SumAvgWorstCost = 0.0;
	    double threshA2SumAvgWorstCost = 0.0;
	    double threshA1SumAvgBestCost = 0.0;
	    double threshA2SumAvgBestCost = 0.0;
	    //For each threshold, run numRuns randomized arrival orders
	    for(int n = 0; n < numRuns; n++) {
		MatchInfo[] algOneMatches = algs.runAlgOnePM();
		double a1SumCost = 0.0;
		double a1WorstCost = 0.0;
		double a1BestCost = Double.MAX_VALUE;
		for(MatchInfo mi: algOneMatches) {
		    a1SumCost += mi.distance;
		    if(mi.distance > a1WorstCost)
			a1WorstCost = mi.distance;
		    if(mi.distance < a1BestCost)
			a1BestCost = mi.distance;
		}
		double a1AvgCost = a1SumCost/algOneMatches.length;
		MatchInfo[] algTwoMatches = algs.runAlgTwoPM();
		double a2SumCost = 0.0;
		double a2WorstCost = 0.0;
		double a2BestCost = Double.MAX_VALUE;
		for(MatchInfo mi: algTwoMatches) {
		    a2SumCost += mi.distance;
		    if(mi.distance > a2WorstCost)
			a2WorstCost = mi.distance;
		    if(mi.distance < a2BestCost)
			a2BestCost = mi.distance;
		}
		double a2AvgCost = a2SumCost/algTwoMatches.length;
		threshA1SumAvgCost += a1AvgCost;
		threshA2SumAvgCost += a2AvgCost;
		threshA1SumAvgWorstCost += a1WorstCost;
		threshA1SumAvgBestCost += a1BestCost;
		threshA2SumAvgWorstCost += a2WorstCost;
		threshA2SumAvgBestCost += a2BestCost;
	    }
	    if(i == 19) {
		sb.append("\nGREEDY Avg. Match Length >> " + fourD.format(threshA1SumAvgCost/numRuns));
		sb.append("\nGREEDY Avg. Worst Match Length >> " + fourD.format(threshA1SumAvgWorstCost/numRuns));
		sb.append("\nGREEDY Avg. Best Match Length >> " + fourD.format(threshA1SumAvgBestCost/numRuns));
	    } else {
		sb.append("\nA1 Avg. Match length for Thresh. " + i + " >> " + fourD.format(threshA1SumAvgCost/numRuns));
		sb.append("\nA1 Avg. Worst Match length for Thresh. " + i + " >> " + fourD.format(threshA1SumAvgWorstCost/numRuns));
		sb.append("\nA1 Avg. Best Match length for Thresh. " + i + " >> " + fourD.format(threshA1SumAvgBestCost/numRuns)+ "\n");
		sb.append("\nA2 Avg. Match length for Thresh. " + i + " >> " + fourD.format(threshA2SumAvgCost/numRuns));
		sb.append("\nA2 Avg. Worst Match length for Thresh. " + i + " >> " + fourD.format(threshA2SumAvgWorstCost/numRuns));
		sb.append("\nA2 Avg. Best Match length for Thresh. " + i + " >> " + fourD.format(threshA2SumAvgBestCost/numRuns));
	    }
	    sb.append("\n\n");
	}
	try {
	    FileWriter fstream = new FileWriter("NewAlgsResults.txt");
	    BufferedWriter out = new BufferedWriter(fstream);
	    out.write("Num Runs - " + numRuns + "\n\n\n");
	    out.write(sb.toString());
	    out.close();
	} catch (Exception e) {
	    
	}
	System.out.println("==========DONE===========");
    }

    public static void main(String[] args) {
	TestNewAlgs test = new TestNewAlgs();
	test.testAlgs();
    }

}