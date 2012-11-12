import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;

import javax.swing.*;

public class VisualMatchPanel extends JPanel {

    private int animDelay;
    private int maxDistance;

    private ArrayList<int[]> reqNodes;
    private ArrayList<int[]> servNodes;

    private ArrayList<MatchInfo> onlineMatchesToDraw;
    private ArrayList<MatchInfo> offlineMatchesToDraw;
    
    public boolean onlineMatchDrawn;
    public boolean offlineMatchDrawn;

    public VisualMatchPanel(int animDelay, int maxDistance) {
	super();
	this.animDelay = animDelay;
	this.maxDistance = maxDistance;
	reqNodes = new ArrayList<int[]>();
	servNodes = new ArrayList<int[]>();
	onlineMatchesToDraw = new ArrayList<MatchInfo>();
	offlineMatchesToDraw = new ArrayList<MatchInfo>();

	onlineMatchDrawn = offlineMatchDrawn = false;

	setBackground(Color.WHITE);
	setPreferredSize(new Dimension(maxDistance * 20, maxDistance * 20));
    }

    public void setNewNodes(ArrayList<int[]> reqNodes, ArrayList<int[]> servNodes) {
	clearAll();
	this.reqNodes = reqNodes;
	this.servNodes = servNodes;
    }

    public void drawOnlineGreedyMatch(final MatchInfo[] onlineMatches) {
	onlineMatchDrawn = true;
	Timer t = new Timer(animDelay, new ActionListener() {
		int matchIndex = 0;
		public void actionPerformed(ActionEvent e) {
		    if(matchIndex < onlineMatches.length) {
			onlineMatchesToDraw.add(onlineMatches[matchIndex]);
			repaint();
			matchIndex++;
		    } else {
			return;
		    }
		}
	    });
	t.start();
    }

    public void drawOfflineGreedyMatch(final MatchInfo[] offlineMatches) {
	offlineMatchDrawn = true;
	Timer t = new Timer(animDelay, new ActionListener() {
		int matchIndex = 0;
		public void actionPerformed(ActionEvent e) {
		    if(matchIndex < offlineMatches.length) {
			offlineMatchesToDraw.add(offlineMatches[matchIndex]);
			repaint();
			matchIndex++;
		    } else {
			return;
		    }
		}
	    });
	t.start();
    }

    public void clearAll() {
	reqNodes.clear();
	servNodes.clear();
	onlineMatchesToDraw.clear();
	offlineMatchesToDraw.clear();

	onlineMatchDrawn = offlineMatchDrawn = false;

	repaint();
    }

    public void paintComponent(Graphics g) {
	super.paintComponent(g);
	g.setFont(new Font("SansSerif", Font.BOLD, 20));

	g.setColor(Color.GRAY);
	//GRID
	for(int i = 0; i <= maxDistance; i++) {
	    g.drawLine(0, i*20, 400, i*20);
	}
	for(int i = 0; i <= maxDistance; i++) {
	    g.drawLine(i*20, 0, i*20, 400);
	}
	for(int[] servXY: servNodes) {
	    g.setColor(Color.DARK_GRAY);
	    g.fillRect(servXY[0] * 20, servXY[1] * 20, 20, 20);
	    g.setColor(Color.WHITE);
	    g.drawString("" + (servNodes.indexOf(servXY) + 1), (servXY[0]*20) + 3, (servXY[1]*20) + 17);
	}
	for(int[] reqXY: reqNodes) {
	    g.setColor(Color.RED);
	    g.fillRect(reqXY[0]*20, reqXY[1]*20, 20, 20);
	    g.setColor(Color.WHITE);
	    g.drawString("" + (reqNodes.indexOf(reqXY) + 1), (reqXY[0]*20) + 3, (reqXY[1]*20) + 17);
	}
	g.setColor(Color.BLACK);
	//TODO - Wider Lines
	for(MatchInfo match: onlineMatchesToDraw) {
	    g.drawLine(match.rNode[0]*20, match.rNode[1]*20, match.sNode[0]*20, match.sNode[1]*20);
	}
	g.setColor(Color.BLUE);
	for(MatchInfo match: offlineMatchesToDraw) {
	    g.drawLine(match.rNode[0]*20, match.rNode[1]*20, match.sNode[0]*20, match.sNode[1]*20);
	}
    }
}