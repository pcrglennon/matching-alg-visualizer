import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;

import javax.swing.*;

/**
 * Extension of a JPanel which displays a simple animation of nodes being matched
 *
 * The only real value of this is to display a Greedy Online matching.  It was made as
 * a tool to demonstrate this matching, and does not show the performance of other
 * matching algorithms.  However, if passed the final matching from any algorithm, it
 * could show that, though not the steps taken to achieve that matching
 */
public class VisualMatchPanel extends JPanel {

    /**
     * The speed of animation - the higher the slower
     *
     * User can configure this via the ConfigPanel
     */
    private int animDelay;
    private int maxDistance;
    /**
     * If this is set to true, the program will display the distance/cost of each match
     * along the line that represents the match
     *
     * User-configurable via the ConfigPanel
     */
    private boolean drawDistance = true;

    private ArrayList<Node> rNodes;
    private ArrayList<Node> sNodes;

    //Used for animation purposes
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
     *
     * The Timer section adds each match to be drawn on a delay.  When repaint() is
     * called, all matches in onlineMatchesToDraw are drawn
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
     *
     * The Timer section adds each match to be drawn on a delay.  When repaint() is
     * called, all matches in offlineMatchesToDraw are drawn
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

    /**
     * Overriden method from JPanel
     *
     * Does the graphical work
     */
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
	//Draw the server nodes, along with their indices
	//NOTE - double-digit numbers will be drawn in an ugly fashion
	for(Node servXY: sNodes) {
	    g.setColor(Color.DARK_GRAY);
	    g.fillRect(servXY.xPos * 20, servXY.yPos * 20, 20, 20);
	    g.setColor(Color.WHITE);
	    g.drawString("" + (sNodes.indexOf(servXY) + 1), (servXY.xPos*20) + 3, (servXY.yPos*20) + 17);
	}
	//Draw the request nodes and indices
	for(Node reqXY: rNodes) {
	    g.setColor(Color.RED);
	    g.fillRect(reqXY.xPos*20, reqXY.yPos*20, 20, 20);
	    g.setColor(Color.WHITE);
	    g.drawString("" + (rNodes.indexOf(reqXY) + 1), (reqXY.xPos*20) + 3, (reqXY.yPos*20) + 17);
	}
	g.setColor(Color.BLACK);
	//Draw the lines that indicate matches
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