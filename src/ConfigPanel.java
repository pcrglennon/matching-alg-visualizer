import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.*;

/**
 * Configuration Panel, allows user to set:
 * 1) Number of nodes to match
 * 2) The animation delay
 *
 * TODO - Graph size
 */
public class ConfigPanel extends JPanel {

    private NumericTextField numberNodesField;
    private NumericTextField animDelayField;
    private JCheckBox drawDistanceCB;

    /**
     * Constructor - initializes components 
     */
    public ConfigPanel() {
	super();
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	
	numberNodesField = new NumericTextField(1);
	numberNodesField.setText("3");
	
	animDelayField = new NumericTextField(4);
	animDelayField.setText("1000");

	drawDistanceCB = new JCheckBox();
	drawDistanceCB.setSelected(true);

	addComponentsToPanel();
    }

    /**
     * Use GridBagLayout to specify the location of each component
     */
    private void addComponentsToPanel() {
	setLayout(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();
	
	c.gridx = 0;
	c.gridy = 0;
	add(new JLabel(" # Nodes "), c);

	c.gridx = 1;
	c.gridy = 0;
	add(numberNodesField, c);
	
	c.gridx = 1;
	c.gridy = 1;
	c.gridwidth = 2;
	add(Box.createVerticalStrut(10));
	
	c.gridwidth = 1;
	c.gridx = 0;
	c.gridy = 2;
	add(new JLabel(" Anim. Delay "), c);

	c.gridx = 1;
	c.gridy = 2;
	add(animDelayField, c);

	c.gridx = 1;
	c.gridy = 3;
	c.gridwidth = 2;
	add(Box.createVerticalStrut(10));

	c.gridwidth = 1;
	c.gridx = 0;
	c.gridy = 4;
	add(new JLabel(" Distances "), c);
	
	c.gridx = 1;
	c.gridy = 4;
	add(drawDistanceCB, c);
    }

    /**
     * Return the value of numberNodesField (or 0 if field is empty)
     * Used by VisualMatchWindow, so it knows how many nodes to lay out
     */
    public int getNumberNodes() {
	int toReturn;
	try {
	    toReturn = Integer.parseInt(numberNodesField.getText());
	} catch (Exception e) {
	    toReturn = 0;
	}
	return toReturn;
    }

    /**
     * Similar to getNumberNodes, for animation delay
     */
    public int getAnimDelay() {
	int toReturn;
	try {
	    toReturn = Integer.parseInt(animDelayField.getText());
	} catch (Exception e) {
	    toReturn = 0;
	}
	return toReturn;
    }

    /**
     * Returns the value of the draw distance enabled checkbox
     */
    public boolean drawDistanceEnabled() {
	return drawDistanceCB.isSelected();
    }

}