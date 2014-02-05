/**
 * 
 */
package de.unirostock.sems.xmlutils.alg;

import java.util.List;

import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TextNode;
import de.unirostock.sems.xmlutils.ds.TreeNode;



/**
 * The Class XyWeighter to compute the weight of nodes and subtrees in a
 * document based on the publication of Cobena2002.
 * 
 * @author Martin Scharm
 */
public class XyWeighter
	extends Weighter
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unirostock.sems.xmldiff.algorithm.Weighter#getWeight(de.unirostock.sems
	 * .xmldiff.xml.DocumentNode)
	 */
	@Override
	public double getWeight (DocumentNode node)
	{
		// from Cobena2002
		double weight = 1;
		List<TreeNode> kids = node.getChildren ();
		for (TreeNode kid : kids)
			weight += kid.getWeight ();
		return weight;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unirostock.sems.xmldiff.algorithm.Weighter#getWeight(de.unirostock.sems
	 * .xmldiff.xml.TextNode)
	 */
	@Override
	public double getWeight (TextNode node)
	{
		return Math.log (node.getText ().length ()) + 1;
	}
	
}
