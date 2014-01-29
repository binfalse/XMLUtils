/**
 * 
 */
package de.unirostock.sems.xmltools.comparison;

import de.unirostock.sems.xmltools.ds.TreeNode;


/**
 * @author martin
 *
 */
public interface Connection
{
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

	
	public void setWeight (double u);
	public double getWeight ();
	public void scaleWeight (double u);
	public void addWeight (double u);
	
}
