import java.io.BufferedWriter;
import java.io.FileWriter;

import java.text.DecimalFormat;

import java.util.ArrayList;

public class GreedyNewAlgs {

    private NewAlgs algs;
    
    private int numRuns = 1000;
    
    private int a1AvgWins = 0;
    private int a2AvgWins = 0;
    private int goAvgWins = 0;
    private int a1WorstWins = 0;
    private int a2WorstWins = 0;
    private int goWorstWins = 0;
    private int a1BestWins = 0;
    private int a2BestWins = 0;
    private int goBestWins = 0;

    public GreedyNewAlgs() {
	algs = new NewAlgs();
    }

    public void testAlgs() {
	algs.initParkingModel();
	DecimalFormat fourD = new DecimalFormat("#.####");
	StringBuilder sb = new StringBuilder();
	//Run for whole range of D. Scores
	for(int i = 19; i < 54; i++) {
	    System.out.println("\n\n========THRESHOLD " + i + "========\n\n");
	    sb.append("\n\n========THRESHOLD " + i + "========\n\n");
	    algs.setThreshold(i);
	    double threshA1SumAvgCost = 0.0;
	    double threshA2SumAvgCost = 0.0;
	    double goSumAvgCost = 0.0;
	    resetWinCounts();
	    //For each threshold, run numRuns randomized arrival orders
	    for(int n = 0; n < numRuns; n++) {
		algs.newArrivalOrder();
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
		MatchInfo[] goMatches = algs.runGreedyPM();
		double goSumCost= 0.0;
		double goWorstCost = 0.0;
		double goBestCost = Double.MAX_VALUE;
		for(MatchInfo mi: goMatches) {
		    goSumCost += mi.distance;
		    if(mi.distance > goWorstCost)
			goWorstCost = mi.distance;
		    if(mi.distance < goBestCost)
			goBestCost = mi.distance;
		}
		double goAvgCost = goSumCost/goMatches.length;
		threshA1SumAvgCost += a1AvgCost;
		threshA2SumAvgCost += a2AvgCost;
		goSumAvgCost += goAvgCost;
		//Determine Winners
		determineAvgWinner(a1AvgCost, a2AvgCost, goAvgCost);
		determineWorstWinner(a1WorstCost, a2WorstCost, goWorstCost);
		determineBestWinner(a1BestCost, a2BestCost, goBestCost);
	    }
	    sb.append("\nA1 Avg. Match length for Thresh. " + i + " >> " + fourD.format(threshA1SumAvgCost/numRuns));
	    sb.append("\nA2 Avg. Match length for Thresh. " + i + " >> " + fourD.format(threshA2SumAvgCost/numRuns));
	    sb.append("\nGREEDY Avg. Match Length >> " + fourD.format(goSumAvgCost/numRuns) + "\n");
	    sb.append("\nA1 WINS:" + "\nAVERAGE >> " + a1AvgWins + "  BNECK >> " + a1WorstWins + "  BEST >> " + a1BestWins+"\n"); 
	    sb.append("\nA2 WINS:" + "\nAVERAGE >> " + a2AvgWins + "  BNECK >> " + a2WorstWins + "  BEST >> " + a2BestWins+"\n"); 
	    sb.append("\nGREEDY WINS:" + "\nAVERAGE >> " + goAvgWins + "  BNECK >> " + goWorstWins + "  BEST >> " + goBestWins+"\n"); 
	    sb.append("\n");
	}
	try {
	    FileWriter fstream = new FileWriter("newalgs_results/NewAlgsTallyResults.txt");
	    BufferedWriter out = new BufferedWriter(fstream);
	    out.write("Num Runs - " + numRuns + "\n\n\n");
	    out.write(sb.toString());
	    out.close();
	} catch (Exception e) {
	    
	}
	System.out.println("==========DONE===========");
    }

    private void determineAvgWinner(double a1Cost, double a2Cost, double goCost) {
	if(a1Cost == a2Cost && a1Cost == goCost && a2Cost == goCost) // Three-Way tie
	    return;
	double winner = Math.min(Math.min(a1Cost, a2Cost), goCost);
	if(winner == a1Cost)
	    a1AvgWins++;
	if(winner == a2Cost)
	    a2AvgWins++;
	if(winner == goCost)
	    goAvgWins++;
    }
    
    private void determineWorstWinner(double a1Cost, double a2Cost, double goCost) {	
	if(a1Cost == a2Cost && a1Cost == goCost && a2Cost == goCost) // Three-Way tie
	    return;
	double winner = Math.min(Math.min(a1Cost, a2Cost), goCost);
	if(winner == a1Cost)
	    a1WorstWins++;
	if(winner == a2Cost)
	    a2WorstWins++;
	if(winner == goCost)
	    goWorstWins++;
    }
    
    private void determineBestWinner(double a1Cost, double a2Cost, double goCost) {
	if(a1Cost == a2Cost && a1Cost == goCost && a2Cost == goCost) // Three-Way tie
	    return;
	double winner = Math.min(Math.min(a1Cost, a2Cost), goCost);
	if(winner == a1Cost)
	    a1BestWins++;
	if(winner == a2Cost)
	    a2BestWins++;
	if(winner == goCost)
	    goBestWins++;
    }

    private void resetWinCounts() {
	a1AvgWins = 0;
	a2AvgWins = 0;
	goAvgWins = 0;
	a1WorstWins = 0;
	a2WorstWins = 0;
	goWorstWins = 0;
	a1BestWins = 0;
	a2BestWins = 0;
	goBestWins = 0;
    }

    public static void main(String[] args) {
	GreedyNewAlgs test = new GreedyNewAlgs();
	test.testAlgs();
    }

}