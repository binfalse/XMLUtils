/**
 * 
 */
package de.unirostock.sems.xmlutils.ds;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.w3c.dom.Document;

import de.unirostock.sems.xmlutils.alg.SemsWeighter;
import de.unirostock.sems.xmlutils.alg.Weighter;
import de.unirostock.sems.xmlutils.ds.mappers.MultiNodeMapper;
import de.unirostock.sems.xmlutils.ds.mappers.NodeMapper;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;



/**
 * The Class TreeDocument representing hierarchically structured content.
 * 
 * @author Martin Scharm
 */
public class TreeDocument
{
	
	/** The root node. */
	private DocumentNode									root;
	
	/** The id mapper. */
	private NodeMapper<DocumentNode>			idMapper;
	
	/** The XPath mapper. */
	private NodeMapper<TreeNode>					pathMapper;
	
	/** The hash mapper. */
	private MultiNodeMapper<TreeNode>			hashMapper;
	
	/** The tag mapper. */
	private MultiNodeMapper<DocumentNode>	tagMapper;
	
	/** The list of text nodes. */
	private List<TextNode>								textNodes;
	
	/** The ordered flag. */
	private boolean												ordered;
	
	/** The subtrees ordered by size. */
	private SortedSet<TreeNode>						subtreesBySize;
	
	/** The flag for unique identifiers. */
	private boolean												uniqueIds;
	
	/** The base URI. */
	private URI														baseUri;
	
	
	/**
	 * Initializes the fields.
	 */
	private void init ()
	{
		pathMapper = new NodeMapper<TreeNode> ();
		idMapper = new NodeMapper<DocumentNode> ();
		hashMapper = new MultiNodeMapper<TreeNode> ();
		tagMapper = new MultiNodeMapper<DocumentNode> ();
		subtreesBySize = new TreeSet<TreeNode> (
			new TreeNodeComparatorBySubtreeSize (true));
		textNodes = new ArrayList<TextNode> ();
	}
	
	
	/**
	 * Instantiates a new tree document.
	 * 
	 * @param d
	 *          the document
	 * @param baseUri
	 *          the base URI (needed to resolve relative imports)
	 * @throws XmlDocumentParseException
	 *           the xml document parse exception
	 */
	public TreeDocument (Document d, URI baseUri)
		throws XmlDocumentParseException
	{
		init ();
		Weighter w = new SemsWeighter (); // default sems weighter
		root = new DocumentNode (d.getDocumentElement (), null, this, w, 1, 0);
		ordered = true;
		uniqueIds = true;
		this.baseUri = baseUri;
	}
	
	
	/**
	 * Instantiates a new tree document.
	 * 
	 * @param d
	 *          the document
	 * @param w
	 *          the weighter to weight the nodes and subtrees
	 * @param baseUri
	 *          the base URI (needed to resolve relative imports)
	 * @throws XmlDocumentParseException
	 *           the xml document parse exception
	 */
	public TreeDocument (Document d, Weighter w, URI baseUri)
		throws XmlDocumentParseException
	{
		init ();
		if (w == null)
			w = new SemsWeighter (); // default sems weighter
		root = new DocumentNode (d.getDocumentElement (), null, this, w, 1, 0);
		ordered = true;
		uniqueIds = true;
		this.baseUri = baseUri;
	}
	
	
	/**
	 * Instantiates a new tree document.
	 * 
	 * @param d
	 *          the document
	 * @param baseUri
	 *          the base URI (needed to resolve relative imports)
	 * @param ordered
	 *          the ordered flag, if true we consider this tree to be ordered
	 * @throws XmlDocumentParseException
	 *           the xml document parse exception
	 */
	public TreeDocument (Document d, URI baseUri, boolean ordered)
		throws XmlDocumentParseException
	{
		init ();
		Weighter w = new SemsWeighter (); // default sems weighter
		root = new DocumentNode (d.getDocumentElement (), null, this, w, 1, 0);
		this.ordered = ordered;
		uniqueIds = true;
	}
	
	
	/**
	 * Instantiates a new tree document.
	 * 
	 * @param d
	 *          the document
	 * @param w
	 *          the weighter to weight the nodes and subtrees
	 * @param baseUri
	 *          the base URI (needed to resolve relative imports)
	 * @param ordered
	 *          the ordered
	 * @throws XmlDocumentParseException
	 *           the xml document parse exception
	 */
	public TreeDocument (Document d, Weighter w, URI baseUri, boolean ordered)
		throws XmlDocumentParseException
	{
		init ();
		if (w == null)
			w = new SemsWeighter (); // default sems weighter
		root = new DocumentNode (d.getDocumentElement (), null, this, w, 1, 0);
		this.ordered = ordered;
		uniqueIds = true;
		this.baseUri = baseUri;
	}
	
	
	/**
	 * Resort subtrees.
	 */
	public void resortSubtrees ()
	{
		// moved to sorted set -> re-sorting unnecessary..
		// Collections.sort (subtreesBySize, new TreeNodeComparatorBySubtreeSize
		// (true));
	}
	
	
	/**
	 * Integrate an node into this tree. This will update hash-/id-/tag-mappers
	 * etc.
	 * 
	 * @param node
	 *          the node to integrate
	 */
	public void integrate (TreeNode node, boolean recursively)
	{
		pathMapper.putNode (node.getXPath (), node);
		subtreesBySize.add (node);
		hashMapper.addNode (node.getSubTreeHash (), node);
		if (node.getType () == TreeNode.DOC_NODE)
		{
			DocumentNode dnode = (DocumentNode) node;
			tagMapper.addNode (dnode.getTagName (), dnode);
			String id = dnode.getId ();
			if (id != null)
			{
				if (idMapper.getNode (id) != null)
				{
					uniqueIds = false;
				}
				else
					idMapper.putNode (id, dnode);
			}
			if (recursively)
			{
				// integrate all children
				Vector<TreeNode> kids = dnode.getChildren ();
				for (TreeNode tn : kids)
					integrate (tn, recursively);
			}
		}
		else
			textNodes.add ((TextNode) node);
		node.doc = this;
	}
	
	
	/**
	 * Extract a node from this tree. Will delete its hash/id/xpath etc from
	 * corresponding mappers.
	 * 
	 * @param node
	 *          the node
	 */
	public void separate (TreeNode node, boolean recursively)
	{
		pathMapper.rmNode (node.getXPath ());
		subtreesBySize.remove (node);
		hashMapper.rmNode (node.getSubTreeHash (), node);
		
		if (node.getType () == TreeNode.DOC_NODE)
		{
			DocumentNode dnode = (DocumentNode) node;
			tagMapper.rmNode (dnode.getTagName (), dnode);
			if (dnode.getId () != null)
			{
				idMapper.rmNode (dnode.getId ());
			}
			if (recursively)
			{
				// separate all children
				Vector<TreeNode> kids = dnode.getChildren ();
				for (TreeNode tn : kids)
					separate (tn, recursively);
			}
		}
		else
			textNodes.remove (node);
		node.doc = null;
	}
	
	
	/**
	 * Gets the base URI.
	 * 
	 * @return the base URI
	 */
	public URI getBaseUri ()
	{
		return baseUri;
	}
	
	
	/**
	 * Are occurring IDs unique?.
	 * 
	 * @return true, if all IDs are unique
	 */
	public boolean uniqueIds ()
	{
		return uniqueIds;
	}
	
	
	/**
	 * Resets all modifications.
	 */
	public void resetAllModifications ()
	{
		root.resetModifications ();
	}
	
	
	/**
	 * Gets the root node.
	 * 
	 * @return the root node
	 */
	public DocumentNode getRoot ()
	{
		return root;
	}
	
	
	/**
	 * Gets the number of nodes in this document.
	 * 
	 * @return the number nodes
	 */
	public int getNumNodes ()
	{
		return root.getSizeSubtree () + 1;
	}
	
	
	/**
	 * Gets the tree weight. (equals the weight of the root node)
	 * 
	 * @return the tree weight
	 */
	public double getTreeWeight ()
	{
		return root.getWeight ();
	}
	
	
	/**
	 * Gets all text nodes.
	 * 
	 * @return the text nodes
	 */
	public List<TextNode> getTextNodes ()
	{
		return textNodes;
	}
	
	
	/**
	 * Gets the nodes sharing a certain tag name. May return null if there is no
	 * such tag.
	 * 
	 * @param tag
	 *          the tag name to search for
	 * @return the nodes sharing this tag name
	 */
	public List<DocumentNode> getNodesByTag (String tag)
	{
		List<DocumentNode> nodes = tagMapper.getNodes (tag);
		if (nodes == null)
			return new Vector<DocumentNode> ();
		return tagMapper.getNodes (tag);
	}
	
	
	/**
	 * Gets the subtrees ordered by size, biggest first.
	 * 
	 * @return the subtrees by size
	 */
	public TreeNode[] getSubtreesBySize ()
	{
		TreeNode[] tmp = new TreeNode[subtreesBySize.size ()];
		subtreesBySize.toArray (tmp);
		return tmp;
	}
	
	
	/**
	 * Gets the nodes by hash. May return null if there is no such hash.
	 * 
	 * @param hash
	 *          the hash
	 * @return the nodes having this hash value
	 */
	public List<TreeNode> getNodesByHash (String hash)
	{
		return hashMapper.getNodes (hash);
	}
	
	
	/**
	 * Gets the node by id. May return null if there is no such id
	 * <strong>or</strong> if the id's in this document aren't unique.
	 * 
	 * @param id
	 *          the id
	 * @return the node having this id value
	 */
	public DocumentNode getNodeById (String id)
	{
		if (uniqueIds)
			return idMapper.getNode (id);
		return null;
	}
	
	
	/**
	 * Gets the node by XPath expression. Currently only XPath expressions
	 * computed by us are supported. A common use case is for example
	 * 
	 * <code>
	 * docB.getNodeByPath (nodeFromA.getXPath ());
	 * </code>
	 * 
	 * to search for a node at the same path in another document.
	 * 
	 * @param path
	 *          the path
	 * @return the node by path
	 */
	public TreeNode getNodeByPath (String path)
	{
		return pathMapper.getNode (path);
	}
	
	
	/**
	 * Get all known XPaths.
	 * 
	 * @return the occurring XPaths
	 */
	public Set<String> getOccurringXPaths ()
	{
		return pathMapper.getIds ();
	}
	
	
	/**
	 * Get all known identifiers.
	 * 
	 * @return the occurring identifiers
	 */
	public Set<String> getOccurringIds ()
	{
		if (uniqueIds)
			return idMapper.getIds ();
		return null;
	}
	
	
	/**
	 * Get all known tag names.
	 * 
	 * @return the occurring tags
	 */
	public Set<String> getOccurringTags ()
	{
		return tagMapper.getIds ();
	}
	
	
	/**
	 * Get all known hashes.
	 * 
	 * @return the occurring hashes
	 */
	public Set<String> getOccurringHashes ()
	{
		return hashMapper.getIds ();
	}
	
	
	/**
	 * Gets the node statistics as a map `tag name` => `nodes sharing this tag`.
	 * 
	 * @return the node stats
	 */
	public HashMap<String, Integer> getNodeStats ()
	{
		HashMap<String, Integer> tags = new HashMap<String, Integer> ();
		root.getNodeStats (tags);
		return tags;
	}
	
	
	/**
	 * Dump mech for debugging purposes.
	 * 
	 * @return the string to debug this object
	 */
	public String dump ()
	{
		return root.dump ("");
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		String s = root.toString () + " - " + ordered;
		s += "\n\n\n";
		// s += pathMapper.toString ();
		return s;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals (Object anObject)
	{
		if (this == anObject)
			return true;
		
		if (anObject instanceof TreeDocument)
		{
			DocumentNode thisNode = this.getRoot ();
			DocumentNode otherNode = ((TreeDocument) anObject).getRoot ();
			
			if (thisNode == null || otherNode == null)
				return thisNode == null && otherNode == null;
			
			return thisNode.getSubTreeHash ().equals (otherNode.getSubTreeHash ())
			// plus just to make sure... ;-)
				&& thisNode.getSizeSubtree () == otherNode.getSizeSubtree ();
		}
		
		return false;
	}
}
