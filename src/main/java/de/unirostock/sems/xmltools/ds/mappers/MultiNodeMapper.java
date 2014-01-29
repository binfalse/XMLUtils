/**
 * 
 */
package de.unirostock.sems.xmltools.ds.mappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


/**
 * The Class MultiNodeMapper maps Strings to a list of nodes. Intended to map e.g. tag names => nodes. T is usually TreeNode or DocumentNode.
 *
 * @param <T> the generic type, e.g. TreeNode or DocumentNode
 * @see de.unirostock.sems.xmltools.ds.TreeNode
 * @see de.unirostock.sems.xmltools.ds.DocumentNode
 * @author Martin Scharm
 */
public class MultiNodeMapper<T>
{
	
	/** The mapper itself. */
	private HashMap<String, List<T>> mapper;
	
	/**
	 * Instantiate a new mapper.
	 */
	public MultiNodeMapper ()
	{
		mapper = new HashMap<String, List<T>> ();
	}
	
	/**
	 * Gets the known identifiers.
	 *
	 * @return the known identifiers
	 */
	public Set<String> getIds ()
	{
		return mapper.keySet ();
	}
	
	/**
	 * Adds a node.
	 *
	 * @param id the identifier
	 * @param node the node
	 */
	public void addNode (String id, T node)
	{
		List<T> nodes = mapper.get (id);
		if (nodes == null)
		{
			nodes =new ArrayList<T> ();
			mapper.put (id, nodes);
		}
		nodes.add (node);
	}
	
	/**
	 * Removes a node.
	 *
	 * @param id the identifier
	 * @param node the node
	 */
	public void rmNode (String id, T node)
	{
		List<T> nodes = mapper.get (id);
		if (nodes == null)
		{
			return;
		}
		nodes.remove (node);
	}
	
	/**
	 * Gets the nodes that are stored for a certain identifier.
	 *
	 * @param id the identifier
	 * @return the nodes
	 */
	public List<T> getNodes (String id)
	{
		return mapper.get (id);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		StringBuilder s = new StringBuilder (" ");
		for (String a : mapper.keySet ())
			s.append (a + " =>> " + mapper.get (a).toString () + "");
		return s.toString ();
	}
	
}
