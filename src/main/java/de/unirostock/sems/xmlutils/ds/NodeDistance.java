/**
 * 
 */
package de.unirostock.sems.xmlutils.ds;

/**
 * The Class NodeDistance to store a distance between two nodes.
 * 
 * @author Martin Scharm
 */
public class NodeDistance
{
	
	/** The nodes a and b. */
	public TreeNode	nodeA, nodeB;
	
	/** The distance. */
	public double		distance;
	
	
	/**
	 * Instantiates a new node distance.
	 * 
	 * @param nodeA
	 *          the node a
	 * @param nodeB
	 *          the node b
	 * @param distance
	 *          the distance
	 */
	public NodeDistance (TreeNode nodeA, TreeNode nodeB, double distance)
	{
		this.nodeA = nodeA;
		this.nodeB = nodeB;
		this.distance = distance;
	}
}
