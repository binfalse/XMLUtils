/**
 * 
 */
package de.unirostock.sems.xmltools.alg;

import de.unirostock.sems.xmltools.ds.DocumentNode;
import de.unirostock.sems.xmltools.ds.TextNode;


/**
 * The Class Weighter to compute the weight of nodes and subtrees in a document.
 *
 * @author Martin Scharm
 */
public abstract class Weighter
{
	
	/**
	 * Calculates the weight of a DocumentNode.
	 *
	 * @param node the node
	 * @return the weight
	 */
	public abstract double getWeight (DocumentNode node);
	
	/**
	 * Calculates the weight of a TextNode.
	 *
	 * @param node the node
	 * @return the weight
	 */
	public abstract double getWeight (TextNode node);
}
