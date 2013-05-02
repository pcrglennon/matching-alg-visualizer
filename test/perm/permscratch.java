



private PermutationMatch pm;

private int[] pmWins = {0,0,0};

/**  -------------------------PERMUTATION-------------------------  */

public ArrayList<MatchInfo> runPermutationMatch() {
    pm = new PermutationMatch(spots, destinations);
    ArrayList<MatchInfo> pmMatching = pm.runAlgorithm();
    return pmMatching;
}

double sumAvgPMCost = 0.0;
double sumPMBneckCost = 0.0;

ArrayList<MatchInfo> pmMatching = runPermutationMatch();
double[] pmMatchingCosts = getCosts(pmMatching);
sumAvgPMCost += pmMatchingCosts[0];
sumPMBneckCost += pmMatchingCosts[1];

determineWinners(obMatchingCosts, mfMatchingCosts, pmMatchingCosts, na1MatchingCosts, na2MatchingCosts, goMatchingCosts);

sb.append("\n\nPERMUTATION AVG COST >> " + fourDecimals.format(pmMatchingCosts[0]));
sb.append("   ||   PERMUATION WORST COST >> " + fourDecimals.format(pmMatchingCosts[1]));

sb.append("\n-------------------------Avg. PERMUTATION Cost >> " + Double.valueOf(fourDecimals.format(sumAvgPMCost/numRuns)));

sb.append("\n-------------------------Avg. PERMUTATION Bottleneck Cost >> " + Double.valueOf(fourDecimals.format(sumPMBneckCost/numRuns)));

sb.append("\n\nPERMUTATION WINS " + "\n\nAVERAGE >> " + pmWins[0] + "\nWORST >> " + pmWins[1] + "\nBEST >> " + pmWins[2]);

private void determineWinners(double[] obCosts, double[] mfCosts, double[] pmCosts, double[] na1Costs, double[] na2Costs, double[] goCosts) {
    determineWinner(obCosts[0], mfCosts[0], pmCosts[0], na1Costs[0], na2Costs[0], goCosts[0], 0);
    determineWinner(obCosts[1], mfCosts[1], pmCosts[0], na1Costs[1], na2Costs[1], goCosts[1], 1);
    determineWinner(obCosts[2], mfCosts[2], pmCosts[0], na1Costs[2], na2Costs[2], goCosts[2], 2);
}

private void determineWinner(double obCost, double mfCost, double pmCost, double na1Cost, double na2Cost, double goCost, int type) {
    double avgWinner = Math.min(Math.min(Math.min(obCost, mfCost), Math.min(pmCost, na1Cost)), Math.min(na2Cost, goCost));
    if(avgWinner == obCost)
	obWins[type]++;
    if(avgWinner == mfCost)
	mfWins[type]++;
    if(avgWinner == pmCost)
	pmWins[type]++;
    if(avgWinner == na1Cost)
	na1Wins[type]++;
    if(avgWinner == na2Cost)
	na2Wins[type]++;
    if(avgWinner == goCost)
	goWins[type]++;
}