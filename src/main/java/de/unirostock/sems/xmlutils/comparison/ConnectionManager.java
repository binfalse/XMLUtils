/**
 * 
 */
package de.unirostock.sems.xmlutils.comparison;

import de.unirostock.sems.xmlutils.ds.TreeNode;


/**
 * @author martin
 *
 */
public interface ConnectionManager
{
	public abstract boolean parentsConnected (Connection c);
	public abstract Connection getConnectionForNode (TreeNode node);
}
