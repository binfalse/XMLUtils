/**
 * 
 */
package de.unirostock.sems.xmlutils.comparison;

import de.unirostock.sems.xmlutils.ds.TreeNode;



/**
 * The Interface Connection representing a connection of nodes in trees.
 * 
 * @author Martin Scharm
 */
public interface Connection
{
	
	/**
	 * Gets the opposite of the given node in this connection. Thus, if this
	 * connection connects A and B: <code>getPartnerOf (A) = B</code>, and
	 * <code>getPartnerOf (B) = A</code>.
	 * 
	 * @param node
	 *          the node
	 * @return the partner of
	 */
	public TreeNode getPartnerOf (TreeNode node);
	
	
	/**
	 * Gets the corresponding node in tree a.
	 * 
	 * @return the node in tree a
	 */
	public TreeNode getTreeA ();
	
	
	/**
	 * Gets the corresponding node in tree b.
	 * 
	 * @return the node in tree b
	 */
	public TreeNode getTreeB ();
	
	
	/**
	 * Sets the weight of that connection. Thus, connections can be weighted
	 * differently.
	 * 
	 * @param w
	 *          the new weight
	 */
	public void setWeight (double w);
	
	
	/**
	 * Gets the weight of that connection.
	 * 
	 * @return the weight
	 */
	public double getWeight ();
	
	
	/**
	 * Scales the weight with an factor s.
	 * 
	 * @param s
	 *          the factor used to scale the weight
	 */
	public void scaleWeight (double s);
	
	
	/**
	 * Adds (+) a weight to the connection's weight.
	 * 
	 * @param s
	 *          the s
	 */
	public void addWeight (double s);
	
}
