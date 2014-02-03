/**
 * 
 */
package de.unirostock.sems.xmlutils.ds;

import java.util.Comparator;



/**
 * The Class NodeDistanceComparator to compare node distances.
 * 
 * @author Martin Scharm
 */
public class NodeDistanceComparator
	implements Comparator<NodeDistance>
{
	
	/** distances in reverse order. */
	private int	reverse;
	
	
	/**
	 * Instantiates a new node distance comparator sorting the distances
	 * ascending.
	 */
	public NodeDistanceComparator ()
	{
		reverse = 1;
	}
	
	
	/**
	 * Instantiates a new node distance comparator. If reverse is
	 * <code>true</code> subtrees are sorted in descending order, otherwise
	 * ascending.
	 * 
	 * @param reverse
	 *          the reverse
	 */
	public NodeDistanceComparator (boolean reverse)
	{
		if (reverse)
			this.reverse = -1;
		else
			this.reverse = 1;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare (NodeDistance o1, NodeDistance o2)
	{
		int ret = 0;
		if (o1.distance < o2.distance)
			ret = -1;
		if (o1.distance > o2.distance)
			ret = 1;
		
		return reverse * ret;
	}
}
