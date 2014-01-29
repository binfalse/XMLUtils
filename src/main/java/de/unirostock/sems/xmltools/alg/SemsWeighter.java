/**
 * 
 */
package de.unirostock.sems.xmltools.alg;

import de.unirostock.sems.xmltools.ds.DocumentNode;
import de.unirostock.sems.xmltools.ds.TextNode;



/**
 * The Class SemsWeighter to compute the weight of nodes and subtrees in a document.
 *
 * @author Martin Scharm
 */
public class SemsWeighter
	extends Weighter
{
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.Weighter#getWeight(de.unirostock.sems.xmldiff.xml.DocumentNode)
	 */
	@Override
	public double getWeight (DocumentNode node)
	{
		return node.getSizeSubtree () + 1;
	}
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.Weighter#getWeight(de.unirostock.sems.xmldiff.xml.TextNode)
	 */
	@Override
	public double getWeight (TextNode node)
	{
		return Math.log (node.getText ().length ()) + 1;
	}
	
}
