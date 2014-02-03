/**
 * 
 */
package de.unirostock.sems.xmlutils.alg;

import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TextNode;



/**
 * The Class Weighter to compute the weight of nodes and subtrees in a document.
 * 
 * @author Martin Scharm
 */
public abstract class Weighter
{
	
	/**
	 * Computes the weight of a DocumentNode.
	 * 
	 * @param node
	 *          the node
	 * @return the weight
	 */
	public abstract double getWeight (DocumentNode node);
	
	
	/**
	 * Computes the weight of a TextNode.
	 * 
	 * @param node
	 *          the node
	 * @return the weight
	 */
	public abstract double getWeight (TextNode node);
}
