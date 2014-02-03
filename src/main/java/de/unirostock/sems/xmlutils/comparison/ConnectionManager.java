/**
 * 
 */
package de.unirostock.sems.xmlutils.comparison;

import de.unirostock.sems.xmlutils.ds.TreeNode;



/**
 * The Interface ConnectionManager to manage connections.
 * 
 * @author Martin Scharm
 */
public interface ConnectionManager
{
	
	/**
	 * Check if the parents of two nodes are connected.
	 * 
	 * @param c
	 *          the connection
	 * @return true, if the parents of the nodes connected in c are also connected
	 */
	public abstract boolean parentsConnected (Connection c);
	
	
	/**
	 * Gets the connection for a certain node. Might return <code>null</code> if
	 * there is no connection.
	 * 
	 * @param node
	 *          the node of interest
	 * @return the connection of the node
	 */
	public abstract Connection getConnectionForNode (TreeNode node);
}
