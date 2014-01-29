/**
 * 
 */
package de.unirostock.sems.xmltools.comparison;

import de.unirostock.sems.xmltools.ds.TreeNode;


/**
 * @author martin
 *
 */
public interface ConnectionManager
{
	public abstract boolean parentsConnected (Connection c);
	public abstract Connection getConnectionForNode (TreeNode node);
}
