/**
 * 
 */
package de.unirostock.sems.xmlutils.ds;

import java.util.HashMap;

import org.jdom2.Element;

import de.binfalse.bflog.LOGGER;
import de.binfalse.bfutils.GeneralTools;
import de.unirostock.sems.xmlutils.alg.Weighter;
import de.unirostock.sems.xmlutils.comparison.Connection;
import de.unirostock.sems.xmlutils.comparison.ConnectionManager;



/**
 * The Class TextNode representing text content inside a document.
 * 
 * @author Martin Scharm
 */
public class TextNode
	extends TreeNode
{
	
	/** The text stored in this node. */
	private String		text;
	
	/** The weight. */
	private double		weight;
	
	/** The weighter. */
	private Weighter	weighter;
	
	
	/**
	 * Copies a text node.
	 * 
	 * @param toCopy
	 *          the node to copy
	 * @param parent
	 *          the new parent or null if this is going to be root
	 * @param numChild
	 *          the number of that child among its siblings
	 */
	public TextNode (TextNode toCopy, DocumentNode parent, int numChild)
	{
		super (TreeNode.TEXT_NODE, parent, null, parent == null ? 0 : parent.level + 1);
		this.text = toCopy.text;
		
		// create xpath
		if (parent == null)
			xPath = "";
		else
			xPath = parent.getXPath ();
		xPath += "/" + TEXT_TAG + "[" + numChild + "]";
		
		ownHash = toCopy.ownHash;
		weight = toCopy.weight;
		weighter = toCopy.weighter;
	}
	
	
	/**
	 * Instantiates a new text node.
	 * 
	 * @param text
	 *          the text stored in this node
	 * @param parent
	 *          the parent node in the tree
	 * @param doc
	 *          the document
	 * @param numChild
	 *          the number of that child among its siblings
	 * @param w
	 *          the weighter
	 * @param level
	 *          the level in the tree
	 */
	public TextNode (String text, DocumentNode parent, TreeDocument doc,
		int numChild, Weighter w, int level)
	{
		super (TreeNode.TEXT_NODE, parent, doc, level);
		this.text = text;
		
		// create xpath
		if (parent == null)
			xPath = "";
		else
			xPath = parent.getXPath ();
		xPath += "/" + TEXT_TAG + "[" + numChild + "]";
		
		ownHash = GeneralTools.hash (text);
		
		weighter = w;
		weight = w.getWeight (this);
		
		doc.integrate (this, false);
	}
	
	
	/**
	 * Gets the text content of this node.
	 * 
	 * @return the text
	 */
	public String getText ()
	{
		return text;
	}
	
	
	/**
	 * Sets the text content of this node.
	 * <strong>Be Careful:</strong> since we have to recalculate all hashes this
	 * operation is quite expensive!
	 * 
	 * @param newText the text to be stored in this node 
	 */
	public void setText (String newText)
	{
		this.text = newText;
		reSetupStructureUp ();
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unirostock.sems.xmlutils.ds.TreeNode#evaluate(de.unirostock.sems.xmlutils
	 * .comparison.ConnectionManager)
	 */
	@Override
	public boolean evaluate (ConnectionManager conMgmr)
	{
		setModification (UNCHANGED);
		
		Connection con = conMgmr.getConnectionForNode (this);
		if (con == null || con.getPartnerOf (this) == null)
		{
			addModification (UNMAPPED | SUBTREEUNMAPPED);
			return true;
		}
		
		TreeNode partner = con.getPartnerOf (this);
		
		// changed?
		if (contentDiffers (partner))
			addModification (MODIFIED);
		// moved?
		if (networkDiffers (partner, conMgmr, con))
		{
			addModification (MOVED);
		}
		
		LOGGER.debug ("mod: ", modified, "(", xPath, ")");
		return (modified & (MODIFIED | MOVED | UNMAPPED)) != 0;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.xmlutils.ds.TreeNode#dump(java.lang.String)
	 */
	@Override
	public String dump (String prefix)
	{
		return prefix + xPath + " -> " + modified + "\n";
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unirostock.sems.xmlutils.ds.TreeNode#getSubDoc(org.w3c.dom.Document,
	 * org.w3c.dom.Element)
	 */
	@Override
	public Element getSubDoc (Element parent)
	{
		if (parent != null)
			parent.setText (text);
		return null;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unirostock.sems.xmlutils.ds.TreeNode#reSetupStructureDown(de.unirostock
	 * .sems.xmlutils.ds.TreeDocument, int)
	 */
	@Override
	protected void reSetupStructureDown (TreeDocument doc, int numChild)
	{
		// seperate this node
		if (this.doc != null)
			this.doc.separate (this, false);
		
		// looks like we need the doc argument?
		this.doc = doc;
		
		// recalculate the properties
		this.xPath = parent.xPath + "/" + TEXT_TAG + "[" + numChild + "]";
		this.level = parent.level + 1;
		
		// integrate into (new) doc
		if (this.doc != null)
			this.doc.integrate (this, false);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.xmlutils.ds.TreeNode#reSetupStructureUp()
	 */
	@Override
	protected void reSetupStructureUp ()
	{
		TreeDocument treeDoc = this.doc;
		if (this.doc != null)
			this.doc.separate (this, false);
		ownHash = GeneralTools.hash (text);

		if (treeDoc != null)
			treeDoc.integrate (this, false);
		
		weight = weighter.getWeight (this);
		
		if (parent != null)
			parent.reSetupStructureUp ();
		
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unirostock.sems.xmlutils.ds.TreeNode#getNodeStats(java.util.HashMap)
	 */
	@Override
	public void getNodeStats (HashMap<String, Integer> map)
	{
		Integer i = map.get (TEXT_TAG);
		if (i == null)
			map.put (TEXT_TAG, 1);
		else
			map.put (TEXT_TAG, i + 1);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.xmlutils.ds.TreeNode#getWeight()
	 */
	@Override
	public double getWeight ()
	{
		return weight;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.xmlutils.ds.TreeNode#getOwnHash()
	 */
	@Override
	public String getOwnHash ()
	{
		return ownHash;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.xmlutils.ds.TreeNode#getSubTreeHash()
	 */
	@Override
	public String getSubTreeHash ()
	{
		return ownHash;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		return "TEXT: " + weight + "\t(" + xPath + ")\t" + text;
	}


	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmlutils.ds.TreeNode#getTagName()
	 */
	@Override
	public String getTagName ()
	{
		return TEXT_TAG;
	}
	
	
	/**
	 * Gets the distance between the texts of two nodes. Here it is defined as the Levenshtein distance / max Levenshtein distance.
	 *
	 * @param cmp the node to compare
	 * @return the text distance
	 */
	public double getTextDistance (TextNode cmp)
	{
		double dist = GeneralTools.computeLevenshteinDistance (text, cmp.text);
		return dist / Math.max (text.length (), cmp.text.length ());
	}
}
