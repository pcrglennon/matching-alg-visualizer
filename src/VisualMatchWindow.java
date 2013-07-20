import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * The "window" that stores the VisualMatchPanel and ConfigPanels
 *
 * This is the element that will pop up when the program is executed
 */
public class VisualMatchWindow implements ActionListener{

    private int numberNodes;
    private int animDelay;
   
    //Defaults
    private static final int DEFAULT_NUMBER_NODES = 4;
    private static final int DEFAULT_ANIM_DELAY = 1000;
    private static final int DEFAULT_MAX_DISTANCE = 20;

    private MatchingAlgorithms algs;

    private VisualMatchPanel vmPanel;
    
    private ConfigPanel configPanel;

    //Buttons
    private JPanel buttonPanel;
    private JButton newNodesB;
    private JButton onlineMatchB;
    private JButton randGreedyB;
    private JButton offlineMatchB;
    private JButton permB;
    private JButton mfMatchB;
    private JButton clearB;
    private JButton quitB;

    public VisualMatchWindow(int numberNodes, int animDelay) {
	this.numberNodes = numberNodes;
	this.animDelay = animDelay;
	algs = new MatchingAlgorithms(numberNodes, DEFAULT_MAX_DISTANCE);
	
	//Don't mess with this!  Doesn't make much sense to look at, but it works
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

    /**
     * This is the real graphical construction work, called in the actual
     * constructor.  This is separate so it may be called in a separate
     * thread, to run more efficiently.  I did this to go along with Swing
     * convention
     */
    public void createAndShowGUI() {
	//Client's screen size
	Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
	JFrame frame = new JFrame("Matching Algorithm Visualizer");
	frame.setSize(600,600);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//Place window near center of the client's screen
	frame.setLocation(screenSize.width/2 - 120, screenSize.height/2 - 200);

	//The main JPanel
	Container panel = frame.getContentPane();

	vmPanel = new VisualMatchPanel(animDelay, DEFAULT_MAX_DISTANCE);
	panel.add(vmPanel, BorderLayout.CENTER);

	configPanel = new ConfigPanel();
	panel.add(configPanel, BorderLayout.LINE_END);

	setupButtonPanel();
	panel.add(buttonPanel, BorderLayout.PAGE_END);

	frame.pack();
	frame.setVisible(true);
    }

    /**
     * Adds each of the buttons to the button panel, then adds the button panel
     */
    private void setupButtonPanel() {
	buttonPanel = new JPanel();
	buttonPanel.setLayout(new FlowLayout());
	
	newNodesB = new JButton("New Nodes");
	onlineMatchB = new JButton("Online Greedy Match");
	randGreedyB = new JButton("Rand. Greedy Match");
	offlineMatchB = new JButton("Offline Greedy Match");
	mfMatchB = new JButton("Optimal Offline Match");
	permB = new JButton("Permutation");
	clearB = new JButton("Clear");
	quitB = new JButton("Quit");

	onlineMatchB.setEnabled(false);
	randGreedyB.setEnabled(false);
	offlineMatchB.setEnabled(false);
	mfMatchB.setEnabled(false);
	permB.setEnabled(false);
	clearB.setEnabled(false);

	newNodesB.addActionListener(this);
	onlineMatchB.addActionListener(this);
	randGreedyB.addActionListener(this);
	offlineMatchB.addActionListener(this);
	mfMatchB.addActionListener(this);
	permB.addActionListener(this);
	clearB.addActionListener(this);
	quitB.addActionListener(this);

	buttonPanel.add(newNodesB);
	buttonPanel.add(onlineMatchB);
	buttonPanel.add(randGreedyB);
	//buttonPanel.add(offlineMatchB);
	//buttonPanel.add(mfMatchB);
	buttonPanel.add(permB);
	buttonPanel.add(clearB);
	buttonPanel.add(quitB);
    }

    /**
     * Listens for ActionEvents, and responds to them based on the event
     *
     * Overriden from ActionListener
     */ 
    @Override
    public void actionPerformed(ActionEvent e) {
	if(e.getSource().equals(newNodesB)) {
	    algs.setNumberNodes(configPanel.getNumberNodes());
	    algs.newRandomNodes();
	    vmPanel.setNewNodes(algs.getRNodes(), algs.getSNodes());
	    vmPanel.repaint();
	    onlineMatchB.setEnabled(true);
	    randGreedyB.setEnabled(true);
	    offlineMatchB.setEnabled(true);
	    mfMatchB.setEnabled(true);
	    permB.setEnabled(true);
	    clearB.setEnabled(true);
	}
	if(e.getSource().equals(onlineMatchB)) {
	    vmPanel.setAnimDelay(configPanel.getAnimDelay());
	    vmPanel.enableDrawDistance(configPanel.drawDistanceEnabled());
	    if(!vmPanel.onlineMatchDrawn) {
		vmPanel.drawOnlineGreedyMatch(algs.greedyOnlineMatch());
	    }
	}
	if(e.getSource().equals(randGreedyB)) {
	    algs.randomGreedyOnlineMatch();
	}
	if(e.getSource().equals(offlineMatchB)) {
	    vmPanel.setAnimDelay(configPanel.getAnimDelay());
	    vmPanel.enableDrawDistance(configPanel.drawDistanceEnabled());
	    if(!vmPanel.offlineMatchDrawn) {
		vmPanel.drawOfflineGreedyMatch(algs.greedyOfflineMatch());
	    }
	}
	if(e.getSource().equals(mfMatchB)) {
	    algs.runMaxFlowBP();
	} 
	if(e.getSource().equals(permB)) {
	    algs.runPermutationMatch();
	}
	if(e.getSource().equals(clearB)) {
	    vmPanel.clearAll();
	    onlineMatchB.setEnabled(false);
	    randGreedyB.setEnabled(false);
	    offlineMatchB.setEnabled(false);
	    mfMatchB.setEnabled(false);
	    permB.setEnabled(false);
	    clearB.setEnabled(false);
	}
	if(e.getSource().equals(quitB)) {
	    System.exit(0);
	}
    }

    /**
     * Allows for command-line arguments for numberNodes and animDelay
     *
     * If no arguments are passed, use defaults 
     */
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