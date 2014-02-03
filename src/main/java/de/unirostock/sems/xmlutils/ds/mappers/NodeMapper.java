/**
 * 
 */
package de.unirostock.sems.xmlutils.ds.mappers;

import java.util.HashMap;
import java.util.Set;



/**
 * The Class NodeMapper maps Strings to nodes. Intended to map unique labels
 * (e.g. node id => node). T is usually TreeNode or DocumentNode.
 * 
 * @param <T>
 *          the generic type, e.g. TreeNode or DocumentNode
 * @see de.unirostock.sems.xmlutils.ds.TreeNode
 * @see de.unirostock.sems.xmlutils.ds.DocumentNode
 * @see de.unirostock.sems.xmlutils.ds.TextNode
 * @author Martin Scharm
 */
public class NodeMapper<T>
{
	
	/** The mapper itself. */
	private HashMap<String, T>	mapper;
	
	
	/**
	 * Instantiate a new mapper.
	 */
	public NodeMapper ()
	{
		mapper = new HashMap<String, T> ();
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
	 * Inserts a new node.
	 * 
	 * @param id
	 *          the identifier
	 * @param node
	 *          the node
	 */
	public void putNode (String id, T node)
	{
		mapper.put (id, node);
	}
	
	
	/**
	 * Removes a node.
	 * 
	 * @param id
	 *          the identifier
	 */
	public void rmNode (String id)
	{
		mapper.remove (id);
	}
	
	
	/**
	 * Gets a node that is stored for a certain identifier.
	 * 
	 * @param id
	 *          the identifier
	 * @return the node
	 */
	public T getNode (String id)
	{
		return mapper.get (id);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
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
