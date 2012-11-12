import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class VisualMatchWindow implements ActionListener{

    private int numberNodes;
    private int animDelay;
    
    private static final int DEFAULT_NUMBER_NODES = 5;
    private static final int DEFAULT_ANIM_DELAY = 1000;
    private static final int DEFAULT_MAX_DISTANCE = 20;

    private MatchingAlgorithms algs;

    private VisualMatchPanel vmPanel;

    private JPanel buttonPanel;
    private JButton newNodesB;
    private JButton onlineMatchB;
    private JButton offlineMatchB;
    private JButton clearB;
    private JButton quitB;

    public VisualMatchWindow(int numberNodes, int animDelay) {
	this.numberNodes = numberNodes;
	this.animDelay = animDelay;
	algs = new MatchingAlgorithms(numberNodes, DEFAULT_MAX_DISTANCE);
	
	SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    createAndShowGUI();
		}
	    });
    }

    public VisualMatchWindow() {
	this(DEFAULT_NUMBER_NODES, DEFAULT_ANIM_DELAY);
    }
    
    public VisualMatchWindow(int numberNodes) {
	this(numberNodes, DEFAULT_ANIM_DELAY);
    }

    public void createAndShowGUI() {
	Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
	JFrame frame = new JFrame("Matching Algorithm Visualizer");
	frame.setSize(600,600);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setLocation(screenSize.width/2 - 120, screenSize.height/2 - 200);

	Container panel = frame.getContentPane();

	vmPanel = new VisualMatchPanel(animDelay, DEFAULT_MAX_DISTANCE);
	panel.add(vmPanel, BorderLayout.CENTER);

	setupButtonPanel();
	panel.add(buttonPanel, BorderLayout.PAGE_END);

	frame.pack();
	frame.setVisible(true);
    }

    private void setupButtonPanel() {
	buttonPanel = new JPanel();
	buttonPanel.setLayout(new FlowLayout());
	
	newNodesB = new JButton("New Nodes");
	onlineMatchB = new JButton("Online Greedy Match");
	offlineMatchB = new JButton("Offline Greedy Match");
	clearB = new JButton("Clear");
	quitB = new JButton("Quit");

	onlineMatchB.setEnabled(false);
	offlineMatchB.setEnabled(false);
	clearB.setEnabled(false);

	newNodesB.addActionListener(this);
	onlineMatchB.addActionListener(this);
	offlineMatchB.addActionListener(this);
	clearB.addActionListener(this);
	quitB.addActionListener(this);

	buttonPanel.add(newNodesB);
	buttonPanel.add(onlineMatchB);
	buttonPanel.add(offlineMatchB);
	buttonPanel.add(clearB);
	buttonPanel.add(quitB);
    }

    public void actionPerformed(ActionEvent e) {
	if(e.getSource() == newNodesB) {
	    algs.newRandomNodes();
	    vmPanel.setNewNodes(algs.getReqNodes(), algs.getServNodes());
	    vmPanel.repaint();
	    onlineMatchB.setEnabled(true);
	    offlineMatchB.setEnabled(true);
	    clearB.setEnabled(true);
	}
	if(e.getSource() == onlineMatchB) {
	    if(!vmPanel.onlineMatchDrawn) {
		vmPanel.drawOnlineGreedyMatch(algs.greedyOnlineMatch());
	    }
	}
	if(e.getSource() == offlineMatchB) {
	    if(!vmPanel.offlineMatchDrawn) {
		vmPanel.drawOfflineGreedyMatch(algs.greedyOfflineMatch());
	    }
	}
	if(e.getSource() == clearB) {
	    vmPanel.clearAll();
	    onlineMatchB.setEnabled(false);
	    offlineMatchB.setEnabled(false);
	    clearB.setEnabled(false);
	}
	if(e.getSource() == quitB) {
	    System.exit(0);
	}
    }

    public static void main(String[] args) {
	int numberNodes, animDelay;
	
	if(args.length == 0) { //No arguments passed in
	    new VisualMatchWindow();
	} else if (args.length == 1) {
	    try { //Only one argument passed in
		numberNodes = Integer.parseInt(args[0]);
		new VisualMatchWindow(numberNodes);
	    } catch(Exception e) {
		System.exit(0);
	    }
	} else { //Both arguments present
	    try {
		numberNodes = Integer.parseInt(args[0]);
		animDelay = Integer.parseInt(args[1]);
		new VisualMatchWindow(numberNodes, animDelay);
	    } catch(Exception e) {
		System.exit(0);
	    }
	}
    }
}