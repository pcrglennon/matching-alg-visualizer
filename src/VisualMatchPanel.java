import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;

import javax.swing.*;

public class VisualMatchPanel extends JPanel {

    private int animDelay;
    private int maxDistance;
    private boolean drawDistance = true;

    private ArrayList<Node> rNodes;
    private ArrayList<Node> sNodes;

    private ArrayList<MatchInfo> onlineMatchesToDraw;
    private ArrayList<MatchInfo> offlineMatchesToDraw;
    
    /**
     * These keep track of whether online matches/offline matches have been drawn,
     * so the program will not try to create matches when the matches have already
     * been drawn in the panel.
     *
     * These are intitially false, and set to true after a matching has been made,
     * and reset to false when new nodes are set, or after the panel is cleared
     */
    public boolean onlineMatchDrawn;
    public boolean offlineMatchDrawn;

    /**
     * Setup the panel
     */
    public VisualMatchPanel(int animDelay, int maxDistance) {
	super();
	this.animDelay = animDelay;
	this.maxDistance = maxDistance;
	rNodes = new ArrayList<Node>();
	sNodes = new ArrayList<Node>();
	onlineMatchesToDraw = new ArrayList<MatchInfo>();
	offlineMatchesToDraw = new ArrayList<MatchInfo>();

	onlineMatchDrawn = offlineMatchDrawn = false;

	setBackground(Color.WHITE);
	setPreferredSize(new Dimension(maxDistance * 20, maxDistance * 20));
    }
    
    /**
     * Update the request and server nodes w/ the new lists
     */
    public void setNewNodes(ArrayList<Node> rNodes, ArrayList<Node> sNodes) {
	clearAll();
	this.rNodes = rNodes;
	this.sNodes = sNodes;
    }

    public void setAnimDelay(int animDelay) {
	this.animDelay = animDelay;
    }

    public void enableDrawDistance(boolean drawDistance) {
	this.drawDistance = drawDistance;
    }

    /**
     * Draw each online match one by one
     */
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

    /**
     * Draw each offline match one by one
     */
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

    /**
     * Clear all nodes and matches
     * 
     * Allow matches to be drawn again
     */
    public void clearAll() {
	rNodes.clear();
	sNodes.clear();
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
	for(Node servXY: sNodes) {
	    g.setColor(Color.DARK_GRAY);
	    g.fillRect(servXY.xPos * 20, servXY.yPos * 20, 20, 20);
	    g.setColor(Color.WHITE);
	    g.drawString("" + (sNodes.indexOf(servXY) + 1), (servXY.xPos*20) + 3, (servXY.yPos*20) + 17);
	}
	for(Node reqXY: rNodes) {
	    g.setColor(Color.RED);
	    g.fillRect(reqXY.xPos*20, reqXY.yPos*20, 20, 20);
	    g.setColor(Color.WHITE);
	    g.drawString("" + (rNodes.indexOf(reqXY) + 1), (reqXY.xPos*20) + 3, (reqXY.yPos*20) + 17);
	}
	g.setColor(Color.BLACK);
	//TODO - Wider Lines
	for(MatchInfo match: onlineMatchesToDraw) {
	    g.drawLine(match.rNode.xPos*20, match.rNode.yPos*20, match.sNode.xPos*20, match.sNode.yPos*20);
	    //If drawDistance is enabled, draw the distance between the nodes
	    //at the midpoint of the edge joining them
	    if(drawDistance) {
		g.drawString("" + match.distance, (((match.rNode.xPos*20) + (match.sNode.xPos*20)) / 2), (((match.rNode.yPos*20) + (match.sNode.yPos*20)) / 2));
	    }
	}
	g.setColor(Color.BLUE);
	for(MatchInfo match: offlineMatchesToDraw) {
	    g.drawLine(match.rNode.xPos*20, match.rNode.yPos*20, match.sNode.xPos*20, match.sNode.yPos*20);
	    if(drawDistance) {
		g.drawString("" + match.distance, (((match.rNode.xPos*20) + (match.sNode.xPos*20)) / 2), (((match.rNode.yPos*20) + (match.sNode.yPos*20)) / 2));
	    }
	}
    }
}