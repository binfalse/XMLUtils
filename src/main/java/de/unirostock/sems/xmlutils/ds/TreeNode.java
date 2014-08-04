/**
 * 
 */
package de.unirostock.sems.xmlutils.ds;

import java.util.HashMap;
import java.util.List;

import org.jdom2.Element;

import de.unirostock.sems.xmlutils.comparison.Connection;
import de.unirostock.sems.xmlutils.comparison.ConnectionManager;



/**
 * The abstract class TreeNode representing a node in a document tree.
 * 
 * @author Martin Scharm
 */
public abstract class TreeNode
{
	
	/** The node tag name for text nodes, as we will use it in XPath expressions. */
	public static final String	TEXT_TAG				= "text()";
	
	/** UNCHANGED => node hasn't changes. */
	public static final int			UNCHANGED				= 0;
	
	/** UNMAPPED => node wasn't mapped. */
	public static final int			UNMAPPED				= 1;
	
	/** MOVED => node has moves. */
	public static final int			MOVED						= 2;
	
	/** MODIFIED => node was modified. */
	public static final int			MODIFIED				= 4;
	
	/** SUB_MODIFIED => the corresponding subtree was modified. */
	public static final int			SUB_MODIFIED		= 8;
	
	/** COPIED => node was copied. */
	public static final int			COPIED					= 16;
	
	/** GLUED => node was glued. */
	public static final int			GLUED						= 32;
	
	/** KIDSSWAPPED => the sequence of kids of this node were altered. */
	public static final int			KIDSSWAPPED			= 64;
	
	/** SWAPPEDKID => this is a swapped kid. */
	public static final int			SWAPPEDKID			= 128;
	
	/** SUBTREEUNMAPPED => the whole subtree is unmapped. */
	public static final int			SUBTREEUNMAPPED	= 256;
	
	/**
	 * DOC_NODE this is a DocumentNode.
	 * 
	 * @see de.unirostock.sems.xmlutils.ds.DocumentNode
	 */
	public static final int			DOC_NODE				= 1;
	
	/**
	 * TEXT_NODE this is a TextNode.
	 * 
	 * @see de.unirostock.sems.xmlutils.ds.TextNode
	 */
	public static final int			TEXT_NODE				= 2;
	
	/** The current modification state. */
	protected int								modified;
	
	/** The node type. */
	protected int								type;
	
	/** The XPath of this node. */
	protected String						xPath;
	
	/** The parent node. */
	protected DocumentNode			parent;
	
	/** The corresponding document. */
	protected TreeDocument			doc;
	
	/** The level in the tree document. */
	protected int								level;
	
	/** The hash of this single node */
	protected String						ownHash;
	
	
	/**
	 * Instantiates a new tree node.
	 * 
	 * @param type
	 *          the node type
	 * @param parent
	 *          the parent node
	 * @param doc
	 *          the corresponding document
	 * @param level
	 *          the level in the tree
	 */
	public TreeNode (int type, DocumentNode parent, TreeDocument doc, int level)
	{
		this.doc = doc;
		this.type = type;
		this.parent = parent;
		this.modified = UNCHANGED;
		this.level = level;
	}
	
	
	/**
	 * Returns the level of this node in its tree. Root has level 0.
	 * 
	 * @return the level
	 */
	public int getLevel ()
	{
		return level;
	}
	
	
	/**
	 * Returns the modification state.
	 * 
	 * @return the modification
	 */
	public int getModification ()
	{
		return modified;
	}
	
	
	/**
	 * Remove a modification.
	 * 
	 * @param mod
	 *          the modification
	 */
	public void rmModification (int mod)
	{
		this.modified &= ~mod;
	}
	
	
	/**
	 * Add a modification.
	 * 
	 * @param mod
	 *          the modification
	 */
	public void addModification (int mod)
	{
		this.modified |= mod;
	}
	
	
	/**
	 * Sets the modification.
	 * 
	 * @param mod
	 *          the new modification
	 */
	public void setModification (int mod)
	{
		this.modified = mod;
	}
	
	
	/**
	 * Checks for a certain modification.
	 * 
	 * @param mod
	 *          the modification
	 * @return true, if this node has modification mod
	 */
	public boolean hasModification (int mod)
	{
		return (this.modified & mod) > 0;
	}
	
	
	/**
	 * Gets the node type.
	 * @see #DOC_NODE
	 * @see #TEXT_NODE
	 * 
	 * @return the node type
	 */
	public int getType ()
	{
		return type;
	}
	
	
	/**
	 * Gets the parent node.
	 * 
	 * @return the parent node
	 */
	public DocumentNode getParent ()
	{
		return parent;
	}
	
	
	/**
	 * Gets the XPath expression corresponding to this node.
	 * 
	 * @return the XPath to the node
	 */
	public String getXPath ()
	{
		return xPath;
	}
	
	
	/**
	 * Checks if this is the root of the tree.
	 * 
	 * @return true, if this is root
	 */
	public boolean isRoot ()
	{
		return parent == null;
	}
	
	
	/**
	 * Resets all modifications.
	 */
	public void resetModifications ()
	{
		this.modified = UNCHANGED;
		if (type == DOC_NODE)
		{
			List<TreeNode> kids = ((DocumentNode) this).getChildren ();
			for (TreeNode kid : kids)
				kid.resetModifications ();
		}
	}
	
	
	/**
	 * Gets the corresponding document.
	 * 
	 * @return the document
	 */
	public TreeDocument getDocument ()
	{
		return doc;
	}
	
	
	/**
	 * Checks if the network of two nodes differs.
	 * 
	 * @param tn
	 *          the node in another tree document
	 * @param conMgmr
	 *          the connection manager
	 * @param c
	 *          the connection
	 * @return true, if parents of these nodes are <strong>not</strong> connected.
	 */
	public boolean networkDiffers (TreeNode tn, ConnectionManager conMgmr,
		Connection c)
	{
		DocumentNode p = getParent ();
		DocumentNode tnp = tn.getParent ();
		
		// both root?
		if (p == null && tnp == null)
			return false;
		
		// one root?
		if (p == null || tnp == null)
			return true;
		
		// parents connected and same child no.?
		if (!conMgmr.parentsConnected (c))
		{
			if (p != null)
				p.addModification (SUB_MODIFIED);
			if (tnp != null)
				tnp.addModification (SUB_MODIFIED);
			return true;
		}
		
		if (p.getNoOfChild (this) != tnp.getNoOfChild (tn))
		{
			p.addModification (KIDSSWAPPED);
			tnp.addModification (KIDSSWAPPED);
			addModification (SWAPPEDKID);
			tn.addModification (SWAPPEDKID);
		}
		
		return false;
	}
	
	
	/**
	 * Check if content between two nodes differs. Just compares the nodes,
	 * neglects everything else of the tree (e.g. network, kids etc).
	 * 
	 * @param tn
	 *          the other node to compare
	 * @return true, if nodes differ
	 */
	public boolean contentDiffers (TreeNode tn)
	{
		if (type != tn.type)
			return true;
		return !ownHash.equals (tn.ownHash);
	}
	
	
	/**
	 * Gets the weight of this node.
	 * 
	 * @return the weight
	 */
	public abstract double getWeight ();
	
	
	/**
	 * Gets the hash of this single node (w/o its subtree).
	 * 
	 * @return the hash of the node
	 */
	public abstract String getOwnHash ();
	
	
	/**
	 * Gets the calculated hash of the subtree rooted in this node, in TextNodes
	 * it equals the own hash.
	 * 
	 * @return the hash of the current subtree
	 */
	public abstract String getSubTreeHash ();
	
	
	/**
	 * Evaluate the modifications of this node. Just useful for tree comparisons.
	 * 
	 * @param conMgmr
	 *          the connection manager
	 * @return true, if node was changed
	 */
	public abstract boolean evaluate (ConnectionManager conMgmr);
	
	
	/**
	 * Dump this node. Just for debugging purposes..
	 * 
	 * @param prefix
	 *          the prefix for a line (indention)
	 * @return the produced dump
	 */
	public abstract String dump (String prefix);
	
	
	/**
	 * Attaches the subtree rooted in this node to the node parent.
	 * Recursively attaches its children. Will fail for
	 * 
	 * <code>
	 * parent == null && this.getType () == TreeNode.TEXT_NODE
	 * </code>
	 * 
	 * That means a text node cannot become root.
	 * 
	 * @param parent
	 *          the parent element which will root this node. If null, this node
	 *          will be root in the document
	 * @return the sub doc
	 */
	public abstract Element getSubDoc (Element parent);
	
	
	/**
	 * Re-setup the document structure downwards. (e.g. recompute XPaths etc.)
	 * 
	 * @param doc
	 *          the document this node corresponds to
	 * @param numChild
	 *          the child number of this node
	 */
	protected abstract void reSetupStructureDown (TreeDocument doc, int numChild);
	
	
	/**
	 * Re-setup the document structure upwards. (e.g. recompute hashes etc.)
	 */
	protected abstract void reSetupStructureUp ();
	
	
	/**
	 * Gets the node statistics of the subtree rooted in this node: tagname =>
	 * number nodes having this tag name.
	 * 
	 * @param map
	 *          the map to write our statistics to
	 */
	public abstract void getNodeStats (HashMap<String, Integer> map);
}
