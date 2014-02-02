/**
 * 
 */
package de.unirostock.sems.xmlutils.ds;

import java.util.Comparator;


/**
 * The Class TreeNodeComparatorBySubtreeSize to compare sub-trees below nodes by size.
 *
 * @author Martin Scharm
 */
public class TreeNodeComparatorBySubtreeSize implements Comparator<TreeNode>
{
	
	/** reverse the comparison result? */
	private int reverse;
	
	/**
	 * Instantiates a new comparator sorting subtrees ascending.
	 */
	public TreeNodeComparatorBySubtreeSize ()
	{
		reverse = 1;
	}
	
	/**
	 * Instantiates a new comparator. If reverse is <code>true</code> subtrees are sorted in descending order, otherwise ascending.
	 *
	 * @param reverse the reverse
	 */
	public TreeNodeComparatorBySubtreeSize (boolean reverse)
	{
		if (reverse)
			this.reverse = -1;
		else
			this.reverse = 1;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare (TreeNode o1, TreeNode o2)
	{
		return reverse * privateCompare (o1, o2);
	}
	
	/**
	 * really compares the nodes.
	 *
	 * @param o1 node one
	 * @param o2 node two
	 * @return the ascending order
	 */
	private int privateCompare (TreeNode o1, TreeNode o2)
	{
		// textnodes are always the smallest nodes
		if (o2.getType () == TreeNode.TEXT_NODE)
			return 1;
		if (o1.getType () == TreeNode.TEXT_NODE)
			return -1;
		
		// get the subtree sizes
		int sub1 = ((DocumentNode) o1).getSizeSubtree ();
		int sub2 = ((DocumentNode) o2).getSizeSubtree ();
		
		// first based on subtree
		if (sub1 < sub2)
			return -1;
		if (sub1 > sub2)
			return 1;
		
		int a1 = ((DocumentNode) o1).getAttributes ().size (), a2 = ((DocumentNode) o2).getAttributes ().size ();
		
		// if that equals, compare number of arguments
		if (a1 < a2)
			return -1;
		if (a1 > a2)
			return 1;
		// ok ok, they have equal priority...
		return 0;
	}
	
}
